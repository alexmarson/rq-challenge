package com.example.rqchallenge.client;

import com.example.rqchallenge.dto.GetSingleEmployeeResponseDto;
import com.example.rqchallenge.dto.GetAllEmployeesResponseDto;
import com.example.rqchallenge.exception.RqChallengeApplicationException;
import com.example.rqchallenge.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${dummyService.host}")
    private String externalEmployeeServiceHost;

    @Value("${dummyService.getAllPath}")
    private String getAllPath;

    @Value("${dummyService.getSinglePath}")
    private String getSinglePath;

    @Value("${dummyService.createPath}")
    private String createPath;

    @Value("${dummyService.deletePath}")
    private String deletePath;

    Logger logger = LoggerFactory.getLogger(EmployeeClient.class);

    public List<Employee> getAllEmployees() {
        GetAllEmployeesResponseDto getEmployeeResponse = restTemplate.getForObject(
                createFullRoute(getAllPath),
                GetAllEmployeesResponseDto.class
        );
        if (getEmployeeResponse == null) {
            logger.info("GET all employees returned null response");
            return new ArrayList<>();
        }
        return getEmployeeResponse.data;
    }

    public Employee getSingleEmployee(String id) {
        String getSingleEmployeePath = getSinglePath + id;
        GetSingleEmployeeResponseDto getSingleEmployeeResponse = restTemplate.getForObject(
                createFullRoute(getSingleEmployeePath),
                GetSingleEmployeeResponseDto.class
        );
        if (getSingleEmployeeResponse == null) {
            logger.info("GET single employee returned empty response");
            return null;
        }
        return getSingleEmployeeResponse.data;
    }

    public String createEmployee(String name, String salary, String age) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("name", name);
        request.add("salary", salary);
        request.add("age", age);
        GetSingleEmployeeResponseDto createEmployeeResponse = restTemplate.postForObject(
                createFullRoute(createPath),
                request,
                GetSingleEmployeeResponseDto.class
        );
        if (createEmployeeResponse == null) {
            logger.error("CREATE employee returned null but successful response");
            throw new RqChallengeApplicationException("Create employee returned null response, please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return createEmployeeResponse.getStatus();
    }

    public String deleteEmployeeById(String id) {
        String deletePathFull = deletePath + id;
        Employee employeeToDelete = getSingleEmployee(id);
        if (employeeToDelete == null) {
            logger.info("No employee with id :: " + id + " exists, no need to delete");
            return "";
        }
        logger.warn("Deleting Employee record with name :: " + employeeToDelete.getEmployeeName());
        restTemplate.delete(createFullRoute(deletePathFull));
        return employeeToDelete.getEmployeeName();
    }

    private String createFullRoute(String path) {
        return externalEmployeeServiceHost + path;
    }
}
