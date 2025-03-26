package com.example.controller;

import com.example.model.Account;
import com.example.model.AccountDto;
import com.example.model.AccountRequest;
import com.example.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountRequest request) {
        Account createdAccount = accountService.createAccount(request);
        return new ResponseEntity<>(AccountDto.fromEntity(createdAccount), HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable String accountId) {
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(AccountDto.fromEntity(account));
    }

    @PostMapping("/exchange/{accountId}")
    public ResponseEntity<AccountDto> exchangeCurrency(@PathVariable String accountId,
                                                       @RequestParam String currencyFrom,
                                                       @RequestParam String currencyTo,
                                                       @RequestParam BigDecimal amount) {
        Account updatedAccount = accountService.exchangeCurrency(accountId, currencyFrom, currencyTo, amount);
        return ResponseEntity.ok(AccountDto.fromEntity(updatedAccount));
    }
}
