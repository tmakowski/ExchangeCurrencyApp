package com.example.factory;

import com.example.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountFactory {

    public static Account createNewAccount(String firstName, String lastName, BigDecimal initialPlnBalance) {
        LocalDateTime now = LocalDateTime.now();

        return Account.builder()
                .id(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .plnBalance(initialPlnBalance)
                .usdBalance(BigDecimal.ZERO)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
