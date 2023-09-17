package com.example.service;

import com.example.config.AppConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Service
public class ServiceA implements CallService{
    private final AppConfig appConfig;
    private RestTemplate restTemplate;
    private CircuitBreaker circuitBreaker;
    private Retry retry;


    @Autowired
    public ServiceA(AppConfig appConfig, RestTemplate restTemplate, CircuitBreakerRegistry circuitBreaker, RetryRegistry retry) {
        this.appConfig = appConfig;
        this.restTemplate = restTemplate;
        this.circuitBreaker = circuitBreaker.circuitBreaker(BACKEND);
        this.retry = retry.retry(BACKEND);
    }

    private static final String BACKEND = "backendA";

    //@CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    //@RateLimiter(name = BACKEND)
    //@Bulkhead(name = BACKEND, fallbackMethod = "fallback")
    //@Retry(name = BACKEND)
    //@TimeLimiter(name = BACKEND)
    /*public CompletionStage test(String param) {

       *//* ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(appConfig.apiEndpoint + "/test/" + param, String.class);*//*

        CompletableFuture<ResponseEntity<String>> response = (CompletableFuture<ResponseEntity<String>>) Executors.newSingleThreadExecutor().submit(() -> {
            restTemplate.getForEntity(appConfig.apiEndpoint + "/test/" + param, String.class);
        });

        //return responseEntity.getStatusCode().name()+responseEntity.getBody();
        return response;
    }*/

    //@CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    //@RateLimiter(name = BACKEND)
    //@Bulkhead(name = BACKEND, fallbackMethod = "fallback")
    //@Retry(name = BACKEND)
    //@TimeLimiter(name = BACKEND)
    public String test1(String param) {
        System.out.println("Calling Rest endpoint param-->" + param);
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(appConfig.apiEndpoint + "/test/" + param, String.class);
        System.out.println("Response received param-->" + param);
        return responseEntity.getStatusCode().name() + responseEntity.getBody();
    }


    public String test2(String param) {
        return Try.ofSupplier(Retry.decorateSupplier(retry, CircuitBreaker.decorateSupplier(circuitBreaker,
                () -> {
                    System.out.println("Calling Rest endpoint param-->" + param);
                    ResponseEntity<String> responseEntity =
                            restTemplate.getForEntity(appConfig.apiEndpoint + "/test/" + param, String.class);
                    System.out.println("Response received param-->" + param);
                    return responseEntity.getStatusCode().name() + responseEntity.getBody();
                }
        ))).recover(throwable -> "Fallback called!").get();

        /*return CircuitBreaker.decorateCheckedSupplier(circuitBreaker,
                () -> Retry.decorateSupplier(retry,
                        () -> {
                            ResponseEntity<String> responseEntity =
                                    restTemplate.getForEntity(appConfig.apiEndpoint + "/test/" + param, String.class);
                            System.out.println("Response received param-->" + param);
                            return responseEntity.getStatusCode().name() + responseEntity.getBody();
                        })).recover(throwable -> () -> () -> "Fallback called!").get().get();*/

    }


    private String fallback(String param1, CallNotPermittedException e) {
        //throw e;
        return "Handled the exception when the CircuitBreaker is open-->" + param1;
    }

    private String fallback(String param1, BulkheadFullException e) {
        return "Handled the exception when the Bulkhead is full-->" + param1;
    }

    private String fallback(String param1, NumberFormatException e) {
        return "Handled the NumberFormatException-->" + param1;
    }

    private String fallback(String param1, Exception e) {
        return "Handled any other exception-->" + param1;
    }

    private Supplier<String> fallback(Throwable e) {
        return () -> "Handled any other exception.";
        //return "Handled any other exception.";
    }

    @Override
    public String test(String param) {
        return null;
    }
}
