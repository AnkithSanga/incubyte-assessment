package com.incubyte.salary.web.dto;

import com.incubyte.salary.domain.ExchangeRate;
import java.math.BigDecimal;

public record ExchangeRateResponse(
    String currencyCode,
    String currencySymbol,
    String countryName,
    BigDecimal rateToUsd
) {
    public static ExchangeRateResponse fromEntity(ExchangeRate rate) {
        return new ExchangeRateResponse(
            rate.getCurrencyCode(),
            rate.getCurrencySymbol(),
            rate.getCountryName(),
            rate.getRateToUsd()
        );
    }
}
