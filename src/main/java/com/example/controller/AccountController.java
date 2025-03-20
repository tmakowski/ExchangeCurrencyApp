package com.example.controller;

import com.example.model.Account;
import com.example.model.AccountRequest;
import com.example.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest request) {
        Account createdAccount = accountService.createAccount(request);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable String accountId) {
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/exchange/{accountId}")
    public ResponseEntity<Account> exchangeCurrency(@PathVariable String accountId) {
        Account updatedAccount = accountService.exchangeCurrency(accountId);
        return ResponseEntity.ok(updatedAccount);
    }
}
