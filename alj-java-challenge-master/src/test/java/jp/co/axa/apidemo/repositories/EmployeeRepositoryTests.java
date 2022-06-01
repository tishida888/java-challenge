package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EmployeeRepositoryTests {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private EmployeeRepository employeeRepository;


    /**
     * findById simple test case
     */
    @Test
    public void findById() {
        Employee expected = testEntityManager.persistFlushFind(Employee.of("user1", 100, "tech"));

        Optional<Employee> account = employeeRepository.findById(expected.getId());
        Employee actual = account.orElseThrow(RuntimeException::new);
        assertThat(actual).isEqualTo(expected);
    }

}
