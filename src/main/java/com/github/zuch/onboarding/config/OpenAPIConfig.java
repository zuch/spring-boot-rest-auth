package com.github.zuch.onboarding.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().
                title("Customer Onboarding API")
                .version("1.0.0")
                .description("This API exposes endpoints to onboard a new customer"));
    }
}