package com.example.rqchallenge.service;

import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNames(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalary();

    List<String> getTopTenHighestEarningEmployeeNames();

    String createEmployee(CreateEmployeeRequest employeeRequest);

    String deleteEmployeeById(String id);
}
