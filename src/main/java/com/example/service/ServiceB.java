package com.example.service;

import com.example.config.AppConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component(value = "serviceB")
public class ServiceB implements CallService {
    private final AppConfig appConfig;
    private final RestTemplate restTemplate;


    public ServiceB(AppConfig appConfig, RestTemplate restTemplate) {
        this.appConfig = appConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = BACKEND, fallbackMethod = "cbFallback")
    //@RateLimiter(name = BACKEND)
    //@Bulkhead(name = BACKEND, fallbackMethod = "fallback")
    @Retry(name = BACKEND, fallbackMethod = "retryFallback")
    //@TimeLimiter(name = BACKEND)
    public String test(String param) {
        System.out.println("Calling Rest endpoint param-->" + param + " - " + new Date());
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(appConfig.apiEndpoint + "/test/" + param, String.class);
        System.out.println("Response received param-->" + param);
        return responseEntity.getStatusCode().name() + responseEntity.getBody();
    }


    private String cbFallback(String param, CallNotPermittedException e) {
        System.out.println(e.getMessage());
        return "Handled by cbFallback CallNotPermittedException.";
    }
   /* private String cbFallback(String param, Exception e) {
        System.out.println(e.getMessage());
        return "Handled by cbFallback Exception.";
    }*/

    private String retryFallback(String param, Exception e) {
        System.out.println(e.getMessage());
        return "Handled by retryFallback Exception.";
    }
}
