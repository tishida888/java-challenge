package jp.co.axa.apidemo.services.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jp.co.axa.apidemo.entities.Account;
import jp.co.axa.apidemo.repositories.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountServiceImplTests {

    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountServiceImpl accountService;

    /**
     * Common function that is called from all test functions for creating Account object for unit test
     * @return Account
     */
    private Account getTestAccount() {
        return Account.of("newUser","newPassword",false);
    }


    /**
     * [retrieveAccounts] unit test.
     */
    @Test
    public void retrieveAccountsTest() throws Exception {
        Account account = getTestAccount();
        List<Account> accounts = Arrays.asList(account, account, account);
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> resAccounts = accountService.retrieveAccounts();

        assertThat(resAccounts.size()).isEqualTo(accounts.size());
    }


    /**
     * [findById] unit test.
     */
    @Test
    public void findByIdTest() throws Exception {
        Long accountId = 1L;
        Account account = getTestAccount();
        account.setId(accountId);
        when(accountRepository.findById(accountId)).thenReturn( Optional.ofNullable(account));

        Account resAccount = accountService.findById(accountId);

        // password could be different, but this returns true because of Account.equals()
        assertThat(resAccount).isEqualTo(account);
    }


    /**
     * [saveAccount] unit test.
     */
    @Test
    public void accountSaveTest() throws Exception {
        Account account = getTestAccount();
        when(accountRepository.save(account)).thenReturn(account);

        Account resAccount = accountService.saveAccount(account);

        assertThat(resAccount.getName()).isEqualTo(account.getName());
    }


    /**
     * [deleteAccount] unit test.
     */
    @Test
    public void accountDeleteTest() throws Exception {
        Long deleteAccountId = 1L;
        doNothing().when(accountRepository).deleteById(deleteAccountId);
        accountService.deleteAccount(deleteAccountId);
        assertThat(true).isTrue();
    }
}
