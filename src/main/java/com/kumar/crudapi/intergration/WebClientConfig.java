package com.kumar.crudapi.intergration;

import com.kumar.crudapi.base.error.BadRequestException;
import com.kumar.crudapi.exception.BadConfigurationException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${external.api.base-url:https://jsonplaceholder.typicode.com}")
    private String baseUrl;

    @Value("${external.api.timeout.connect:5000}")
    private int connectTimeoutMs;

    @Value("${external.api.timeout.read:10000}")
    private int readTimeoutMs;

    @Value("${com.header.pod-id:#{null}}")
    private String xPodId;

    @Value("${com.header.tenant-id:#{null}}")
    private String xTenantId;

    @Bean
    public WebClient channelwebClient(ReactorClientHttpConnector connector,
                                      ExchangeStrategies strategies) {

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(connector)
                .exchangeStrategies(strategies)
                .filter(loggingFilter())         // request/response logging
                .filter(errorHandler())   // global error handler
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient() {

        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(5000 * 1024))
                        .build())
//                .filter(oauth)
                .filter(errorHandler())
                .defaultHeaders(this::addDefaultHeaders)
                .build();
    }

    @Bean
    public WebClient publicWebClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create().followRedirect((req, res) -> req.redirectedFrom().length < 3);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return builder
                .clientConnector(connector)
                .build();
    }

    private void addDefaultHeaders(final HttpHeaders headers) {
        if (StringUtils.hasText(xPodId)) {
            headers.add("x-pod-id", xPodId.trim());
        }
        if (StringUtils.hasText(xTenantId)) {
            headers.add("x-tenant-id", xTenantId.trim());
        }
    }

    /**
     * Reactor Netty connector with connection pooling, HTTP/2, and timeouts
     */
    @Bean
    public ReactorClientHttpConnector connector() {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                );

        HttpClient httpClient = HttpClient.from(tcpClient)
                .protocol(HttpProtocol.H2) // HTTP/2
                .compress(true);

        return new ReactorClientHttpConnector(httpClient);
    }

    /**
     * Optional: increase buffer size for large responses
     */
    @Bean
    public ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // 16 MB
                .build();
    }

    @Bean
    public ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Request: {} {}", request.method(), request.url());
            request.headers().forEach((k, v) -> log.debug("{}: {}", k, v));
            return Mono.just(request);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("Response: {} {}", response.statusCode(), response.headers().asHttpHeaders());
            return Mono.just(response);
        }));
    }

    public ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                log.error("5XX Error while calling delegate - {}", clientResponse.statusCode());
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)));
            } else if (clientResponse.statusCode().value() == 404) {
                log.error("404 Error while calling delegate - {}", clientResponse.statusCode());
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, errorBody)));
            } else if (clientResponse.statusCode().value() == 401 || clientResponse.statusCode().value() == 403) {
                log.error("Auth Error while calling delegate - {}", clientResponse.statusCode());
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new BadConfigurationException("error.invalid.credentials", "Invalid Client Credentials", "spring.security.oauth2.client.registration.com")));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                log.error("4XX Error while calling delegate - {}", clientResponse.statusCode());
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new BadRequestException("Invalid Input", errorBody, "error.invalid.input")));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
}
