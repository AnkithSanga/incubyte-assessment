package com.incubyte.salary.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateEmployeeRequest(
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName,
    @Email(message = "Valid email is required") @NotBlank String email,
    @NotBlank(message = "Department is required") String department,
    @NotBlank(message = "Job title is required") String jobTitle,
    @NotBlank(message = "Country is required") String country,
    @NotBlank(message = "City is required") String city,
    @NotBlank(message = "Currency code is required") String currencyCode,
    @NotNull(message = "Annual base salary is required") @Positive(message = "Salary must be positive") BigDecimal annualBaseSalaryLocal
) {}
