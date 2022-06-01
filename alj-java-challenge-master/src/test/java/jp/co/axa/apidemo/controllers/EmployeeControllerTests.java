package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Account;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.AccountRepository;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import jp.co.axa.apidemo.security.DemoLoginUser;
import jp.co.axa.apidemo.services.impl.EmployeeServiceImpl;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(value = EmployeeController.class)
public class EmployeeControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeServiceImpl employeeService;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private AccountRepository accountRepository;

    MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));

    // define adminAccount for authentication
    DemoLoginUser loginUser = new DemoLoginUser( Account.of("admin1","admin1",true) );

    /**
     * [getEmployees] function unit test on controll.
     * It expects to return the list of employees by JSON string.
     * @throws Exception
     */
    @Test
    public void getEmployeesTest() throws Exception {
        Employee employee = Employee.of("emp1",100,"Tech");
        List<Employee> resEmployees = Arrays.asList(employee);
        when(employeeService.retrieveEmployees()).thenReturn(resEmployees);

        RequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/employees")
                            .with(user(loginUser))
                            .accept(MediaType.APPLICATION_JSON);

        String expectedContent = "[{\"id\":null,\"name\":\"emp1\",\"salary\":100,\"department\":\"Tech\"}]";
        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent))
                .andDo(print())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedContent);
    }



    /**
     * Normal behavior test on [getEmployee] function on controll.
     * It expects to return JSON string for Employee object from service (mock here).
     * @throws Exception
     */
    @Test
    public void getEmployeeTest() throws Exception {
        Employee employee = Employee.of("test",100,"tech");
        when(employeeService.getEmployee(5L)).thenReturn(employee);

        RequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/employees/{employeeId}", "5")
                        .with(user(loginUser))
                        .accept(MediaType.APPLICATION_JSON);

        String expectedContent = "{\"id\":null,\"name\":\"test\",\"salary\":100,\"department\":\"tech\"}";
        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent))
                .andDo(print())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedContent);
    }


    /**
     * Normal behavior test on [saveEmployee] function on controll.
     * It expects to return JSON string for Employee object from service (mock here).
     * @throws Exception
     */
    @Test
    public void saveEmployeeTest() throws Exception {
        Employee employee = Employee.of("test",100,"tech");
        when(employeeService.saveEmployee(employee)).thenReturn(employee);

        RequestBuilder builder = MockMvcRequestBuilders.post("/api/v1/employees")
                        .param("name", "test")
                        .param("salary", "100")
                        .param("department", "tech")
                        .with(user(loginUser))
                        .accept(MediaType.APPLICATION_JSON);

        String expectedContent = "{\"id\":null,\"name\":\"test\",\"salary\":100,\"department\":\"tech\"}";
        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent))
                .andDo(print())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedContent);
    }



    /**
     * Exception handling test on [saveEmployee] function. This is to simulate when service throws exception
     * when client sends bad request or some error on service layer. 
     * @throws Exception
     */
    @Test
    public void throwExceptionOnSaveTest() throws Exception {
        Employee employee = Employee.of("test",100,"tech");
        doThrow(IllegalArgumentException.class).when(employeeService).saveEmployee(employee);

        RequestBuilder builder = MockMvcRequestBuilders.post("/api/v1/employees")
                .param("name", "test")
                .param("salary", "100")
                .param("department", "tech")
                .with(user(loginUser))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getErrorMessage().equals("Illegal Argument error. Please validate your input and try again."));
    }


    /**
     * Exception handling test on deleteEmployee function. This is to simulate when service throws exception
     * when client sends bad request (like ID is missing) or some error on service layer. 
     * @throws Exception
     */
    @Test
    public void throwExceptionOnDeleteTest() throws Exception {
        Long deleteEmployeeId = 2L;
        when(employeeService.getEmployee(deleteEmployeeId)).thenReturn(null);
        doThrow(EmptyResultDataAccessException.class).when(employeeService).deleteEmployee(deleteEmployeeId);

        RequestBuilder builder = MockMvcRequestBuilders.delete("/api/v1/employees/{employeeId}", deleteEmployeeId.toString())
                                .with(user(loginUser))
                                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getErrorMessage().equals("Employee Not Found. ID = "+deleteEmployeeId.toString()));
    }


    /**
     * Normal behavior test on [updateEmployee] function on controll.
     * It expects to return JSON string for Employee object from service (mock here).
     * @throws Exception
     */
    @Test
    public void updateEmployeeTest() throws Exception {
        Long updateEmployeeId = 2L;
        Employee employee = Employee.of("test",200,"tech");
        employee.setId(updateEmployeeId);
        when(employeeService.getEmployee(updateEmployeeId)).thenReturn(employee);
        when(employeeService.updateEmployee(employee)).thenReturn(employee);

        String updateContent = "{\"id\":2,\"name\":\"test\",\"salary\":200,\"department\":\"tech\"}";
        RequestBuilder builder = MockMvcRequestBuilders.put("/api/v1/employees/{employeeId}", updateEmployeeId.toString())
                        .with(user(loginUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateContent);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(updateContent))
                .andDo(print())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(updateContent);
    }


    /**
     * Exception handling test on updateEmployee function. This is also to simulate when service throws exception
     * when client sends bad request (like ID is missing) or some error on service layer. 
     * @throws Exception
     */
    @Test
    public void throwExceptionOnUpdateTest() throws Exception {
        Long updateEmployeeId = 2L;
        when(employeeService.getEmployee(updateEmployeeId)).thenReturn(null);

        String updateContent = "{\"id\":2,\"name\":\"test\",\"salary\":200,\"department\":\"tech\"}";
        RequestBuilder builder = MockMvcRequestBuilders.put("/api/v1/employees/{employeeId}", updateEmployeeId.toString())
                                .with(user(loginUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateContent);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getErrorMessage().equals("Error on updating employee. Please contact support."));
    }

}
