package com.kumar.crudapi.exception;

import org.springframework.http.HttpStatusCode;

public class ClientErrorException extends RuntimeException {
    private final HttpStatusCode status;

    public ClientErrorException(HttpStatusCode status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}