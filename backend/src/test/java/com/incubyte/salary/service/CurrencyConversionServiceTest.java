package com.incubyte.salary.service;

import com.incubyte.salary.domain.ExchangeRate;
import com.incubyte.salary.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private CurrencyConversionService conversionService;

    @BeforeEach
    void setUp() {
        when(exchangeRateRepository.findAll()).thenReturn(List.of(
            new ExchangeRate("USD", "$", "United States", new BigDecimal("1.000000")),
            new ExchangeRate("EUR", "€", "Germany", new BigDecimal("1.090000")),
            new ExchangeRate("INR", "₹", "India", new BigDecimal("0.012000")),
            new ExchangeRate("GBP", "£", "United Kingdom", new BigDecimal("1.300000"))
        ));
        conversionService = new CurrencyConversionService(exchangeRateRepository);
        conversionService.refreshCache();
    }

    @Test
    @DisplayName("Should convert USD to USD without modification")
    void shouldConvertUsdToUsd() {
        BigDecimal local = new BigDecimal("100000.00");
        BigDecimal usd = conversionService.convertToUsd(local, "USD");
        assertThat(usd).isEqualByComparingTo(new BigDecimal("100000.00"));
    }

    @Test
    @DisplayName("Should convert EUR to USD using 1.09 exchange rate")
    void shouldConvertEurToUsd() {
        BigDecimal local = new BigDecimal("100000.00");
        BigDecimal usd = conversionService.convertToUsd(local, "EUR");
        assertThat(usd).isEqualByComparingTo(new BigDecimal("109000.00"));
    }

    @Test
    @DisplayName("Should convert INR to USD using 0.012 exchange rate")
    void shouldConvertInrToUsd() {
        BigDecimal local = new BigDecimal("2500000.00");
        BigDecimal usd = conversionService.convertToUsd(local, "INR");
        assertThat(usd).isEqualByComparingTo(new BigDecimal("30000.00"));
    }

    @Test
    @DisplayName("Should handle null local amount gracefully returning ZERO")
    void shouldHandleNullAmount() {
        BigDecimal usd = conversionService.convertToUsd(null, "EUR");
        assertThat(usd).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
