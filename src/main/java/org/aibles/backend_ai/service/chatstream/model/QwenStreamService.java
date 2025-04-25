package org.aibles.backend_ai.service.chatstream.model;

import lombok.extern.slf4j.Slf4j;
import org.aibles.backend_ai.service.chatstream.ChatStreamService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component("QWEN2_5")
@Slf4j
public class QwenStreamService implements ChatStreamService {

    private final OllamaChatModel ollamaChatModel;

    public QwenStreamService(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    @Override
    public Flux<String> streamAnswer(List<Message> messages) {
        log.info("Qwen 2.5 start streaming answer");
        return ollamaChatModel.stream(new Prompt(messages, OllamaOptions
                .builder()
                .model("qwen2.5:3b")
                .build()))
                .map(
                        chatRsp -> chatRsp.getResult().getOutput().getText()
                );
    }
}
