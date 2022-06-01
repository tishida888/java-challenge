package jp.co.axa.apidemo.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jp.co.axa.apidemo.entities.Account;
import jp.co.axa.apidemo.repositories.AccountRepository;
import jp.co.axa.apidemo.services.AccountService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Account management service that provide functions to add/update/delete API users.
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private PasswordEncoder encoder;

    public AccountServiceImpl() {
        this.encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Load list of API accounts and return
     * @return List<Account>
     */
    @Cacheable(cacheNames="allAccountCache")
    @Override
    public List<Account> retrieveAccounts() {
        List<Account> accounts = accountRepository.findAll();

        // Delete password on all accounts
        for(Account account : accounts) {
            account.setPassword("");
            log.info("Retrieve Account : " + account.toString());
        }
        return accounts;
    }

    /**
     * Load Account by name and return
     * @param accountId
     * @return Account
     */
    @Cacheable(cacheNames="accountCache", key="#accountId")
    @Override
    public Account findById(Long accountId) {
        log.info("Finding name by " + accountId);
        Objects.requireNonNull(accountId, "accountId must be not null");
        Optional<Account> account = accountRepository.findById(accountId);

        // Hide password
        Account returnAccount = account.get();
        returnAccount.setPassword("");
        return returnAccount;
    }

    /**
     * Save Account and return Account if success.
     * account has raw password and it is encoded before storing to DB
     * cache on [accountCache] and delete all cache on [allAccountCache]
     * @param Account
     * @return Account
     */
//    @CachePut(key="#result.id")
    @Caching(put = {@CachePut(cacheNames="accountCache", key="#result.id")},
             evict = {@CacheEvict(cacheNames="allAccountCache", allEntries = true)})
    @Override
    public Account saveAccount(Account acccount) {
        log.info("Saving Account : " + acccount.toString());
        acccount.setPassword(this.encoder.encode(acccount.getPassword()));
        return accountRepository.save(acccount);
    }

    /**
     * Delete Account by ID
     * cache on both [accountCache] and [allAccountCache] are remoed
     * @param accountId
     */
    @Caching(evict = {@CacheEvict(cacheNames="accountCache", key="#accountId"),
                      @CacheEvict(cacheNames="allAccountCache", allEntries = true)})
    @Override
    public void deleteAccount(Long accountId) {
        log.info("Delete Account by ID : ID =" + accountId.toString());
        accountRepository.deleteById(accountId);
    }

}
