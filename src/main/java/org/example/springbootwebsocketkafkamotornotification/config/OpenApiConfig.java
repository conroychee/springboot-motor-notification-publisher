package org.example.springbootwebsocketkafkamotornotification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info().title("HMGICS Motor Data Publisher").version("v1")
                        .description("Publish motor notification to frontend"));
    }
}
