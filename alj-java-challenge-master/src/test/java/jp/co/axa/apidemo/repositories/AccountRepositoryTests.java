package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Account;
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
public class AccountRepositoryTests {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private AccountRepository accountRepository;


    /**
     * findById simple test case
     */
    @Test
    public void findById() {
        Account expected = testEntityManager.persistFlushFind(Account.of("admin1", "admin1", true));

        Optional<Account> account = accountRepository.findById(expected.getId());
        Account actual = account.orElseThrow(RuntimeException::new);
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * findFirstByName simple test case (used in DemoUserDetailsService)
     */
    @Test
    public void findFirstByName() {
        Account expected = testEntityManager.persistFlushFind(Account.of("admin1", "admin1", true));

        Optional<Account> account = accountRepository.findFirstByName(expected.getName());
        Account actual = account.orElseThrow(RuntimeException::new);
        assertThat(actual).isEqualTo(expected);
    }

}
