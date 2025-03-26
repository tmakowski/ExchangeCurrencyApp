package com.example.service;

import com.example.exception.AccountBalanceException;
import com.example.factory.AccountFactory;
import com.example.model.Account;
import com.example.model.AccountRequest;
import com.example.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final NbpExchangeService nbpExchangeService;

    public Account createAccount(AccountRequest request) {
        Account account = AccountFactory.createNewAccount(
                request.getFirstName(),
                request.getLastName(),
                request.getInitialPlnBalance()
        );
        return accountRepository.save(account);
    }

    public Account getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + accountId));
    }

    @Transactional
    public Account exchangeCurrency(String accountId, String currencyFrom, String currencyTo, BigDecimal amount) {
        Account account = getAccount(accountId);

        validateCurrencyExchange(account, currencyFrom, currencyTo, amount);

        return updateAccountBalances(account, currencyFrom, currencyTo, amount);
    }

    private void validateCurrencyExchange(Account account, String currencyFrom, String currencyTo, BigDecimal amount) {
        BigDecimal currentBalance = getCurrentBalanceForUSD(account, currencyFrom);

        if (currentBalance.compareTo(amount) < 0) {
            throw new AccountBalanceException("Not enough funds on Your account!");
        }

        if (currencyFrom.equals(currencyTo)) {
            throw new AccountBalanceException("Cannot exchange to the same currency!");
        }
    }

    private Account updateAccountBalances(Account account, String currencyFrom, String currencyTo, BigDecimal amount) {
        BigDecimal exchangedAmount = nbpExchangeService.exchange(currencyFrom, currencyTo, amount);

        if (currencyFrom.equals("USD")) {
            account.setUsdBalance(account.getUsdBalance().subtract(amount));
            account.setPlnBalance(account.getPlnBalance().add(exchangedAmount));
        } else {
            account.setPlnBalance(account.getPlnBalance().subtract(amount));
            account.setUsdBalance(account.getUsdBalance().add(exchangedAmount));
        }

        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    private BigDecimal getCurrentBalanceForUSD(Account account, String currencyFrom) {
        return currencyFrom.equals("USD") ? account.getUsdBalance() : account.getPlnBalance();
    }
}
