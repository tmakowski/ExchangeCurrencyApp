package com.example.controller;

import com.example.model.Account;
import com.example.model.AccountRequest;
import com.example.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private Account testAccount;
    private AccountRequest testAccountRequest;
    private final String ACCOUNT_ID = "test-account-id";

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(ACCOUNT_ID)
                .firstName("Jan")
                .lastName("Kowalski")
                .plnBalance(BigDecimal.valueOf(1000))
                .usdBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAccountRequest = new AccountRequest();
        testAccountRequest.setFirstName("Jan");
        testAccountRequest.setLastName("Kowalski");
        testAccountRequest.setInitialPlnBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldCreateAccount() throws Exception {
        when(accountService.createAccount(any(AccountRequest.class))).thenReturn(testAccount);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.fullName").value("Jan Kowalski"))
                .andExpect(jsonPath("$.plnBalance").value(1000))
                .andExpect(jsonPath("$.usdBalance").value(0));
    }

    @Test
    void shouldGetAccountById() throws Exception {
        when(accountService.getAccount(ACCOUNT_ID)).thenReturn(testAccount);

        mockMvc.perform(get("/api/v1/accounts/{accountId}", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.fullName").value("Jan Kowalski"))
                .andExpect(jsonPath("$.plnBalance").value(1000))
                .andExpect(jsonPath("$.usdBalance").value(0));
    }

    @Test
    void shouldExchangeCurrency() throws Exception {
        Account updatedAccount = Account.builder()
                .id(ACCOUNT_ID)
                .firstName("Jan")
                .lastName("Kowalski")
                .plnBalance(BigDecimal.ZERO)
                .usdBalance(BigDecimal.valueOf(250))
                .createdAt(testAccount.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(accountService.exchangeCurrency(
                eq(ACCOUNT_ID),
                eq("PLN"),
                eq("USD"),
                eq(BigDecimal.valueOf(1000))
        )).thenReturn(updatedAccount);

        mockMvc.perform(post("/api/v1/accounts/exchange/{accountId}", ACCOUNT_ID)
                        .param("currencyFrom", "PLN")
                        .param("currencyTo", "USD")
                        .param("amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.plnBalance").value(0))
                .andExpect(jsonPath("$.usdBalance").value(250));
    }
}