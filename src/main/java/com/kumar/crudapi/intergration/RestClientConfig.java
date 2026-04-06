package com.kumar.crudapi.intergration;

import com.kumar.crudapi.exception.ClientErrorException;
import com.kumar.crudapi.exception.ServerErrorException;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestClientConfig {

    Logger log = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl("https://jsonplaceholder.typicode.com")
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-App-Version", "1.0");
                })
                .requestInterceptor(loggingInterceptor())
                .defaultStatusHandler(HttpStatusCode::isError, errorHandler())
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public CloseableHttpClient httpClient() {

        return HttpClients.custom()
                .setConnectionManager(connectionManager())
                .setDefaultRequestConfig(requestConfig())

                // Prefer HTTP/2
//                .setVersionPolicy(HttpVersionPolicy.NEGOTIATE)
                .setRetryStrategy(retryStrategy())
                .evictIdleConnections(TimeValue.of(30, TimeUnit.SECONDS))
                .evictExpiredConnections()
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager connectionManager() {

        PoolingHttpClientConnectionManager manager =
                new PoolingHttpClientConnectionManager();

        manager.setMaxTotal(100);          // total connections
        manager.setDefaultMaxPerRoute(20); // per host

        return manager;
    }


    @Bean
    public RequestConfig requestConfig() {

        return RequestConfig.custom()
                .setConnectionRequestTimeout(5,TimeUnit.SECONDS)        // TCP connect
                .setResponseTimeout(10,TimeUnit.SECONDS)      // read timeout
                .setConnectionRequestTimeout(3,TimeUnit.SECONDS) // pool wait
                .build();
    }

    @Bean
    public HttpRequestRetryStrategy retryStrategy() {
        return new DefaultHttpRequestRetryStrategy(
                3,                          // max retries
                TimeValue.ofSeconds(2)      // retry interval
        );
    }

    @Bean
    public ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            long start = System.currentTimeMillis();
            log.info("Request: {} {}", request.getMethod(), request.getURI());
            ClientHttpResponse response = execution.execute(request, body);
            long timeTaken = System.currentTimeMillis() - start;
            log.info("Response: {} ({} ms)", response.getStatusCode(), timeTaken);
            return response;
        };
    }

    @Bean
    public RestClient.ResponseSpec.ErrorHandler errorHandler() {
        return (request, response) -> {

            String body = readResponseBody(response);

            if (response.getStatusCode().is4xxClientError()) {
                log.error("4XX Error: {} {} -> {}", request.getMethod(), request.getURI(), body);
                throw new ClientErrorException(response.getStatusCode(), body);
            }

            if (response.getStatusCode().is5xxServerError()) {
                log.error("5XX Error: {} {} -> {}", request.getMethod(), request.getURI(), body);
                throw new ServerErrorException(response.getStatusCode(), body);
            }
        };
    }

    private String readResponseBody(ClientHttpResponse response) {
        try {
            return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Unable to read response body";
        }
    }
}
