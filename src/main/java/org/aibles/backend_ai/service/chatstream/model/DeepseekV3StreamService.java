package org.aibles.backend_ai.service.chatstream.model;

import lombok.extern.slf4j.Slf4j;
import org.aibles.backend_ai.service.chatstream.ChatStreamService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component("DEEPSEEK_V3")
@Slf4j
public class DeepseekV3StreamService implements ChatStreamService {

    private final OpenAiChatModel openAiChatModel;

    public DeepseekV3StreamService(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @Override
    public Flux<String> streamAnswer(List<Message> messages) {
        log.info("Deepseek-V3 start streaming answer");
        return openAiChatModel.stream(messages.toArray(new Message[0]));
    }
}
