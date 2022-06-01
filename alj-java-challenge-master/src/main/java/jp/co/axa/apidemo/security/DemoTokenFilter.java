package jp.co.axa.apidemo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jp.co.axa.apidemo.repositories.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 *  DemoTokenFilter :
 *    This class does the authentication verification when client sends a request with bearer token
 *    that is registered in Token API. 
 *    If client token is either expired or unauthorized, it returns error with '401 Unauthorized' status code.
 */
@Slf4j
public class DemoTokenFilter extends GenericFilterBean {

  final private AccountRepository accountRepository;
  final private Algorithm algorithm;

  /**
   *  DemoTokenFilter constructor that takes secret key, then set up HMAC512 algorithm
   */
  public DemoTokenFilter(AccountRepository accountRepository, String secretKey) {
    Objects.requireNonNull(secretKey, "secret key must be not null");
    this.accountRepository = accountRepository;
    this.algorithm = Algorithm.HMAC512(secretKey);
  }


  /**
   * doFilter is invoked when client sends a request with bearer token (in header) for authentication.
   *    1. Takes token from request
   *    2. Does authentication
   *    3. Invoke a next filter on chain
   * @param request
   * @param response
   * @param filterChain
   * @return
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

    // 1. Takes token from request
    String token = resolveToken(request);
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Does authentication
    //    Send error if authentication failure
    try {
      authentication(verifyToken(token));

    } catch (TokenExpiredException e) {
      log.error("Token Expired : ", e);
      SecurityContextHolder.clearContext();
      ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(), "Token Expired. Please try with a new token.");
      
    } catch (JWTVerificationException e) {
      log.error("Token Error on verifyToken : ", e);
      SecurityContextHolder.clearContext();
      ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    // 3. Invoke next filter
    filterChain.doFilter(request, response);
  }


  /**
   * Take token from request object. Please note that there is prefix "Bearer " that needs to be removed
   * @param request
   * @return token string
   */
  private String resolveToken(ServletRequest request) {
    String token = ((HttpServletRequest) request).getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      return null;
    }
    // remove "Bearer "
    return token.substring(7);
  }


  /**
   * Return DecodedJWT from given token. DecodedJWT has user info for authentication
   * @param token
   * @return decoded jwt
   */
  private DecodedJWT verifyToken(String token) {
    JWTVerifier verifier = JWT.require(algorithm).build();
    return verifier.verify(token);
  }


  /**
   * Check user existance from givem decoded jwt. If not exists or expired, throw exception and caught in doFilter function
   * @param jwt
   */
  private void authentication(DecodedJWT jwt) {
    Long accountId = Long.valueOf(jwt.getSubject());
    accountRepository.findById(accountId).ifPresent(user -> {
      DemoLoginUser demoLoginUser = new DemoLoginUser(user);
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(demoLoginUser, null, demoLoginUser.getAuthorities()));
    });
  }

}