package org.aibles.backend_ai.configuration;

import org.aibles.backend_ai.repository.ConversationMetadataRepo;
import org.aibles.backend_ai.repository.ConversationRepository;
import org.aibles.backend_ai.repository.MessageRepository;
import org.aibles.backend_ai.service.ChatService;
import org.aibles.backend_ai.service.ChatServiceImpl;
import org.aibles.backend_ai.service.ConversationService;
import org.aibles.backend_ai.service.ConversationServiceImpl;
import org.aibles.backend_ai.service.chatstream.ChatStreamRegistry;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackendAiConfiguration {

    @Bean
    public ConversationService conversationService(
                                           ConversationRepository conversationRepository,
                                           ConversationMetadataRepo conversationMetadataRepo,
                                           MessageRepository messageRepository) {
        return new ConversationServiceImpl(conversationRepository, conversationMetadataRepo, messageRepository);
    }

    @Bean
    public ChatService chatService(OllamaChatModel ollamaChatModel,
                                   ConversationService conversationService,
                                   ChatStreamRegistry chatStreamRegistry) {
        return new ChatServiceImpl(ollamaChatModel, conversationService, chatStreamRegistry);
    }

    @Bean
    public ServiceLocatorFactoryBean chatStreamFactoryBean() {
        ServiceLocatorFactoryBean serviceLocatorFactoryBean = new ServiceLocatorFactoryBean();
        serviceLocatorFactoryBean.setServiceLocatorInterface(ChatStreamRegistry.class);
        return serviceLocatorFactoryBean;
    }
}
