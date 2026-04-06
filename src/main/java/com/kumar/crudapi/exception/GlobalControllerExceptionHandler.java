package com.kumar.crudapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<String> handleClientError(ClientErrorException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatus().value()))
                .body("Client Error: " + ex.getMessage());
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<String> handleServerError(ServerErrorException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatus().value()))
                .body("Server Error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        log.error("Unexpected exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + ex.getMessage());
    }
}