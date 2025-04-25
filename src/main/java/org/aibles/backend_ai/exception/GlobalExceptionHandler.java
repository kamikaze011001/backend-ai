package org.aibles.backend_ai.exception;

import com.datastax.oss.driver.api.core.servererrors.QueryExecutionException;
import com.datastax.oss.driver.api.core.servererrors.QueryValidationException;
import com.datastax.oss.driver.api.core.servererrors.ServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the WebFlux application.
 * This handler catches all exceptions thrown by the application and returns standardized error responses.
 */
@Component
@Order(-2) // High priority to ensure this handler is used before the default ones
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                 WebProperties.Resources resources,
                                 ApplicationContext applicationContext,
                                 ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::formatErrorResponse);
    }

    private Mono<ServerResponse> formatErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        log.error("Exception caught by global exception handler", error);
        
        ErrorResponse errorResponse;
        HttpStatus status;

        if (error instanceof ResourceNotFoundException) {
            // Handle resource not found exceptions
            ResourceNotFoundException ex = (ResourceNotFoundException) error;
            status = HttpStatus.NOT_FOUND;
            errorResponse = ErrorResponse.of(
                    status,
                    "RESOURCE_NOT_FOUND",
                    String.format("%s not found with %s: '%s'", 
                            ex.getResourceName(), 
                            ex.getFieldName(), 
                            ex.getFieldValue())
            );
        } else if (error instanceof AIServiceException) {
            // Handle AI service exceptions
            AIServiceException ex = (AIServiceException) error;
            status = HttpStatus.SERVICE_UNAVAILABLE;
            errorResponse = ErrorResponse.of(
                    status,
                    ex.getErrorCode(),
                    ex.getMessage()
            );
        } else if (error instanceof DatabaseException) {
            // Handle database exceptions
            DatabaseException ex = (DatabaseException) error;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ErrorResponse.of(
                    status,
                    ex.getErrorCode(),
                    ex.getMessage()
            );
        } else if (error instanceof WebExchangeBindException) {
            // Handle validation exceptions
            WebExchangeBindException ex = (WebExchangeBindException) error;
            status = HttpStatus.BAD_REQUEST;
            List<String> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(this::formatFieldError)
                    .collect(Collectors.toList());
            
            errorResponse = ErrorResponse.of(
                    status,
                    "VALIDATION_ERROR",
                    "Validation failed for request",
                    errors
            );
        } else if (error instanceof ResponseStatusException) {
            // Handle Spring WebFlux ResponseStatusException
            ResponseStatusException ex = (ResponseStatusException) error;
            status = HttpStatus.valueOf(ex.getStatusCode().value());
            errorResponse = ErrorResponse.of(
                    status,
                    ex.getReason() != null ? ex.getReason() : "Request failed with status " + status.value()
            );
        } else if (error instanceof WebClientResponseException) {
            // Handle Web client response exceptions (from external service calls)
            WebClientResponseException ex = (WebClientResponseException) error;
            status = HttpStatus.valueOf(ex.getStatusCode().value());
            errorResponse = ErrorResponse.of(
                    status,
                    "EXTERNAL_SERVICE_ERROR",
                    "External service request failed: " + ex.getMessage()
            );
        } else if (error instanceof QueryExecutionException || 
                   error instanceof QueryValidationException ||
                   error instanceof ServerError) {
            // Handle Cassandra specific exceptions
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ErrorResponse.of(
                    status,
                    "DATABASE_ERROR",
                    "Database operation failed: " + error.getMessage()
            );
        } else {
            // Handle any other exceptions
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ErrorResponse.of(
                    status,
                    "INTERNAL_SERVER_ERROR",
                    "An unexpected error occurred"
            );
        }
        
        // Add the request path to the error response
        errorResponse.setPath(request.path());
        
        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    private String formatFieldError(FieldError fieldError) {
        return String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
    }
}
