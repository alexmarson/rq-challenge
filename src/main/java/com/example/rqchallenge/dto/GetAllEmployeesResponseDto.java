package com.example.rqchallenge.dto;

import com.example.rqchallenge.model.Employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@AllArgsConstructor
public class GetAllEmployeesResponseDto {
    @JsonProperty("status")
    String status;
    @JsonProperty("data")
    public List<Employee> data;
}
