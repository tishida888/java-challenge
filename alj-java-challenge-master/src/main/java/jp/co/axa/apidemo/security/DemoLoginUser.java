package jp.co.axa.apidemo.security;

import jp.co.axa.apidemo.entities.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;


/**
 *  Login User class that populates name, roles etc from DB and it's used to identify
 *  if login user has access to 'Account' API that manages account list.
 *  Only "ROLE_ADMIN" role is allowed to manage account list. Otherwise user doesn't have
 *  access to 'Account' API.
 */
public class DemoLoginUser extends org.springframework.security.core.userdetails.User {

    // User Entity
    private Account account;

    public Account getAccount() {
        return account;
    }

    public DemoLoginUser(Account account) {
        super(account.getName(), account.getPassword(), determineRoles(account.getAdmin()));
        this.account = account;
    }

    // Role prefix has to be "ROLE_" as it's appended when hasRole is called (in security/DemoSecurityConfig.java)
    private static final List<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER");
    private static final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");

    private static List<GrantedAuthority> determineRoles(boolean isAdmin) {
        return isAdmin ? ADMIN_ROLES : USER_ROLES;
    }
}
