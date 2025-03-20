package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Min(0)
    @Column(name = "pln_balance")
    private BigDecimal plnBalance;

    @NotNull
    @Min(0)
    @Column(name = "usd_balance")
    private BigDecimal usdBalance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


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
