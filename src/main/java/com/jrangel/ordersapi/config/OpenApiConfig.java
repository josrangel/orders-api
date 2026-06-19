package com.jrangel.ordersapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ordersApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Orders API")
                        .description("API de práctica con Spring Boot, PostgreSQL, Flyway y Docker")
                        .version("1.0.0"));
    }
}
