package com.example.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountDto(
        String id,
        String fullName,
        BigDecimal plnBalance,
        BigDecimal usdBalance
) {
    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .fullName(account.getFirstName() + " " + account.getLastName())
                .plnBalance(account.getPlnBalance())
                .usdBalance(account.getUsdBalance())
                .build();
    }
}
