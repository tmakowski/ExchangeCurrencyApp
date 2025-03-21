package com.example.ciient;

import com.example.ciient.model.NbpResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "nbp-api", url = "http://api.nbp.pl/api")
public interface NbpApiClient {

    @GetMapping("/exchangerates/rates/a/{code}")
    NbpResponse getExchangeRate(@PathVariable("code") String currencyCode);
}
