package org.aibles.backend_ai.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response model that will be returned to clients
 * when an exception occurs in the application.
 */
@Data
@Builder
public class ErrorResponse {

    /**
     * HTTP status code associated with the error.
     */
    private int status;

    /**
     * Short error code for programmatic identification of the error type.
     */
    private String errorCode;

    /**
     * Human-readable error message describing what went wrong.
     */
    private String message;

    /**
     * Timestamp of when the error occurred.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    /**
     * Optional list of detailed error messages, particularly useful for validation errors.
     */
    private List<String> details;

    /**
     * Optional path that caused the error.
     */
    private String path;

    /**
     * Creates a basic error response with a status and message.
     *
     * @param status HTTP status
     * @param message error message
     * @return constructed error response
     */
    public static ErrorResponse of(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .status(status.value())
                .errorCode(status.name())
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates an error response with a status, error code, and message.
     *
     * @param status HTTP status
     * @param errorCode custom error code
     * @param message error message
     * @return constructed error response
     */
    public static ErrorResponse of(HttpStatus status, String errorCode, String message) {
        return ErrorResponse.builder()
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a detailed error response with a status, message, and list of details.
     *
     * @param status HTTP status
     * @param message error message
     * @param details list of detailed error messages
     * @return constructed error response
     */
    public static ErrorResponse of(HttpStatus status, String message, List<String> details) {
        return ErrorResponse.builder()
                .status(status.value())
                .errorCode(status.name())
                .message(message)
                .details(details)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a detailed error response with a status, error code, message, and list of details.
     *
     * @param status HTTP status
     * @param errorCode custom error code
     * @param message error message
     * @param details list of detailed error messages
     * @return constructed error response
     */
    public static ErrorResponse of(HttpStatus status, String errorCode, String message, List<String> details) {
        return ErrorResponse.builder()
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .details(details)
                .timestamp(Instant.now())
                .build();
    }
}
