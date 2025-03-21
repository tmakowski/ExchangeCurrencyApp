package com.example.service;

import com.example.ciient.NbpApiClient;
import com.example.ciient.model.NbpResponse;
import com.example.exception.ExchangeRateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class NbpExchangeService {

    private final NbpApiClient nbpApiClient;

    @Cacheable(value = "exchangeRates", key = "'USD'", cacheManager = "ratesCacheManager")
    public BigDecimal getUsdToPlnRate() {
        try {
            log.info("Fetching USD to PLN exchange rate from NBP API");
            NbpResponse response = nbpApiClient.getExchangeRate("USD");

            return response.getRates().get(0).getMid();
        } catch (ResourceAccessException e) {
            log.error("Error connecting to NBP API", e);
            throw new ExchangeRateException("Unable to connect to NBP API: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while getting exchange rate", e);
            throw new ExchangeRateException("Error while fetching exchange rate: " + e.getMessage());
        }
    }

    public BigDecimal exchangeUsdToPln(BigDecimal usdAmount) {
        BigDecimal rate = getUsdToPlnRate();
        return usdAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal exchangePlnToUsd(BigDecimal plnAmount) {
        BigDecimal rate = getUsdToPlnRate();
        return plnAmount.divide(rate, 2, RoundingMode.HALF_UP);
    }
}
