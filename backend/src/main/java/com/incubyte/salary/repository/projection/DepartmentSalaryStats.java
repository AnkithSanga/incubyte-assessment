package com.incubyte.salary.repository.projection;

import java.math.BigDecimal;

public interface DepartmentSalaryStats {
    String getDepartment();
    Long getHeadcount();
    BigDecimal getTotalPayrollUsd();
    BigDecimal getAverageSalaryUsd();
    BigDecimal getMinSalaryUsd();
    BigDecimal getMaxSalaryUsd();
}
