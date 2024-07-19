package com.example.rqchallenge.client;

import com.example.rqchallenge.dto.GetAllEmployeesResponseDto;
import com.example.rqchallenge.dto.GetSingleEmployeeResponseDto;
import com.example.rqchallenge.exception.RqChallengeApplicationException;
import com.example.rqchallenge.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
class EmployeeClientTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @Value("${dummyService.host}")
    private String host;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    Employee employee = new Employee("1", "Alex Marson", 100000, 25, "profileImg");
    Employee employee2 = new Employee("2", "Bob Smith", 20000, 30, "profileImg");


    @Test
    void canGetAllEmployeesWhenResponseFromExternalServiceSuccessful() throws URISyntaxException, JsonProcessingException {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee);
        employeeList.add(employee2);

        GetAllEmployeesResponseDto response = new GetAllEmployeesResponseDto(
                "Success",
                employeeList
        );

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employees")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response))
                );
        List<String> actualResponseNames = employeeClient.getAllEmployees()
                .stream()
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
        mockServer.verify();

        assertTrue(
                "Should contain expected employee",
                actualResponseNames.contains("Alex Marson")
        );

        assertTrue(
                "Should contain expected employee",
                actualResponseNames.contains("Bob Smith")
        );

    }

    @Test
    void returnsEmptyListWhenExternalResponseNull() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employees")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );
        List<Employee> actualResponse = employeeClient.getAllEmployees();
        mockServer.verify();

        assertTrue(
                "Should return empty response",
                actualResponse.isEmpty()
        );

    }

    @Test
    void handles5xxFromExternalService() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employees")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.getAllEmployees());
        mockServer.verify();

        assertEquals(
                "Should handle 500 error from external service",
                "Internal Server Error",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatus()
        );
    }

    @Test
    void handles4xxFromExternalServiceWhenGetAll() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employees")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.getAllEmployees());
        mockServer.verify();

        assertEquals(
                "Should handle 4xx error from external service",
                "Bad Request",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.BAD_REQUEST,
                exception.getStatus()
        );
    }

    @Test
    void canGetSingleEmployeeWhenExternalResponseSuccess() throws JsonProcessingException, URISyntaxException {
        GetSingleEmployeeResponseDto response = new GetSingleEmployeeResponseDto(
                "Success",
                employee
        );

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response))
                );
        String actualResponseName = employeeClient.getSingleEmployee("1").getEmployeeName();
        mockServer.verify();

        assertEquals(
                "Should match expected response",
                "Alex Marson",
                actualResponseName
        );
    }

    @Test
    void returnsNullWhenExternalResponseNull() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        Employee actualResponse = employeeClient.getSingleEmployee("1");
        mockServer.verify();

        assertEquals(
                "Should match expected response",
                null,
                actualResponse
        );
    }

    @Test
    void handles4xxFromExternalServiceWhenGetSingle() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.getSingleEmployee("1"));
        mockServer.verify();

        assertEquals(
                "Should handle 4xx error from external service",
                "Bad Request",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.BAD_REQUEST,
                exception.getStatus()
        );
    }

    @Test
    void handles5xxFromExternalServiceWhenGetSingle() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.getSingleEmployee("1"));
        mockServer.verify();

        assertEquals(
                "Should handle 5xx error from external service",
                "Internal Server Error",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatus()
        );
    }

    @Test
    void ableToDeleteWhenResponseFromExternalServiceSuccessful() throws URISyntaxException, JsonProcessingException {
        // getting the employee to delete
        GetSingleEmployeeResponseDto response = new GetSingleEmployeeResponseDto(
                "Success",
                employee
        );
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response))
                );

        // executing the deletion
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/delete/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        String actualName = employeeClient.deleteEmployeeById("1");
        mockServer.verify();

        assertEquals(
                "Delete employee should match expected",
                "Alex Marson",
                actualName
        );
    }

    @Test
    void doesntDeleteWhenNoEmployeeWithIdExists() throws URISyntaxException {
        // getting the empty employee response
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        String actualName = employeeClient.deleteEmployeeById("1");
        mockServer.verify();

        assertEquals(
                "No employee should be deleted and empty string returned",
                "",
                actualName
        );
    }

    @Test
    void handles4xxFromExternalServiceWhenDelete() throws URISyntaxException, JsonProcessingException {
        // getting the employee to delete - successful response
        GetSingleEmployeeResponseDto response = new GetSingleEmployeeResponseDto(
                "Success",
                employee
        );
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response))
                );

        // 4xx when deleting
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/delete/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.deleteEmployeeById("1"));
        mockServer.verify();

        assertEquals(
                "Should handle 4xx error from external service",
                "Bad Request",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.BAD_REQUEST,
                exception.getStatus()
        );
    }

    @Test
    void handles5xxFromExternalServiceWhenDelete() throws URISyntaxException, JsonProcessingException {
        // getting the employee to delete - successful response
        GetSingleEmployeeResponseDto response = new GetSingleEmployeeResponseDto(
                "Success",
                employee
        );
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/employee/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response))
                );

        // 4xx when deleting
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/delete/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.deleteEmployeeById("1"));
        mockServer.verify();

        assertEquals(
                "Should handle 5xx error from external service",
                "Internal Server Error",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatus()
        );
    }

    @Test
    void returnsStatusWhenAbleToCreateEmployeeSuccessfully() throws JsonProcessingException, URISyntaxException {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("name", "Alex");
        request.add("salary", "100000");
        request.add("age", "25");

        GetSingleEmployeeResponseDto response = new GetSingleEmployeeResponseDto(
                "Success",
                employee
        );
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/create")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formData(request))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response))
                );

        String responseStatus = employeeClient.createEmployee("Alex", "100000", "25");
        mockServer.verify();

        assertEquals(
                "Should return response of create",
                "Success",
                responseStatus
        );
    }

    @Test
    void handlesNullResponseFromExternalServiceWhenCreate() throws URISyntaxException {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("name", "Alex");
        request.add("salary", "100000");
        request.add("age", "25");

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/create")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formData(request))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.createEmployee("Alex", "100000", "25"));

        assertEquals(
                "Should give helpful message when null create response",
                "Create employee returned null response, please try again",
                exception.getMessage()
        );

        assertEquals(
                "Should give server error when create response null",
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatus()
        );

    }

    @Test
    void handles5xxFromExternalServiceWhenCreate() throws URISyntaxException {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("name", "Alex");
        request.add("salary", "100000");
        request.add("age", "25");

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/create")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formData(request))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.createEmployee("Alex", "100000", "25"));
        mockServer.verify();

        assertEquals(
                "Should handle 5xx error from external service",
                "Internal Server Error",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getStatus()
        );
    }

    @Test
    void handles4xxFromExternalServiceWhenCreate() throws URISyntaxException {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("name", "Alex");
        request.add("salary", "100000");
        request.add("age", "25");

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(host + "/api/v1/create")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().formData(request))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("")
                );

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () ->
                employeeClient.createEmployee("Alex", "100000", "25"));
        mockServer.verify();

        assertEquals(
                "Should handle 4xx error from external service",
                "Bad Request",
                exception.getMessage()
        );

        assertEquals(
                "Exception status should match response status",
                HttpStatus.BAD_REQUEST,
                exception.getStatus()
        );
    }
}
