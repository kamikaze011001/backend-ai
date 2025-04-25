package org.aibles.backend_ai.exception;

/**
 * Exception thrown when there's an issue with database operations.
 * This could be due to connection issues, query failures, or data integrity problems.
 */
public class DatabaseException extends RuntimeException {

    private final String operation;
    private final String errorCode;

    /**
     * Constructs a new DatabaseException with the specified details.
     *
     * @param operation the database operation that failed (e.g., "save", "query", "delete")
     * @param message detailed error message
     */
    public DatabaseException(String operation, String message) {
        super(String.format("Database Operation Failed [%s]: %s", operation, message));
        this.operation = operation;
        this.errorCode = "DB_ERROR";
    }

    /**
     * Constructs a new DatabaseException with the specified details.
     *
     * @param operation the database operation that failed (e.g., "save", "query", "delete")
     * @param message detailed error message
     * @param cause the original exception that caused this error
     */
    public DatabaseException(String operation, String message, Throwable cause) {
        super(String.format("Database Operation Failed [%s]: %s", operation, message), cause);
        this.operation = operation;
        this.errorCode = "DB_ERROR";
    }

    /**
     * Constructs a new DatabaseException with the specified details.
     *
     * @param operation the database operation that failed (e.g., "save", "query", "delete")
     * @param errorCode a specific error code for this issue
     * @param message detailed error message
     */
    public DatabaseException(String operation, String errorCode, String message) {
        super(String.format("Database Operation Failed [%s]: %s", operation, message));
        this.operation = operation;
        this.errorCode = errorCode;
    }

    /**
     * Returns the database operation that encountered an error.
     *
     * @return the operation name
     */
    public String getOperation() {
        return operation;
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
