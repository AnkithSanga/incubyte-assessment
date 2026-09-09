package com.incubyte.salary.web.dto;

import java.math.BigDecimal;

public record OverallKpiResponse(
    long totalEmployees,
    BigDecimal totalPayrollUsd,
    BigDecimal averageSalaryUsd,
    BigDecimal medianSalaryUsd,
    BigDecimal minSalaryUsd,
    BigDecimal maxSalaryUsd,
    String highestPayingDepartment,
    String highestPayingCountry
) {}
