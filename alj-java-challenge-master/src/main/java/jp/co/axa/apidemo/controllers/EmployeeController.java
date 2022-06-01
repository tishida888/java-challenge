package jp.co.axa.apidemo.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Api(tags = "Employee")
@RestController
@RequestMapping(path="/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    /**
     * getEmployees :
     *    Return the list of employees
     * @return List<Employee>
     */
    @ApiOperation("Get all employees")
    @GetMapping("/employees")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public List<Employee> getEmployees() {
        try {
            List<Employee> employees = employeeService.retrieveEmployees();
            return employees;
        } catch(Exception e) {
            log.debug("Exception on getEmployees. Exception : " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get all employees. Please contact support.");
        }
    }


    /**
     * getEmployee :
     *     Get Employee by employee ID
     * @param employeeId
     * @return
     */
    @ApiOperation("Get employees by ID")
    @GetMapping("/employees/{employeeId}")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public Employee getEmployee(@PathVariable(name="employeeId")Long employeeId) {
        try {
            return employeeService.getEmployee(employeeId);
        } catch(NoSuchElementException e) {
            log.debug("Employee Not Found. ID = " + employeeId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee Not Found. ID = " + employeeId);
        } catch(Exception e) {
            log.debug("Exception on getEmployee. Exception : " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get employee. Please contact support.");
        }
    }


    /**
     * Register a new employee. Invoke saveEmployee method in service to save the data.
     * @param employee
     * @return employee
     * @throws Exception
     */
    @ApiOperation("Register new employee")
    @PostMapping("/employees")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public Employee saveEmployee(Employee employee) throws Exception {
        return _saveEmployee(employee);
    }

    /**
     * Actual control for saving employee
     * @param employee
     */
    private Employee _saveEmployee(Employee employee) {
        try {
            Employee emp = employeeService.saveEmployee(employee);
            log.info("Employee Saved Successfully");
            return emp;
        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal Argument error. Please validate your input and try again.");
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save Employee. Please contact support.");
        }
    }


    /**
     * Delete existing employee by ID
     * @param employeeId
     */
    @ApiOperation("Delete employee by ID")
    @DeleteMapping("/employees/{employeeId}")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public void deleteEmployee(@PathVariable(name="employeeId")Long employeeId){
        _deleteEmployee(employeeId);
    } 

    /**
     * Actual control for deleting employee
     * @param employeeId
     */
    private void _deleteEmployee(Long employeeId) {
        try {
            employeeService.deleteEmployee(employeeId);
            log.info("Employee Deleted Successfully");    
        } catch(NoSuchElementException e) {
            log.debug("Employee Not Found. ID = " + employeeId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee Not Found. ID = " + employeeId);
        } catch(EmptyResultDataAccessException e) {
            log.debug("Employee Not Found. ID = " + employeeId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee Not Found. ID = " + employeeId);
        } catch(Exception e) {
            log.debug("Exception on _deleteEmployee. Exception : " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete Employee. Please contact support.");
        }
    }

    /**
     * Update existing employee's fields by ID
     * @param employee
     * @param employeeId
     * @return Employee
     */
    @ApiOperation("Update employee fields by ID")
    @PutMapping("/employees/{employeeId}")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public Employee updateEmployee(@RequestBody Employee employee,
                                   @PathVariable(name="employeeId")Long employeeId) {
        try {
            Employee emp = getEmployee(employeeId);

            // If ID is consistent, just update existing employee record
            if(emp.getId() == employee.getId()) {
                log.info("Update Employee : Original Employee=" + emp.toString());
                return employeeService.updateEmployee(employee);

            // otherwise delete the existing record and add new employee
            } else {
                _deleteEmployee(employeeId);
                return _saveEmployee(employee);
            }

        } catch(NoSuchElementException e) {
            log.info("Update Employee : Employee Not Found. ID=" + employeeId.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee Not Found. ID = " + employeeId);

        } catch (Exception e) {
            log.debug("Exception on updateEmployee. Exception : " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on updating employee. Please contact support.");
        }

    }

}
