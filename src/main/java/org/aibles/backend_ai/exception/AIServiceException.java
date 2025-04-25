package org.aibles.backend_ai.exception;

/**
 * Exception thrown when there's an issue with the AI service communication.
 * This could be due to network issues, service unavailability, or API errors.
 */
public class AIServiceException extends RuntimeException {

    private final String serviceName;
    private final String errorCode;

    /**
     * Constructs a new AIServiceException with the specified details.
     *
     * @param serviceName the name of the AI service (e.g., "Ollama", "OpenAI")
     * @param message detailed error message
     */
    public AIServiceException(String serviceName, String message) {
        super(String.format("AI Service Error [%s]: %s", serviceName, message));
        this.serviceName = serviceName;
        this.errorCode = "AI_SERVICE_ERROR";
    }

    /**
     * Constructs a new AIServiceException with the specified details.
     *
     * @param serviceName the name of the AI service (e.g., "Ollama", "OpenAI")
     * @param message detailed error message
     * @param cause the original exception that caused this error
     */
    public AIServiceException(String serviceName, String message, Throwable cause) {
        super(String.format("AI Service Error [%s]: %s", serviceName, message), cause);
        this.serviceName = serviceName;
        this.errorCode = "AI_SERVICE_ERROR";
    }

    /**
     * Constructs a new AIServiceException with the specified details.
     *
     * @param serviceName the name of the AI service (e.g., "Ollama", "OpenAI")
     * @param errorCode a specific error code for this issue
     * @param message detailed error message
     */
    public AIServiceException(String serviceName, String errorCode, String message) {
        super(String.format("AI Service Error [%s]: %s", serviceName, message));
        this.serviceName = serviceName;
        this.errorCode = errorCode;
    }

    /**
     * Returns the name of the AI service that encountered an error.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
