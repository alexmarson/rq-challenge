package com.example.rqchallenge.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class RqApplicationErrorResponse {
    private String message;
    private int status;
    private Instant timestamp;

}
