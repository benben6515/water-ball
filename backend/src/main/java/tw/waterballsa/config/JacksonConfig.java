package tw.waterballsa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson configuration for JSON serialization.
 * Configures proper date/time serialization with timezone support.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register JavaTimeModule for Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());

        // Serialize dates as ISO-8601 strings (not timestamps)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configure timezone as UTC for consistent API responses
        // Frontend will handle conversion to local timezone
        mapper.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        return mapper;
    }
}
