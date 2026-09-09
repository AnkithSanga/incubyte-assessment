package com.incubyte.salary.repository.projection;

import java.math.BigDecimal;

public interface CountrySalaryStats {
    String getCountry();
    String getCurrencyCode();
    Long getHeadcount();
    BigDecimal getTotalPayrollUsd();
    BigDecimal getAverageSalaryUsd();
    BigDecimal getMinSalaryUsd();
    BigDecimal getMaxSalaryUsd();
}
