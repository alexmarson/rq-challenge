package com.example.rqchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateEmployeeRequest {
    String name;
    String salary;
    String age;

    public boolean isInvalid() {
        return (name == null ||
                name.isEmpty() ||
                salary == null ||
                salary.isEmpty() ||
                age == null ||
                age.isEmpty());
    }
}
