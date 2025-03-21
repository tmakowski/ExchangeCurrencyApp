package com.example.service;

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
    public Account exchangeCurrency(String accountId) {
        Account account = getAccount(accountId);

        if (account.getUsdBalance().compareTo(BigDecimal.ZERO) == 0) {
            return exchangePlnToUsd(account);
        } else {
            return exchangeUsdToPln(account);
        }
    }

    private Account exchangePlnToUsd(Account account) {
        log.info("Exchanging from PLN to USD");
        BigDecimal usdAmount = nbpExchangeService.exchangePlnToUsd(account.getPlnBalance());

        account.setPlnBalance(BigDecimal.ZERO);
        account.setUsdBalance(account.getUsdBalance().add(usdAmount));
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    private Account exchangeUsdToPln(Account account) {
        log.info("Exchanging from USD to PLN");
        BigDecimal plnAmount = nbpExchangeService.exchangeUsdToPln(account.getUsdBalance());

        account.setUsdBalance(BigDecimal.ZERO);
        account.setPlnBalance(account.getPlnBalance().add(plnAmount));
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }
}
