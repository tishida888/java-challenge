package jp.co.axa.apidemo.security;

import jp.co.axa.apidemo.repositories.AccountRepository;
import jp.co.axa.apidemo.services.DemoUserDetailsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 
import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.filter.GenericFilterBean;


/**
 *  Security Config class that mainly manage all authentication configs for API.
 *    - Specify URL for authentication, login, logout etc
 *    - Specify handlers for each events such as authentication success, failure etc
 */

@Configuration
@EnableWebSecurity
@Slf4j
public class DemoSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccountRepository accountRepository;

    // API path (like /api/v1/)
    @Value("${axa.java.challenge.api.path}")
    private String apiPath = "";

    // secret key that is used in hash algorithm
    @Value("${security.admin.key:secret}")
    private String secretKey = "secretkey";

    // Token Expiration Time
    @Value("${jwt.token.expiration.period}")
    private Long expirationTime = 10L;


    // Specify URLs that should be permitted in authentication
    private static final String[] AUTH_WHITELIST = {
        // -- Swagger UI v3
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/webjars/**",
        // -- error page
        "/error/**",
    };

    /**
     *  Ignore URL pattern on authentication. Images, js and style sheet etc should be excluded from authentication.
     *  ***I guess*** that h2-console is only a temporal usage because of just code testing purpose. So I add it in
     *  ignore list here.
     * @param WebSecurity
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
    // @formatter:off
    web
      .debug(false)
      // ### IgnoredRequestConfigurer
      .ignoring()
          .antMatchers("/images/**", "/js/**", "/css/**", "/h2-console/**")
      .and()
    ;
    // @formatter:on
    }


    /**
     * Main Configuration for authentiction setting on API
     * @param HttpSecurity
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        log.info("API Path : " + apiPath+"token");

        // @formatter:off
        http
            // Authentication URL configuration
            //  - URL in AUTH_WHITELIST should be all permitted
            //  - Only admin user has access to '/api/v1/user/**'
            .authorizeRequests()
                .mvcMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers(apiPath+"account/**").hasRole("ADMIN")
                .anyRequest()
                    .authenticated()
            .and()


            // Handler setting when client requests to some URL without token
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .accessDeniedPage("/error/denied")
            .and()


            // Login URL setting (token API) and specify user name and password params
            .formLogin()
                .loginProcessingUrl(apiPath+"token").permitAll()
                    .usernameParameter("name")
                    .passwordParameter("password")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
            .and()


            // Logout URL setting (token API)
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
            .and()

            // disable iframe for h2-console
            .headers().frameOptions().disable()
            .and()

            // Disable CSRF as API is using JWT token authentication instead
            .csrf()
                .disable()


            // Token Authentication as implemented in tokenFilter
            .addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class)

            // Set as stateless (i.e. don't use session)
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ;

        // @formatter:on
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(true)
                .userDetailsService(demoUserService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean("DemoUserDetailsService")
    public DemoUserDetailsService demoUserService() {
        return new DemoUserDetailsService(accountRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private GenericFilterBean tokenFilter() {
        return new DemoTokenFilter(accountRepository, secretKey);
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new DemoAuthenticationEntryPoint();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new DemoAccessDeniedHandler();
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new DemoAuthenticationSuccessHandler(secretKey, expirationTime);
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return new DemoAuthenticationFailureHandler();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return new HttpStatusReturningLogoutSuccessHandler();
    }

}
