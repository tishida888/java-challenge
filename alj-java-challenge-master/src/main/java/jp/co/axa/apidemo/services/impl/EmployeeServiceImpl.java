package jp.co.axa.apidemo.services.impl;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Return all registered employees
     * @return List<Employee>
     */
    @Cacheable(cacheNames="allEmployeeCache")
    @Override
    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        log.info(employees.toString());
        return employees;
    }

    /**
     * Return Employee by employee ID
     * @return Employee
     */
    @Cacheable(cacheNames="employeeCache", key="#employeeId")
    @Override
    public Employee getEmployee(Long employeeId) {
        log.info("Get Employee by ID = " + employeeId);
        Optional<Employee> optEmp = employeeRepository.findById(employeeId);
        return optEmp.get();
    }

    /**
     * Save Employee as registration
     * @return Employee (if saved successfully)
     */
    @Caching(put = {@CachePut(cacheNames="employeeCache", key="#result.id")},
             evict = {@CacheEvict(cacheNames="allEmployeeCache", allEntries = true)})
    @Override
    public Employee saveEmployee(Employee employee){
        log.info("Saving Employee : " + employee.toString());
        return employeeRepository.save(employee);
    }

    /**
     * Delete Employee from DB by employee ID
     */
    @Caching(evict = {@CacheEvict(cacheNames="employeeCache", key="#employeeId"),
                      @CacheEvict(cacheNames="allEmployeeCache", allEntries = true)})
    @Override
    public void deleteEmployee(Long employeeId){
        log.info("Delete Employee by ID : ID = " + employeeId.toString());
        employeeRepository.deleteById(employeeId);
    }

    /**
     *  Update Employee fields by employee field passed through API
     */
    @Caching(put = {@CachePut(cacheNames="employeeCache", key="#result.id")},
             evict = {@CacheEvict(cacheNames="allEmployeeCache", allEntries = true)})
    @Override
    public Employee updateEmployee(Employee employee) {
        log.info("Updating Employee : " + employee.toString());
        return employeeRepository.save(employee);
    }
}
