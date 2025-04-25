package org.aibles.backend_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.aibles.backend_ai.dto.ModelContextWindow;
import org.aibles.backend_ai.dto.response.ConversationDto;
import org.aibles.backend_ai.dto.response.ConversationMessageDto;
import org.aibles.backend_ai.entity.*;
import org.aibles.backend_ai.repository.ConversationMetadataRepo;
import org.aibles.backend_ai.repository.ConversationRepository;
import org.aibles.backend_ai.repository.MessageRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;

    private final ConversationMetadataRepo conversationMetadataRepo;

    private final MessageRepository messageRepository;

    private static final int MAX_CONTEXT_MESSAGES = 15;

    private static final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MM-yyyy");

    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   ConversationMetadataRepo conversationMetadataRepo,
                                   MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.conversationMetadataRepo = conversationMetadataRepo;
        this.messageRepository = messageRepository;
    }

    @Override
    public Mono<Conversation> saveConversation(String title, String model) {
        log.info("(saveConversation)title : {}", title);

        final String monthBucket = YearMonth.from(LocalDateTime.now().atZone(ZoneOffset.UTC)).format(monthYearFormatter);
        Conversation conversation = new Conversation();
        ConversationKey conversationKey = new ConversationKey();
        conversationKey.setCreatedAt(Instant.now());
        conversationKey.setMonthBucket(monthBucket);
        conversation.setKey(conversationKey);

        conversation.setId(UUID.randomUUID().toString());
        conversation.setTitle(title);
        return conversationRepository.save(conversation).flatMap(savedConversation -> {
            ConversationMetadata metadata = ConversationMetadata
                    .builder()
                    .conversationId(conversation.getId())
                    .model(model)
                    .build();
            return conversationMetadataRepo.save(metadata).thenReturn(savedConversation);
        });
    }

    @Override
    public Flux<ConversationDto> findConversations(Instant toTine, int limit) {
        log.info("(findLatestConversations)toTime : {}, limit : {}", toTine, limit);
        final String monthBucket = YearMonth.from(LocalDateTime.now().atZone(ZoneOffset.UTC)).format(monthYearFormatter);
        return conversationRepository.findBy(monthBucket, toTine, limit).map(conversation ->
                ConversationDto.builder()
                        .id(conversation.getId())
                        .title(conversation.getTitle())
                        .createdAt(conversation.getKey().getCreatedAt())
                        .build()
        );
    }

    @Override
    public Mono<String> findConversationModel(String id) {
        log.info("(findConversation)id : {}", id);
        return conversationMetadataRepo.findById(id)
                .flatMap(metadata -> Mono.just(metadata.getModel()));
    }

    @Override
    public Flux<ConversationMessageDto> findMessagesInConversation(String conversationId, Instant toTime) {
        log.info("(findMessagesInConversation)conversationId : {}", conversationId);
        return messageRepository.findMessagesBy(conversationId, toTime, MAX_CONTEXT_MESSAGES)
                .map(conversationMessage ->
                        ConversationMessageDto.builder()
                                .id(conversationMessage.getId())
                                .conversationId(conversationMessage.getKey().getConversationId())
                                .createdAt(conversationMessage.getKey().getCreatedAt())
                                .question(conversationMessage.getQuestion())
                                .answer(conversationMessage.getAnswer())
                                .build()
                );
    }

    @Override
    public Mono<ModelContextWindow> generateContextWindow(String conversationId, String model, String newPromptMessage) {
        log.info("(generateContextPrompt)conversationId : {}, newPromptMessage : {}", conversationId, newPromptMessage);

        Flux<ConversationMessage> contextWindow = messageRepository
                .findContextWindowByConversationId(conversationId, MAX_CONTEXT_MESSAGES)
                .sort(Comparator.comparing(ConversationMessage::getCreatedAt));

        return contextWindow.collectList().map(conversationMessages -> {
            List<Message> messages = new ArrayList<>();
            UserMessage userMessage;
            AssistantMessage assistantMessage;
            for (ConversationMessage conversationMessage : conversationMessages) {
                userMessage = new UserMessage(conversationMessage.getQuestion());
                assistantMessage = new AssistantMessage(conversationMessage.getAnswer());
                messages.add(userMessage);
                messages.add(assistantMessage);
            }
            messages.add(new UserMessage(newPromptMessage));

            ModelContextWindow modelContextWindow = new ModelContextWindow();
            modelContextWindow.setModel(model);
            modelContextWindow.setMessages(messages);
            return modelContextWindow;
        });
    }

    @Override
    public Mono<Void> saveMessage(String conversationId, String question, String answer) {
        log.info("(saveMessage)conversationId : {}, question : {}, answer : {}", conversationId, question, answer);
        ConversationMessage conversationMessage = new ConversationMessage();

        MessageKey messageKey = new MessageKey();
        messageKey.setConversationId(conversationId);
        messageKey.setCreatedAt(Instant.now());

        conversationMessage.setKey(messageKey);
        conversationMessage.setId(UUID.randomUUID().toString());
        conversationMessage.setQuestion(question);
        conversationMessage.setAnswer(answer);
        return messageRepository.save(conversationMessage).then();
    }

}
