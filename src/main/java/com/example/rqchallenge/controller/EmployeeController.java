package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class EmployeeController implements IEmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        List<Employee> employeeList = employeeService.getAllEmployees();
        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employeeList = employeeService.getEmployeesByNames(searchString);
        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    public ResponseEntity<Employee> getEmployeeById(String id) {
        return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
    }

    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return new ResponseEntity<>(employeeService.getHighestSalary(), HttpStatus.OK);
    }

    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return new ResponseEntity<>(employeeService.getTopTenHighestEarningEmployeeNames(), HttpStatus.OK);
    }

    public ResponseEntity<String> createEmployee(CreateEmployeeRequest createRequest) {
        return new ResponseEntity<>(employeeService.createEmployee(createRequest), HttpStatus.OK);
    }

    public ResponseEntity<String> deleteEmployeeById(String id) {
        return new ResponseEntity<>(employeeService.deleteEmployeeById(id), HttpStatus.OK);
    }
}
