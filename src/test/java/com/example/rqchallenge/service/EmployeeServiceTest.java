package com.example.rqchallenge.service;

import com.example.rqchallenge.client.EmployeeClient;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.exception.RqChallengeApplicationException;
import com.example.rqchallenge.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    private IEmployeeService employeeService;

    @MockBean
    private EmployeeClient employeeClient;


    @Test
    void shouldReturnAllEmployeesWhenClientResponseSuccessful() {
        Employee employee = generateEmployee(
                "1",
                "Alex Marson",
                100000,
                25,
                "profileImage"
        );
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee);
        when(employeeClient.getAllEmployees()).thenReturn(employeeList);

        assertEquals(
                "Employee list should match mock client response",
                employeeList,
                employeeService.getAllEmployees()
        );
    }

    @Test
    void shouldReturnEmptyEmployeeListWhenClientResponseEmpty() {
        when(employeeClient.getAllEmployees()).thenReturn(new ArrayList<>());

        assertTrue("List should be empty", employeeService.getAllEmployees().isEmpty());

    }

    @Test
    void shouldReturnSingleEmployeeWhenGivenId() {
        Employee employee = generateEmployee(
                "1",
                "Ryan Gosling",
                1000000,
                30,
                "profileImage"
        );
        when(employeeClient.getSingleEmployee("1")).thenReturn(employee);

        assertEquals(
                "Employee should match mock client response",
                employee,
                employeeService.getEmployeeById("1")
        );
    }

    @Test
    void shouldReturnNullEmployeeWhenClientReturnsNullResponse() {
        when(employeeClient.getSingleEmployee("1")).thenReturn(null);

        assertEquals(
                "Employee should be null",
                null,
                employeeService.getEmployeeById("1")
        );
    }

    @Test
    void shouldReturnMatchingNameEmployeesWhenClientResponseNotEmpty() {
        Employee employee1 = generateEmployee(
                "1",
                "Rob Smith",
                30000,
                25,
                "profileImg"
        );

        Employee employee2 = generateEmployee(
                "2",
                "Robert White",
                50000,
                32,
                "profileImg"
        );

        Employee employee3 = generateEmployee(
                "3",
                "Sarah Smith",
                60000,
                23,
                "profileImage"
        );

        List<Employee> employeesList = new ArrayList<>();
        employeesList.add(employee1);
        employeesList.add(employee2);
        employeesList.add(employee3);

        when(employeeClient.getAllEmployees()).thenReturn(employeesList);

        List<Employee> actual = employeeService.getEmployeesByNames("Rob");

        assertTrue("Should return Robert", actual.contains(employee1));
        assertTrue("Should return Rob", actual.contains(employee2));
        assertFalse("Should not return Sarah", actual.contains(employee3));

    }

    @Test
    void shouldReturnNoEmployeesWhenNoEmployeesMatchString() {
        Employee employee1 = generateEmployee(
                "1",
                "Rob Smith",
                30000,
                25,
                "profileImg"
        );

        Employee employee2 = generateEmployee(
                "2",
                "Robert White",
                50000,
                32,
                "profileImg"
        );

        Employee employee3 = generateEmployee(
                "3",
                "Sarah Smith",
                60000,
                23,
                "profileImage"
        );

        List<Employee> employeesList = new ArrayList<>();
        employeesList.add(employee1);
        employeesList.add(employee2);
        employeesList.add(employee3);

        when(employeeClient.getAllEmployees()).thenReturn(employeesList);

        List<Employee> actual = employeeService.getEmployeesByNames("Elmo");

        assertFalse("Should not return Robert", actual.contains(employee1));
        assertFalse("Should not return Rob", actual.contains(employee2));
        assertFalse("Should not return Sarah", actual.contains(employee3));
        assertTrue("List should return nothing", actual.isEmpty());

    }

    @Test
    void shouldReturnEmptyListWhenClientResponseIsEmpty() {
        when(employeeClient.getAllEmployees()).thenReturn(new ArrayList<>());

        assertTrue(
                "List should be empty",
                employeeService.getEmployeesByNames("Rob").isEmpty()
        );
    }

    @Test
    void shouldReturnTopTenHighestEarnersWhenClientResponseNotEmpty() {
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            Employee employee = generateEmployee(
                    String.valueOf(i),
                    "Name " + i,
                    1000 + i,
                    22,
                    "profileImage"
            );
            employeeList.add(employee);
        }

        when(employeeClient.getAllEmployees()).thenReturn(employeeList);

        List<String> highestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(
                "List should only be size 10",
                10,
                highestEarningEmployeeNames.size()
        );

        //Should not have the employee names from 0 - 10
        for (int i = 0; i <= 10; i++ ) {
            assertFalse(
                    "Should not have low earner in list",
                    highestEarningEmployeeNames.contains("Name " + i)
            );
        }

        //Should have all the employee names from 11 - 20
        for (int i = 11; i <= 20; i ++) {
            assertTrue(
                    "Should have high earner in list",
                    highestEarningEmployeeNames.contains("Name " + i)
            );
        }
    }

    @Test
    void shouldReturnTopEarnersWhenClientResponseLessThan10() {
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i <= 8; i++) {
            Employee employee = generateEmployee(
                    String.valueOf(i),
                    "Name " + i,
                    1000 + i,
                    22,
                    "profileImage"
            );
            employeeList.add(employee);
        }

        when(employeeClient.getAllEmployees()).thenReturn(employeeList);

        List<String> highestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertTrue(
                "List should be size 10 or less",
                highestEarningEmployeeNames.size() <= 10
        );

        //Should have all the employee names since response less than 10
        for (int i = 0; i <= 8; i++ ) {
            assertTrue(
                    "Should have all employees in list",
                    highestEarningEmployeeNames.contains("Name " + i)
            );
        }
    }

    @Test
    void shouldReturnNoHighEarnEmployeesWhenClientResponseEmpty() {
        when(employeeClient.getAllEmployees()).thenReturn(new ArrayList<>());

        assertTrue(
                "Should not return any high earners",
                employeeService.getTopTenHighestEarningEmployeeNames().isEmpty()
        );
    }

    @Test
    void shouldReturnSingleHighestSalaryWhenClientResponseNotEmpty() {
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            Employee employee = generateEmployee(
                    String.valueOf(i),
                    "Name " + i,
                    1000 + i,
                    22,
                    "profileImage"
            );
            employeeList.add(employee);
        }

        when(employeeClient.getAllEmployees()).thenReturn(employeeList);

        assertEquals(
                "Should return single highest salary",
                1020,
                employeeService.getHighestSalary()
        );
    }

    @Test
    void shouldReturnZeroSalaryWhenClientResponseEmpty() {
        when(employeeClient.getAllEmployees()).thenReturn(new ArrayList<>());

        assertEquals(
                "Should return 0 salary",
                0,
                employeeService.getHighestSalary()
        );
    }

    @Test
    void shouldCreateEmployeeWhenRequestIsValid() {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("Alex", "1000000", "25");

        when(employeeClient.createEmployee("Alex", "1000000", "25")).thenReturn("Success!");

        assertEquals(
                "Should have successful response",
                "Success!",
                employeeService.createEmployee(createEmployeeRequest)
        );
    }

    @Test
    void shouldThrowExceptionWhenCreateRequestIsInvalid() {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(null, "Salary", "");

        RqChallengeApplicationException exception = assertThrows(RqChallengeApplicationException.class, () -> {
            employeeService.createEmployee(createEmployeeRequest);
        });

        assertEquals(
                "Should have thrown helpful exception",
                "Create request does not have required input",
                exception.getMessage()
        );

        assertEquals(
                "Should have bad request status",
                HttpStatus.BAD_REQUEST,
                exception.getStatus()
        );
    }

    @Test
    void shouldDeleteEmployeeWhenGivenId() {
        when(employeeClient.deleteEmployeeById("1")).thenReturn("Deleted Employee Name");

        assertEquals(
                "Should return name of deleted employee",
                "Deleted Employee Name",
                employeeService.deleteEmployeeById("1")
        );
    }

    private Employee generateEmployee(
            String id,
            String name,
            Integer salary,
            Integer age,
            String profileImage
    ) {
        return new Employee(
                id,
                name,
                salary,
                age,
                profileImage
        );
    }
}
