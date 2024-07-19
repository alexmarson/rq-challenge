package com.example.rqchallenge.dto;

import com.example.rqchallenge.model.Employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetSingleEmployeeResponseDto {
    @JsonProperty("status")
    String status;
    @JsonProperty("data")
    public Employee data;
}
