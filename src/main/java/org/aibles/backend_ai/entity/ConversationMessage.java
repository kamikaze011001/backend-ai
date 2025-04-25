package org.aibles.backend_ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table(value = "conversation_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMessage {

    @PrimaryKey
    private MessageKey key;

    private String id;

    private String question;

    private String answer;

    public Instant getCreatedAt() {
        return key.getCreatedAt();
    }
}
