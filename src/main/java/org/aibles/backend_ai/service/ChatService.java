package org.aibles.backend_ai.service;

import org.aibles.backend_ai.dto.request.ConversationRequest;
import org.aibles.backend_ai.dto.request.StartConversationRequest;
import org.aibles.backend_ai.dto.response.ConversationDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {

    Mono<ConversationDto> startConversation(StartConversationRequest request);

    Flux<String> streamAnswer(String conversationId, ConversationRequest request);
}
