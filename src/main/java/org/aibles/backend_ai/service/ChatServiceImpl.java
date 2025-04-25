package org.aibles.backend_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.aibles.backend_ai.dto.request.ConversationRequest;
import org.aibles.backend_ai.dto.request.StartConversationRequest;
import org.aibles.backend_ai.dto.response.ConversationDto;
import org.aibles.backend_ai.exception.AIServiceException;
import org.aibles.backend_ai.exception.ResourceNotFoundException;
import org.aibles.backend_ai.service.chatstream.ChatStreamRegistry;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
public class ChatServiceImpl implements ChatService {

    private final OllamaChatModel chatModel;

    private final ConversationService conversationService;

    private final ChatStreamRegistry chatStreamRegistry;

    private static final String DEFAULT_MODEL = "qwen2.5:3b";

    public ChatServiceImpl(OllamaChatModel chatModel,
                           ConversationService conversationService,
                           ChatStreamRegistry chatStreamRegistry) {
        this.chatModel = chatModel;
        this.conversationService = conversationService;
        this.chatStreamRegistry = chatStreamRegistry;
    }

    @Override
    public Mono<ConversationDto> startConversation(StartConversationRequest request) {
        log.info("(startConversation)request : {}", request);
        return generateTitlePrompt(request.getPromptMessage())
                .flatMap(title -> conversationService.saveConversation(title, request.getModel()))
                .flatMap(conversation -> Mono.just(ConversationDto.builder()
                        .id(conversation.getId())
                        .title(conversation.getTitle())
                        .createdAt(conversation.getKey().getCreatedAt())
                         .build()))
                .onErrorMap(error -> {
                    log.error("Error starting conversation", error);
                    if (error instanceof AIServiceException) {
                        return error;
                    }
                    return new AIServiceException("Chat Service", "Failed to start conversation: " + error.getMessage(), error);
                });
    }

    private Mono<String> generateTitlePrompt(String promptMessage) {
        log.info("(generateTitlePrompt)promptMessage : {}", promptMessage);

        final String titlePrompt = """
                Generate a consice title (max 10 words) for a conversation based on this exchange: %s
                """;

        SystemMessage systemMessage = new SystemMessage(titlePrompt);
        UserMessage userMessage = new UserMessage(promptMessage);

        return chatModel.stream(new Prompt(List.of(systemMessage, userMessage),
                        OllamaOptions.builder().model(DEFAULT_MODEL).build()))
                .collectList()
                .map(chatResponses -> {
                    if (chatResponses.isEmpty()) {
                        return promptMessage;
                    }
                    final StringBuilder titleBuilder = new StringBuilder();
                    for (ChatResponse chatResponse : chatResponses) {
                        titleBuilder.append(chatResponse.getResult().getOutput().getText());
                    }
                    return titleBuilder.toString();
                })
                .onErrorMap(error -> {
                    log.error("Error generating title prompt", error);
                    return new AIServiceException("Ollama", "Failed to generate conversation title", error);
                });
    }

    @Override
    public Flux<String> streamAnswer(String conversationId, ConversationRequest request) {
        log.info("(streamAnswer)conversationId : {}, request : {}", conversationId, request);
        return conversationService.findConversationModel(conversationId)
                .onErrorResume(ResourceNotFoundException.class, error -> {
                    log.error("Conversation not found: {}", conversationId);
                    return Mono.error(error);
                })
                .flatMap(model ->
                        conversationService.generateContextWindow(conversationId, model, request.getPromptMessage())
                )
                .publishOn(Schedulers.boundedElastic())
                .flatMapMany(
                        modelContextWindow -> {
                            try {
                                Flux<String> chatStreamResp =
                                        chatStreamRegistry.getChatStreamService(modelContextWindow.getModel())
                                                .streamAnswer(modelContextWindow.getMessages());

                                Flux<String> chatStreamMulticast = chatStreamResp.publish().refCount(2);

                                chatStreamMulticast
                                        .reduce(new StringBuilder(), StringBuilder::append)
                                        .flatMap(answer ->
                                                conversationService.saveMessage(conversationId,
                                                        request.getPromptMessage(),
                                                        answer.toString())).subscribeOn(Schedulers.boundedElastic())
                                        .subscribe();

                                return chatStreamMulticast;
                            } catch (Exception e) {
                                log.error("Error streaming answer", e);
                                return Flux.error(new AIServiceException(
                                        modelContextWindow.getModel(),
                                        "Failed to stream AI response",
                                        e));
                            }
                        }
                )
                .onErrorResume(error -> {
                    if (error instanceof ResourceNotFoundException || error instanceof AIServiceException) {
                        return Flux.error(error);
                    }
                    log.error("Unexpected error in stream answer", error);
                    return Flux.error(new AIServiceException("Chat Service", "Failed to stream answer", error));
                });
    }
}
