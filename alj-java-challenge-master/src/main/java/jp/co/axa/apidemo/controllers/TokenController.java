package jp.co.axa.apidemo.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  TokenController class is for swagger UI that provides Token API for authentication.
 *  This API is based on JWT token and need a document for Token API on swagger. 
 *  Therefore token(name, password) is prepared to be the interface of actual authentication.
 */

@Api(tags = "Token")
@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class TokenController {

    /**
     * This is a fake token interface that is defined only for Swagger UI.
     * This should not be called, because actual token (login) implementation is done by spring security.
     * @param name
     * @param password
     * @return ""
     */
    @ApiOperation("Generate Token to access API")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Token Generated Successfully",
                     examples = @Example({ @ExampleProperty(mediaType = "*/*",
                                            value = "{\n    \"Token\": \"YourToken (including Bearer)\"\n}")})),
        @ApiResponse(code = 500, message = "Token generation failure on internal")
      })    
    @PostMapping("/token")
    public String token(@RequestParam(name = "name", required = true) String name,
                        @RequestParam(name = "password", required = true) String password) {
        log.info("Login Action by " + name);
        throw new IllegalStateException("Token controller method shouldn't be called. It's implemented by Spring Security filters.");
    }    
}
