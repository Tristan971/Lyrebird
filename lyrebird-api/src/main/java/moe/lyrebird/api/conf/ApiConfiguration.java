package moe.lyrebird.api.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ApiConfiguration {

    /**
     * @return The Jackson object mapper used for serialization and deserialization
     */
    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(
                DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                DeserializationFeature.FAIL_ON_TRAILING_TOKENS
        );
        return objectMapper;
    }

}
