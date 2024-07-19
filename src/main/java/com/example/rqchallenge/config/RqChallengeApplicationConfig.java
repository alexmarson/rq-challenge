package com.example.rqchallenge.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RqChallengeApplicationConfig {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.errorHandler(new RqChallengeErrorHandler()).build();
    }
}
