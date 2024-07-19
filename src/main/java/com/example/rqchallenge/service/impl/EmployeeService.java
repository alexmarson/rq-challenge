package com.example.rqchallenge.service.impl;

import com.example.rqchallenge.client.EmployeeClient;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.exception.RqChallengeApplicationException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class EmployeeService implements IEmployeeService {

    @Autowired
    private EmployeeClient employeeClient;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeClient.getAllEmployees();
    }

    @Override
    public List<Employee> getEmployeesByNames(String searchString) {
        return employeeClient.getAllEmployees()
                .stream()
                .filter(e -> e.getEmployeeName().contains(searchString))
                .collect(Collectors.toList());
    }

    @Override
    public Employee getEmployeeById(String id) {
        return employeeClient.getSingleEmployee(id);
    }

    @Override
    public Integer getHighestSalary() {
        Optional<Employee> highestSalaryEmployee = employeeClient.getAllEmployees()
                .stream()
                .max(Comparator.comparingInt(Employee::getEmployeeSalary));
        if (highestSalaryEmployee.isEmpty()) {
            return 0;
        }
        return highestSalaryEmployee.get().getEmployeeSalary();
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return employeeClient.getAllEmployees()
                .stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
    }

    @Override
    public String createEmployee(CreateEmployeeRequest createRequest) {
        if (createRequest.isInvalid()) {
            throw new RqChallengeApplicationException("Create request does not have required input", HttpStatus.BAD_REQUEST);
        }
        return employeeClient.createEmployee(
                createRequest.getName(),
                createRequest.getSalary(),
                createRequest.getAge()
        );
    }

    @Override
    public String deleteEmployeeById(String id) {
        return employeeClient.deleteEmployeeById(id);
    }

}
