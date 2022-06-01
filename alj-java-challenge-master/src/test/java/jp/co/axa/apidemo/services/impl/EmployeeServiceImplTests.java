package jp.co.axa.apidemo.services.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EmployeeServiceImplTests {

    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    /**
     * Common function that is called from all test functions for creating Employee object for unit test
     * @return Account
     */
    private Employee getTestEmployee() {
        return Employee.of("employee",100,"tech");
    }


    /**
     * [retrieveEmployees] unit test.
     */
    @Test
    public void retrieveAccountsTest() throws Exception {
        Employee employee = getTestEmployee();
        List<Employee> employees = Arrays.asList(employee, employee);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> resEmployees = employeeService.retrieveEmployees();

        assertThat(resEmployees.size()).isEqualTo(employees.size());
    }


    /**
     * [getEmployee] unit test.
     */
    @Test
    public void getEmployeeTest() throws Exception {
        Long employeeId = 1L;
        Employee employee = getTestEmployee();
        employee.setId(employeeId);
        when(employeeRepository.findById(employeeId)).thenReturn( Optional.ofNullable(employee) );

        Employee resEmployee = employeeService.getEmployee(employeeId);

        // compare on all variables on Employee instance
        assertThat(resEmployee).isEqualTo(employee);
    }


    /**
     * [saveEmployee] unit test.
     */
    @Test
    public void saveEmployeeTest() throws Exception {
        Employee employee = getTestEmployee();
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee resEmployee = employeeService.saveEmployee(employee);

        assertThat(resEmployee.getName()).isEqualTo(employee.getName());
    }


    /**
     * [deleteEmployee] unit test.
     */
    @Test
    public void deleteEmployeeTest() throws Exception {
        Long deleteEmployeeId = 1L;
        doNothing().when(employeeRepository).deleteById(deleteEmployeeId);
        employeeService.deleteEmployee(deleteEmployeeId);
        assertThat(true).isTrue();
    }


    /**
     * [updateEmployee] unit test.
     */
    @Test
    public void updateEmployeeTest() throws Exception {
        Employee employee = getTestEmployee();
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee resEmployee = employeeService.saveEmployee(employee);

        assertThat(resEmployee.getName()).isEqualTo(employee.getName());
    }
}
