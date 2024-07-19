package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {
    @JsonProperty("id")
    String id;
    @JsonProperty("employee_name")
    String employeeName;
    @JsonProperty("employee_salary")
    Integer employeeSalary;
    @JsonProperty("employee_age")
    Integer employeeAge;
    @JsonProperty("profile_image")
    String profileImage;
}
