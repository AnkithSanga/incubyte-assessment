package com.incubyte.salary.service;

import com.incubyte.salary.domain.ExchangeRate;
import com.incubyte.salary.repository.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CurrencyConversionService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final Map<String, BigDecimal> rateCache = new ConcurrentHashMap<>();

    public CurrencyConversionService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @PostConstruct
    public void refreshCache() {
        for (ExchangeRate rate : exchangeRateRepository.findAll()) {
            rateCache.put(rate.getCurrencyCode().toUpperCase(), rate.getRateToUsd());
        }
    }

    public BigDecimal convertToUsd(BigDecimal localAmount, String currencyCode) {
        if (localAmount == null) {
            return BigDecimal.ZERO;
        }
        if (currencyCode == null || currencyCode.equalsIgnoreCase("USD")) {
            return localAmount.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal rate = rateCache.computeIfAbsent(currencyCode.toUpperCase(), code ->
            exchangeRateRepository.findByCurrencyCode(code)
                    .map(ExchangeRate::getRateToUsd)
                    .orElse(BigDecimal.ONE)
        );

        return localAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal convertFromUsd(BigDecimal usdAmount, String targetCurrencyCode) {
        if (usdAmount == null) {
            return BigDecimal.ZERO;
        }
        if (targetCurrencyCode == null || targetCurrencyCode.equalsIgnoreCase("USD")) {
            return usdAmount.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal rate = rateCache.computeIfAbsent(targetCurrencyCode.toUpperCase(), code ->
            exchangeRateRepository.findByCurrencyCode(code)
                    .map(ExchangeRate::getRateToUsd)
                    .orElse(BigDecimal.ONE)
        );

        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return usdAmount.divide(rate, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getRate(String currencyCode) {
        return rateCache.getOrDefault(currencyCode.toUpperCase(), BigDecimal.ONE);
    }
}
