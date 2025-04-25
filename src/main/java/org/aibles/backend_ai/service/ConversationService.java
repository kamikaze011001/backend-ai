package org.aibles.backend_ai.service;

import org.aibles.backend_ai.dto.ModelContextWindow;
import org.aibles.backend_ai.dto.response.ConversationDto;
import org.aibles.backend_ai.dto.response.ConversationMessageDto;
import org.aibles.backend_ai.entity.Conversation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface ConversationService {

    Mono<Conversation> saveConversation(String title, String model);

    Flux<ConversationDto> findConversations(Instant toTine, int limit);

    Mono<String> findConversationModel(String id);

    Flux<ConversationMessageDto> findMessagesInConversation(String conversationId, Instant toTine);

    Mono<ModelContextWindow> generateContextWindow(String conversationId, String model, String newPromptMessage);

    Mono<Void> saveMessage(String conversationId, String question, String answer);
}
