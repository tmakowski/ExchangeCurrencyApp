package com.example.service;

import com.example.ciient.NbpApiClient;
import com.example.ciient.model.NbpResponse;
import com.example.exception.ExchangeRateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class NbpExchangeService {

    private final NbpApiClient nbpApiClient;

    public BigDecimal getExchangeRate(String currencyFrom, String currencyTo) {
        if (currencyFrom.equals(currencyTo)) {
            return BigDecimal.ONE;
        }

        if (currencyFrom.equals("PLN")) {
            return BigDecimal.ONE.divide(getRateForCurrency(currencyTo), 4, RoundingMode.HALF_UP);
        }

        if (currencyTo.equals("PLN")) {
            return getRateForCurrency(currencyFrom);
        }

        return getRateForCurrency(currencyTo).divide(getRateForCurrency(currencyFrom), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal getRateForCurrency(String currency) {
        try {
            log.info("Fetching rate for {} from NBP API", currency);
            NbpResponse response = nbpApiClient.getExchangeRate(currency);
            return response.getRates().get(0).getMid();
        } catch (ResourceAccessException e) {
            log.error("Error connecting to NBP API", e);
            throw new ExchangeRateException("Unable to connect to NBP API: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while getting exchange rate", e);
            throw new ExchangeRateException("Error while fetching exchange rate: " + e.getMessage());
        }
    }

    public BigDecimal exchange(String currencyFrom, String currencyTo, BigDecimal amount) {
        return amount.multiply(getExchangeRate(currencyFrom, currencyTo)).setScale(2, RoundingMode.HALF_UP);
    }
}
