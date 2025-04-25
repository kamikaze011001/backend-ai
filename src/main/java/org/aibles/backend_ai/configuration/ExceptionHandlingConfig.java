package org.aibles.backend_ai.configuration;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for exception handling related beans.
 */
@Configuration
public class ExceptionHandlingConfig {

    /**
     * Provides the WebProperties.Resources bean required by the error handlers.
     *
     * @return web resources configuration
     */
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    /**
     * Provides the ErrorAttributes bean which is used to store and retrieve error attributes.
     *
     * @return error attributes
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }
}
