package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.exception.RqChallengeApplicationException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IEmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IEmployeeService employeeService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    private Employee employeeMock = new Employee();

    @Test
    void returns200WhenGetsAllEmployeesSuccessfully() throws Exception {
        List<Employee> employeesMockList = new ArrayList<>();
        employeeMock.setEmployeeName("Test Name");
        employeesMockList.add(employeeMock);

        when(employeeService.getAllEmployees()).thenReturn(employeesMockList);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Test Name")));
    }

    @Test
    void returns4xxWhenGetsAllEmployeesHasClientError() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new RqChallengeApplicationException("Error", HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void returns200WhenGetSingleEmployeeIsSuccessful() throws Exception {
        employeeMock.setEmployeeAge(22);
        when(employeeService.getEmployeeById("1")).thenReturn(employeeMock);

        mockMvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("22")));
    }

    @Test
    void returns4xxWhenGetSingleEmployeeHasClientError() throws Exception {
        when(employeeService.getEmployeeById("1")).thenThrow(new RqChallengeApplicationException("Error", HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void returns200WhenGetBySearchStringIsSuccessful() throws Exception {
        List<Employee> employeesMockList = new ArrayList<>();
        employeeMock.setEmployeeName("Alex");
        employeesMockList.add(employeeMock);

        when(employeeService.getEmployeesByNames("Alex")).thenReturn(employeesMockList);

        mockMvc.perform(get("/search/Alex"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Alex")));
    }

    @Test
    void returns4xxWhenGetEmployeeBySearchStringHasClientError() throws Exception {
        when(employeeService.getEmployeesByNames("Alex")).thenThrow(new RqChallengeApplicationException("Error", HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/search/Alex"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void returns200WhenGetHighestSalaryIsSuccessful() throws Exception {
        when(employeeService.getHighestSalary()).thenReturn(1000000);

        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("1000000")));
    }

    @Test
    void returns4xxWhenGetHighestSalaryReturnsClientError() throws Exception {
        when (employeeService.getHighestSalary()).thenThrow(new RqChallengeApplicationException("Error", HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void returns200WhenGetTopEarnersIsSuccessful() throws Exception {
        List<String> highestSalaryMockList = new ArrayList<>();
        highestSalaryMockList.add("Alex");
        highestSalaryMockList.add("Andres");

        when (employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(highestSalaryMockList);

        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Alex")))
                .andExpect(content().string(Matchers.containsString("Andres")));
    }

    @Test
    void returns4xxWhenGetTopEarnersReturnsClientError() throws Exception {
        when (employeeService.getTopTenHighestEarningEmployeeNames()).thenThrow(new RqChallengeApplicationException("Error", HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void returns200WhenCreateEmployeeIsSuccessful() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Alex", "100000", "25");

        when(employeeService.createEmployee(any())).thenReturn("Alex");

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Alex")));
    }

    @Test
    void returns4xxWhenCreateEmployeeReturnsClientError() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Alex", "100000", "25");

        when(employeeService.createEmployee(any())).thenThrow(new RqChallengeApplicationException("Error", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void returns200WhenDeleteEmployeeIsSuccessful() throws Exception {
        when(employeeService.deleteEmployeeById("1")).thenReturn("Alex");

        mockMvc.perform(delete("/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Alex")));
    }

    @Test
    void returns4xxWhenDeleteEmployeeHasClientError() throws Exception {
        when(employeeService.deleteEmployeeById("1")).thenThrow(new RqChallengeApplicationException("Error", HttpStatus.BAD_REQUEST));

        mockMvc.perform(delete("/1"))
                .andExpect(status().is4xxClientError());
    }
}
