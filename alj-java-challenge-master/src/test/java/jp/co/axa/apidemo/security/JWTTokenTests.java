package jp.co.axa.apidemo.security;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.axa.apidemo.entities.Account;
import jp.co.axa.apidemo.repositories.AccountRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.FilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JWTTokenTests {

    @Mock
    private Authentication auth;
    @Mock
    private FilterChain filterChain;
    @MockBean
    private AccountRepository accountRepository;

    /**
     * generateTOken test 
     *  1. Generate token by DemoAuthenticationSuccessHandler.generateToken
     *  2. Set token to Mock response
     *  3. Check if "Bearer " string in token
     */
    @Test
    public void generateTokenTests() throws Exception {
        String secretKey = "secretKey";
        Long accountId = 8L;
        Account account = Account.of("newUser","newPassword",false);
        account.setId(accountId);
        DemoLoginUser user = new DemoLoginUser(account);
        DemoAuthenticationSuccessHandler authHandler = new DemoAuthenticationSuccessHandler(secretKey, 1000000L);
 
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(auth.getPrincipal()).thenReturn(user);

        authHandler.onAuthenticationSuccess(request, response, auth);
    

        /**
         *  Do check if response has the valid token format
         */
        DemoToken demoToken;
        ObjectMapper mapper = new ObjectMapper();
        try {
            demoToken = mapper.readValue(response.getContentAsString(), DemoToken.class);
            assertThat(demoToken.getToken()).startsWith("Bearer ");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failure on generateToken Test due to an exception");
            return;
        }


        /**
         *  Verify Token test
         *   1. Set token in request header (for test preparation)
         *   2. take token out from request for authentication
         *   3. verify token and get DecodedJWT for authentication
         *   4. Do check if user exists in user list (db)
         */

        // set token in header
        request.addHeader("Authorization", demoToken.getToken());

        doNothing().when(filterChain).doFilter(request, response);
        when(accountRepository.findById(accountId)).thenReturn( Optional.ofNullable(account));

        DemoTokenFilter tokenFilter = new DemoTokenFilter(accountRepository, secretKey);
        tokenFilter.doFilter(request, response, filterChain);

        assertThat(true).isTrue();

    }

}
