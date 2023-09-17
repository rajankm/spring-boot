package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Value(value = "${api.endpoint}")
    public String apiEndpoint;
    @Bean
    public static RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
