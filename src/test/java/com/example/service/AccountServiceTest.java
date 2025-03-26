package com.example.service;

import com.example.exception.AccountBalanceException;
import com.example.model.Account;
import com.example.model.AccountRequest;
import com.example.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NbpExchangeService nbpExchangeService;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private AccountRequest testAccountRequest;
    private final String ACCOUNT_ID = "test-account-id";
    private static final int PLN_BALANCE = 1000;
    private static final int USD_BALANCE = 250;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(ACCOUNT_ID)
                .firstName("Jan")
                .lastName("Kowalski")
                .plnBalance(BigDecimal.valueOf(PLN_BALANCE))
                .usdBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAccountRequest = new AccountRequest();
        testAccountRequest.setFirstName("Jan");
        testAccountRequest.setLastName("Kowalski");
        testAccountRequest.setInitialPlnBalance(BigDecimal.valueOf(PLN_BALANCE));
    }

    @Test
    void shouldCreateNewAccount() {
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account result = accountService.createAccount(testAccountRequest);

        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccount.getFirstName(), result.getFirstName());
        assertEquals(testAccount.getLastName(), result.getLastName());
        assertEquals(testAccount.getPlnBalance(), result.getPlnBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void shouldGetAccountById() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

        Account result = accountService.getAccount(ACCOUNT_ID);

        assertNotNull(result);
        assertEquals(ACCOUNT_ID, result.getId());
        verify(accountRepository, times(1)).findById(ACCOUNT_ID);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccount(ACCOUNT_ID));
        verify(accountRepository, times(1)).findById(ACCOUNT_ID);
    }

    @Test
    void shouldExchangeFromPlnToUsd() {
        BigDecimal plnBalance = BigDecimal.valueOf(PLN_BALANCE);
        BigDecimal usdAmount = BigDecimal.valueOf(USD_BALANCE);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        when(nbpExchangeService.exchange("PLN", "USD", plnBalance)).thenReturn(usdAmount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account result = accountService.exchangeCurrency(ACCOUNT_ID, "PLN", "USD", plnBalance);

        assertNotNull(result);
        verify(nbpExchangeService, times(1)).exchange(eq("PLN"), eq("USD"), eq(plnBalance));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void shouldExchangeFromUsdToPln() {
        BigDecimal usdBalance = BigDecimal.valueOf(USD_BALANCE);
        BigDecimal plnAmount = BigDecimal.valueOf(PLN_BALANCE);

        Account accountWithUsd = Account.builder()
                .id(ACCOUNT_ID)
                .firstName("Jan")
                .lastName("Kowalski")
                .plnBalance(BigDecimal.ZERO)
                .usdBalance(usdBalance)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(accountWithUsd));
        when(nbpExchangeService.exchange("USD", "PLN", usdBalance)).thenReturn(plnAmount);
        when(accountRepository.save(any(Account.class))).thenReturn(accountWithUsd);

        Account result = accountService.exchangeCurrency(ACCOUNT_ID, "USD", "PLN", usdBalance);

        assertNotNull(result);
        verify(nbpExchangeService, times(1)).exchange(eq("USD"), eq("PLN"), eq(usdBalance));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughBalance() {
        BigDecimal exchangeAmount = BigDecimal.valueOf(2000);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

        assertThrows(AccountBalanceException.class, () ->
                accountService.exchangeCurrency(ACCOUNT_ID, "PLN", "USD", exchangeAmount)
        );
    }
}