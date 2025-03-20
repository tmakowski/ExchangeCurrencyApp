package com.example.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountRequest {
    @NotBlank(message = "FIRST_NAME_REQUIRED")
    @Pattern(regexp = "[A-Z][a-z]+", message = "FIRSTNAME_WRONG_FORMAT")
    private String firstName;

    @NotBlank(message = "LAST_NAME_REQUIRED")
    @Pattern(regexp = "[A-Z][a-z]+", message = "LASTNAME_WRONG_FORMAT")
    private String lastName;

    @NotNull(message = "INITIAL_PLN_BALANCE_REQUIRED")
    @Positive(message = "BALANCE_MUST_BE_POSITIVE")
    private BigDecimal initialPlnBalance;
}
