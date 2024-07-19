package com.example.rqchallenge.exception;

import org.springframework.http.HttpStatus;

public class RqChallengeApplicationException extends RuntimeException {
    final String message;
    final HttpStatus status;

    public RqChallengeApplicationException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

