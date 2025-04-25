package org.aibles.backend_ai.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aibles.backend_ai.constant.AIModel;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StartConversationRequest {

    @NotBlank
    private String promptMessage;

    @NotBlank
    private String model;

    @AssertTrue(message = "Invalid ai model")
    private boolean isValidModel() {
        return Arrays.stream(AIModel.values()).anyMatch(aiModel -> aiModel.name().equals(this.model));
    }
}
