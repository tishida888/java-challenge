package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.security.DemoLoginUser;
import jp.co.axa.apidemo.repositories.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * DefaultUserService used in authentication process. 
 */
@Slf4j
public class DemoUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public DemoUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Load User by name and return
     * @param name
     */
    @Override
    public UserDetails loadUserByUsername(final String name) {
        log.info("Search by Name : " + name);
        // Search user by name
        return accountRepository.findFirstByName(name)
                .map(DemoLoginUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

}
