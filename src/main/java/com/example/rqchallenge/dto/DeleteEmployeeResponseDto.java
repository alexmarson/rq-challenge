package com.example.rqchallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteEmployeeResponseDto {
    @JsonProperty("status")
    String status;
    @JsonProperty("data")
    String data;
    @JsonProperty("message")
    String message;
}
