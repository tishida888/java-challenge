package jp.co.axa.apidemo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * When client gets Access Denied while accessing resource,
 * 'handle()' is invoked and it returns access denied error to client (and logging)
 */
@Slf4j
public class DemoAccessDeniedHandler implements AccessDeniedHandler {

  /**
   *  Handle access denied request/response.
   *  Logging the exception and send '403 Forbidden' error to client
   */
  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException exception) throws IOException {
    if (response.isCommitted()) {
      log.info("Response has already been committed.");
      return;
    }
    dump(exception);
    response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
  }

  /**
   * Exception logging when getting Access Denied exception
   * @param e
   */
  private void dump(AccessDeniedException e) {
    if (e instanceof AuthorizationServiceException) {
      log.debug("AuthorizationServiceException : {}", e.getMessage());
    } else if (e instanceof CsrfException) {
      log.debug("org.springframework.security.web.csrf.CsrfException : {}", e.getMessage());
    } else if (e instanceof org.springframework.security.web.server.csrf.CsrfException) {
      log.debug("org.springframework.security.web.server.csrf.CsrfException : {}", e.getMessage());
    } else {
      log.debug("AccessDeniedException : {}", e.getMessage());
    }
  }

}
