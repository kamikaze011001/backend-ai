package org.aibles.backend_ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyClass
public class MessageKey implements Serializable {

    @PrimaryKeyColumn(value = "conversation_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String conversationId;

    @PrimaryKeyColumn(value = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Instant createdAt;
}
