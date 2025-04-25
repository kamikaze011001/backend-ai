package org.aibles.backend_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModelContextWindow {

    private String model;

    private List<Message> messages;
}
