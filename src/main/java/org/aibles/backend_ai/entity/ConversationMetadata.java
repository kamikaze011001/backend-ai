package org.aibles.backend_ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "conversation_metadata")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMetadata {

    @PrimaryKey(value = "conversation_id")
    private String conversationId;

    private String model;
}
