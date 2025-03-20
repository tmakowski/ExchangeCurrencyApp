package com.example.service;

import com.example.ciient.NbpApiClient;
import com.example.ciient.model.NbpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class NbpExchangeService {

    private final NbpApiClient nbpApiClient;

    public BigDecimal getUsdToPlnRate() {
        NbpResponse response = nbpApiClient.getExchangeRate("USD");
        return response.getRates().get(0).getMid();
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
