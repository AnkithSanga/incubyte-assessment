package com.incubyte.salary.web.dto;

import java.math.BigDecimal;

public record CountryAnalyticsResponse(
    String country,
    String currencyCode,
    long headcount,
    BigDecimal totalPayrollUsd,
    BigDecimal averageSalaryUsd,
    BigDecimal minSalaryUsd,
    BigDecimal maxSalaryUsd,
    double payrollPercentage
) {}
