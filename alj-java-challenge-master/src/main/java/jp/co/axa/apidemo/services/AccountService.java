package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Account;

import java.util.List;

/**
 * AccountService interface
 */
public interface AccountService {

    public List<Account> retrieveAccounts();

    Account findById(Long accountId);

    public Account saveAccount(Account account);

    public void deleteAccount(Long accountId);

}
