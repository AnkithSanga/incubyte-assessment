package com.incubyte.salary.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateEmployeeRequest(
    @NotBlank(message = "Department is required") String department,
    @NotBlank(message = "Job title is required") String jobTitle,
    @NotNull(message = "Annual base salary is required") @Positive(message = "Salary must be positive") BigDecimal annualBaseSalaryLocal
) {}
