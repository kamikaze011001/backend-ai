package org.aibles.backend_ai.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.aibles.backend_ai.dto.request.ConversationRequest;
import org.aibles.backend_ai.dto.request.StartConversationRequest;
import org.aibles.backend_ai.dto.response.ConversationDto;
import org.aibles.backend_ai.dto.response.ConversationMessageDto;
import org.aibles.backend_ai.service.ChatService;
import org.aibles.backend_ai.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@RequestMapping("/api/v1")
@RestController
public class ChatController {

    private final ConversationService conversationService;

    private final ChatService chatService;

    public ChatController(ConversationService conversationService, ChatService chatService) {
        this.conversationService = conversationService;
        this.chatService = chatService;
    }

    @PostMapping(path = "/conversations:start", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ConversationDto> startConversation(@RequestBody @Valid StartConversationRequest request) {
        log.info("New conversation request: {}", request);
        return chatService.startConversation(request);
    }

    @PostMapping(value = "/conversations/{id}:stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<String> streamConversation(@PathVariable("id") String id, @RequestBody @Valid ConversationRequest request) {
        log.info("Streaming conversation request: {}", request);
        return chatService.streamAnswer(id, request);
    }

    @GetMapping("/conversations")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ConversationDto> get(@RequestParam Instant toTime,
                                     @RequestParam int limit) {
        log.info("(getLatestConversations)toTime: {}, limit: {}", toTime, limit);
        return conversationService.findConversations(toTime, limit);
    }

    @GetMapping("/conversations/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ConversationMessageDto> getLatestMessageInConversations(
            @PathVariable("id") String id,
            @RequestParam Instant toTime
    ) {
        log.info("(getLatestMessageInConversations)id: {}, toTime: {}", id, toTime);
        return conversationService.findMessagesInConversation(id, toTime);
    }
}
