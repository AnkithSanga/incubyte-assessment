package com.incubyte.salary.repository.projection;

import java.math.BigDecimal;

public interface OverallSalaryStats {
    Long getTotalEmployees();
    BigDecimal getTotalPayrollUsd();
    BigDecimal getAverageSalaryUsd();
    BigDecimal getMinSalaryUsd();
    BigDecimal getMaxSalaryUsd();
}
