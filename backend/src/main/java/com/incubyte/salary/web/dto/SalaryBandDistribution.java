package com.incubyte.salary.web.dto;

import java.math.BigDecimal;

public record SalaryBandDistribution(
    String label,
    BigDecimal minUsd,
    BigDecimal maxUsd,
    long count,
    double percentage
) {}
