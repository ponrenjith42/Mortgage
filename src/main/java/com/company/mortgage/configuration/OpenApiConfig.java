package com.company.mortgage.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mortgageOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mortgage API")
                        .description("REST API for mortgage calculations")
                        .version("1.0.0"));
    }
}
