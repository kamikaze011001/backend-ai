package org.aibles.backend_ai.repository;

import org.aibles.backend_ai.entity.Conversation;
import org.aibles.backend_ai.entity.ConversationKey;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Repository
public interface ConversationRepository extends ReactiveCassandraRepository<Conversation, ConversationKey> {

    @Query("""
           SELECT * FROM conversation WHERE month_bucket = :monthBucket AND created_at <= :toTime LIMIT :limit
           """)
    Flux<Conversation> findBy(String monthBucket, Instant toTime, int limit);
}
