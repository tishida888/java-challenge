package jp.co.axa.apidemo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * When client is accessing API with authentication successfully,
 * 'onAuthenticationSuccess()' is invoked and it returns valid token as JSON message
 */
@Slf4j
public class DemoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  final private Algorithm algorithm;
  final private Long expirationTime;

  public DemoAuthenticationSuccessHandler(String secretKey, Long expirationTime) {
    Objects.requireNonNull(secretKey, "secret key must be not null");
    this.algorithm = Algorithm.HMAC512(secretKey);
    this.expirationTime = TimeUnit.MINUTES.toMillis(expirationTime);
  }

  /**
   * Invoked when a client is authenticased successfully. It sets token in response body
   * and set status to OK status code.
   * @param request
   * @param response
   * @param auth
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication auth) {
    if (response.isCommitted()) {
      log.info("Response has already been committed.");
      return;
    }
    setToken(response, generateToken(auth));
    response.setStatus(HttpStatus.OK.value());
    clearAuthenticationAttributes(request);
  }

  /**
   * Generate token using JWT
   * @param auth
   * @return
   */
  private String generateToken(Authentication auth) {
    DemoLoginUser loginUser = (DemoLoginUser) auth.getPrincipal();
    Date issuedAt = new Date();
    Date notBefore = new Date(issuedAt.getTime());
    Date expiresAt = new Date(issuedAt.getTime() + expirationTime);
    String token = JWT.create()
        .withIssuedAt(issuedAt)
        .withNotBefore(notBefore)
        .withExpiresAt(expiresAt)
        .withSubject(loginUser.getAccount().getId().toString())
        .sign(this.algorithm);
    log.debug("Generated token for User={}", loginUser.getAccount().getName());
    return token;
  }


  /**
   * Set token in response body (not header) as JSON
   * @param response
   * @param token
   */
  private void setToken(HttpServletResponse response, String token) {
    String bearerToken = String.format("Bearer %s", token);

    DemoToken demoToken = new DemoToken(bearerToken);

    try {
      // write toekn data as JSON in response content
      PrintWriter out = response.getWriter();
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      
      ObjectMapper objectMapper = new ObjectMapper();
      String tokenAsJson = objectMapper.writeValueAsString(demoToken);
      out.print(tokenAsJson);
      out.flush();
    } catch (IOException e) {
      log.error("Failed to set Toekn to response on Authentication");
    }
  }

  /**
   * Removes temporary authentication-related data which may have been stored in the
   * session during the authentication process.
   * @param request
   */
  private void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return;
    }
    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
  }

}
