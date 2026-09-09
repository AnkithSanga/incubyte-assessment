package com.incubyte.salary.web.dto;

import java.math.BigDecimal;

public record DepartmentAnalyticsResponse(
    String department,
    long headcount,
    BigDecimal totalPayrollUsd,
    BigDecimal averageSalaryUsd,
    BigDecimal minSalaryUsd,
    BigDecimal maxSalaryUsd,
    double payrollPercentage
) {}
