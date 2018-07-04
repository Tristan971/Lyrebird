package moe.lyrebird.api.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = "moe.lyrebird.api.client")
public class ClientConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Profile("dev")
    @Configuration
    @PropertySource("classpath:api-dev.properties")
    public static class DevConfiguration {}

    @Profile("!dev")
    @Configuration
    @PropertySource("classpath:api.properties")
    public static class ProdConfiguration {}

}
