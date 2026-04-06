package com.kumar.crudapi.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TodoService {

    private final RestClient restClient;
    private final WebClient webClient;

    public TodoService(RestClient restClient, @Qualifier("channelwebClient") WebClient webClient) {
        this.restClient = restClient;
        this.webClient = webClient;
    }

    public String getTodoById(int id) {
        return restClient.get()
                .uri("/todos/{id}", id)
                .retrieve()
                .body(String.class);
    }



    @Retry(name = "todoService", fallbackMethod = "fallback")
    @CircuitBreaker(name = "todoService", fallbackMethod = "fallback")
    public Mono<String> getTodo(int id) {
        return webClient.get()
                .uri("/todos/{id}", id)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fallback(int id, Throwable ex) {
        log.warn("Fallback triggered for id {}: {}", id, ex.getMessage());
        return Mono.just("Fallback response for id=" + id);
    }
}