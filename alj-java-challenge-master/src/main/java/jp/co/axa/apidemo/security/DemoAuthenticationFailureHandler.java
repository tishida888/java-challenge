package jp.co.axa.apidemo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * When client request fails due to authentication failure,
 * 'onAuthenticationFailure()' is invoked and it returns forbidden exception '403 Forbidden' to client (and logging)
 */
@Slf4j
public class DemoAuthenticationFailureHandler implements AuthenticationFailureHandler {

    public DemoAuthenticationFailureHandler() {
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.info("Response has already been committed.");
            return;
        }

        log.debug("Client authentication Failure : User=" + request.getRemoteUser());
        response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
    }

}
