package org.aibles.backend_ai.service.chatstream;

public interface ChatStreamRegistry {

    ChatStreamService getChatStreamService(String modelName);
}
