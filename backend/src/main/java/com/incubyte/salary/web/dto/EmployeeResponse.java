package com.incubyte.salary.web.dto;

import com.incubyte.salary.domain.Employee;
import java.math.BigDecimal;

public record EmployeeResponse(
    Long id,
    String employeeId,
    String firstName,
    String lastName,
    String fullName,
    String email,
    String department,
    String jobTitle,
    String country,
    String city,
    String currencyCode,
    BigDecimal annualBaseSalaryLocal,
    BigDecimal annualBaseSalaryUsd
) {
    public static EmployeeResponse fromEntity(Employee e) {
        return new EmployeeResponse(
            e.getId(),
            e.getEmployeeId(),
            e.getFirstName(),
            e.getLastName(),
            e.getFirstName() + " " + e.getLastName(),
            e.getEmail(),
            e.getDepartment(),
            e.getJobTitle(),
            e.getCountry(),
            e.getCity(),
            e.getCurrencyCode(),
            e.getAnnualBaseSalaryLocal(),
            e.getAnnualBaseSalaryUsd()
        );
    }
}
