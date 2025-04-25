package org.aibles.backend_ai.repository;

import org.aibles.backend_ai.entity.ConversationMetadata;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationMetadataRepo extends ReactiveCrudRepository<ConversationMetadata, String> {
}
