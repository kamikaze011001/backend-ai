package org.aibles.backend_ai.repository;

import org.aibles.backend_ai.entity.ConversationMessage;
import org.aibles.backend_ai.entity.MessageKey;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Repository
public interface MessageRepository extends ReactiveCassandraRepository<ConversationMessage, MessageKey> {

    @Query("""
            select * from conversation_message where conversation_id = :conversationId order by created_at desc limit :maxWindow
          """)
    Flux<ConversationMessage> findContextWindowByConversationId(String conversationId, int maxWindow);

    @Query("""
           select * from conversation_message where conversation_id = :conversationId
           and created_at <= :toTime order by created_at desc limit :limit
           """)
    Flux<ConversationMessage> findMessagesBy(String conversationId, Instant toTime, int limit);
}
