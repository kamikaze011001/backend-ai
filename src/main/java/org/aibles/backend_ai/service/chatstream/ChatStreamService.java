package org.aibles.backend_ai.service.chatstream;

import org.springframework.ai.chat.messages.Message;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatStreamService {

    Flux<String> streamAnswer(List<Message> messages);
}
