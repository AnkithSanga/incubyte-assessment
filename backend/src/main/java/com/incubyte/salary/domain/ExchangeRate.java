package com.incubyte.salary.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "exchange_rates", indexes = {
    @Index(name = "idx_currency_code", columnList = "currency_code", unique = true)
})
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_code", nullable = false, length = 3, unique = true)
    private String currencyCode;

    @Column(name = "currency_symbol", nullable = false, length = 5)
    private String currencySymbol;

    @Column(name = "country_name", nullable = false, length = 50)
    private String countryName;

    @Column(name = "rate_to_usd", nullable = false, precision = 12, scale = 6)
    private BigDecimal rateToUsd;

    public ExchangeRate() {}

    public ExchangeRate(String currencyCode, String currencySymbol, String countryName, BigDecimal rateToUsd) {
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.countryName = countryName;
        this.rateToUsd = rateToUsd;
    }

    public Long getId() {
        return id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public BigDecimal getRateToUsd() {
        return rateToUsd;
    }

    public void setRateToUsd(BigDecimal rateToUsd) {
        this.rateToUsd = rateToUsd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(currencyCode, that.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode);
    }
}
