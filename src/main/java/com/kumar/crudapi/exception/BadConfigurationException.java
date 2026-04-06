package com.kumar.crudapi.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class BadConfigurationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String ERROR_KEY = "error.badConfig";

    private final String configName;
    private final String message;
    private final Throwable cause;
    private final String errorKey;

    public BadConfigurationException(String errorKey, String message, String configName, Throwable cause) {
        super(message, cause);
        this.configName = configName;
        this.message = message;
        this.cause = cause;
        this.errorKey = errorKey;
    }

    public BadConfigurationException(String message, String configName, Throwable cause) {
        this(ERROR_KEY, message, configName, cause);
    }

    public BadConfigurationException(String errorKey, String message, String configName) {
        this(errorKey, message, configName, null);
    }

    public BadConfigurationException(String message, String configName) {
        this(ERROR_KEY, message, configName, null);
    }
}