package org.aibles.backend_ai.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 * This exception is typically used when a query for a specific entity returns no results.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructs a new ResourceNotFoundException with the specified details.
     *
     * @param resourceName the name of the resource that was not found
     * @param fieldName the name of the field used in the search criteria
     * @param fieldValue the value of the field used in the search criteria
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Returns the name of the resource that was not found.
     *
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Returns the name of the field used in the search criteria.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the value of the field used in the search criteria.
     *
     * @return the field value
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
