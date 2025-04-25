package org.aibles.backend_ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {

    @PrimaryKey
    private ConversationKey key;

    private String id;

    private String title;
}
