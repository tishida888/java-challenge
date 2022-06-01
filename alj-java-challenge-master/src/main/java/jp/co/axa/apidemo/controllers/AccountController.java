package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Account;
import jp.co.axa.apidemo.services.impl.AccountServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@Api(tags = {"Account"})
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AccountController {

    @Autowired
    private AccountServiceImpl accountService;

    /**
     * Get all account list on Axa Java Challange API (Admin Role only)
     * @param request
     * @return List<Account>
     */
    @ApiOperation("Get list of accounts (Admin role only)")
    @GetMapping("/account")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public List<Account> getAccounts(HttpServletRequest request) {
        try {
            log.info("getAccounts() is invoked by User : " + request.getRemoteUser());
            return accountService.retrieveAccounts();
        } catch(Exception e) {
            log.debug("Exception on getAccounts. Exception : " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get all accounts. Please contact support.");
        }
    }


    /**
     * Get account by given account name
     * @param name
     * @return Account
     */
    @ApiOperation("Get account by ID (Admin role only)")
    @GetMapping("/account/{accountId}")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public Account getAccountById(@PathVariable(name="accountId")Long accountId) {
        log.info("getAccountbyName() is invoked");

        try {
            return accountService.findById(accountId);
        } catch(NoSuchElementException e) {
            log.debug("Account Not Found. ID = " + accountId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Not Found. ID = " + accountId);
        } catch(Exception e) {
            log.debug("Exception on getAccountById. Exception : " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Failed to get account by ID = {}. Please contact support.", accountId));
        }
    }


    /**
     * Save Account info (Admin Role only)
     * @param account
     * @return
     * @throws Exception
     */
    @ApiOperation("Register new account (Admin role only)")
    @PostMapping("/account")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public Account saveAccount(Account account) throws Exception {
        log.info("saveAccounts() is invoked");
        Account resAccount = null;
        try {
            log.debug("Saving User Info : " + account.toString());
            resAccount = accountService.saveAccount(account);
            resAccount.setPassword("");
            log.info("Account Saved Successfully");
        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal Argument error. Please validate your input and try again.");
        } catch(Exception e) {
            log.error("Failed to save Account. Error : ", e);
            log.error("Account Data : ", account.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save Account. Please contact support.");
        }            
        return resAccount;
    }
 
    /**
     * Delete Account by ID (Admin Role only)
     * @param accountId
     */
    @ApiOperation("Delete account (Admin role only)")
    @DeleteMapping("/account/{accountId}")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Server Error") })  
    public void deleteAccount(@PathVariable(name="accountId")Long accountId){
        try {
            accountService.deleteAccount(accountId);
            log.info("Account Deleted Successfully for AccountID="+accountId.toString());
        } catch(NoSuchElementException e) {
            log.error("Account Not Found. ID = " + accountId.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Not Found. ID = " + accountId);
        } catch(EmptyResultDataAccessException e) {
            log.error("Account Not Found. ID = " + accountId.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Not Found. ID = " + accountId);
        } catch(Exception e) {
            log.error("Exception on deleteAccount. Exception : " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete account. Please contact support.");
        }
    }
    
}
