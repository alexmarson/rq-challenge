package com.example.rqchallenge.config;

import com.example.rqchallenge.exception.RqApplicationErrorResponse;
import com.example.rqchallenge.exception.RqChallengeApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@ControllerAdvice
public class RqChallengeExceptionHandler {

    @ExceptionHandler(RqChallengeApplicationException.class)
    public ResponseEntity<RqApplicationErrorResponse> handleException(
            RqChallengeApplicationException exception,
            WebRequest request) {
        RqApplicationErrorResponse errorResponse = new RqApplicationErrorResponse(
                exception.getMessage(),
                exception.getStatus().value(),
                Instant.now()
        );
        return new ResponseEntity<>(errorResponse, exception.getStatus());
    }
}
