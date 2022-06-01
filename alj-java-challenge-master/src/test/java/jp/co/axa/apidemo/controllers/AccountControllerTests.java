package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Account;
import jp.co.axa.apidemo.repositories.AccountRepository;
import jp.co.axa.apidemo.services.impl.AccountServiceImpl;
import jp.co.axa.apidemo.security.DemoSecurityConfig;
import jp.co.axa.apidemo.security.DemoLoginUser;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(value = AccountController.class)
@Import(value = {DemoSecurityConfig.class})
public class AccountControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountServiceImpl accountService;
    @MockBean
    private AccountRepository accountRepository;


    /**
     * Standard behavior test on [getAccounts] function on controll.
     * It expects to return JSON string for Account object from service (mock here).
     * @throws Exception
     */
    @Test
    public void getAccountsTest() throws Exception {
        Account account = Account.of("admin1","admin1",true);
        List<Account> resAccounts = Arrays.asList(account);
        when(accountService.retrieveAccounts()).thenReturn(resAccounts);

        DemoLoginUser loginUser = new DemoLoginUser(account);
        RequestBuilder builder = MockMvcRequestBuilders.get("/api/v1/account")
                        .with(user(loginUser))
                        .accept(MediaType.APPLICATION_JSON);

        String expectedContent = "[{\"id\":null,\"name\":\"admin1\",\"password\":\"admin1\",\"admin\":true}]";
        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent))
                .andDo(print())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedContent);
    }



    /**
     * Standard behavior test on [saveAccount] function on controll.
     * It also expects to return JSON string for Account object from service (mock here).
     * @throws Exception
     */
    @Test
    public void saveAccountTest() throws Exception {
        Account account = Account.of("test","test_password",true);
        when(accountService.saveAccount(account)).thenReturn(account);

        DemoLoginUser loginUser = new DemoLoginUser(account);
        RequestBuilder builder = MockMvcRequestBuilders.post("/api/v1/account")
                        .param("id", "")
                        .param("name", "test")
                        .param("password", "test_password")
                        .param("admin", "true")
                        .with(user(loginUser))
                        .accept(MediaType.APPLICATION_JSON);

        String expectedContent = "{\"id\":null,\"name\":\"test\",\"password\":\"\",\"admin\":true}";
        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent))
                .andDo(print())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedContent);
    }



    /**
     * Exception handling test on saveAccount function. This is to simulate when service throws exception
     * when client sends bad request or some error on service layer. 
     * @throws Exception
     */
    @Test
    public void throwExceptionOnSaveTest() throws Exception {
        Account account = Account.of("admin1","admin1",true);
        doThrow(IllegalArgumentException.class).when(accountService).saveAccount(account);

        DemoLoginUser loginUser = new DemoLoginUser(account);
        RequestBuilder builder = MockMvcRequestBuilders.post("/api/v1/account")
                        .param("name", "test1")
                        .param("password", "test1")
                        .param("admin", "false")
                        .with(user(loginUser))
                        .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().is5xxServerError())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getErrorMessage().equals("Failed to save Account. Please contact support."));
    }


    /**
     * Exception handling test on deleteAccount function. This is also to simulate when service throws exception
     * when client sends bad request (like ID is missing) or some error on service layer. 
     * @throws Exception
     */
    @Test
    public void throwExceptionOnDeleteTest() throws Exception {
        Long deleteAccountId = 5L;
        Account account = Account.of("admin1","admin1",true);
        doThrow(EmptyResultDataAccessException.class).when(accountService).deleteAccount(deleteAccountId);

        DemoLoginUser loginUser = new DemoLoginUser(account);
        RequestBuilder builder = MockMvcRequestBuilders.delete("/api/v1/account/{accountId}", deleteAccountId.toString())
                        .with(user(loginUser))
                        .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getErrorMessage().equals("Account Not Found. ID = "+deleteAccountId.toString()));
    }

}
