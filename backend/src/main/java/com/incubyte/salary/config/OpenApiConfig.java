package com.incubyte.salary.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ACME Global Salary Management API")
                        .version("1.0.0")
                        .description("RESTful API for ACME Org's HR Manager to analyze and manage compensation across 10,000 international employees.")
                        .contact(new Contact()
                                .name("Ankith Sanga")
                                .email("ankithsanga1234@gmail.com")));
    }
}
