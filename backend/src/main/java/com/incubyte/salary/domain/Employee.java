package com.incubyte.salary.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_emp_id", columnList = "employee_id", unique = true),
    @Index(name = "idx_emp_department", columnList = "department"),
    @Index(name = "idx_emp_country", columnList = "country"),
    @Index(name = "idx_emp_salary_usd", columnList = "annual_base_salary_usd")
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, unique = true, length = 15)
    private String employeeId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "annual_base_salary_local", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualBaseSalaryLocal;

    @Column(name = "annual_base_salary_usd", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualBaseSalaryUsd;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Employee() {}

    public Employee(String employeeId, String firstName, String lastName, String email,
                    String department, String jobTitle, String country, String city,
                    String currencyCode, BigDecimal annualBaseSalaryLocal, BigDecimal annualBaseSalaryUsd) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.jobTitle = jobTitle;
        this.country = country;
        this.city = city;
        this.currencyCode = currencyCode;
        this.annualBaseSalaryLocal = annualBaseSalaryLocal;
        this.annualBaseSalaryUsd = annualBaseSalaryUsd;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAnnualBaseSalaryLocal() {
        return annualBaseSalaryLocal;
    }

    public void setAnnualBaseSalaryLocal(BigDecimal annualBaseSalaryLocal) {
        this.annualBaseSalaryLocal = annualBaseSalaryLocal;
    }

    public BigDecimal getAnnualBaseSalaryUsd() {
        return annualBaseSalaryUsd;
    }

    public void setAnnualBaseSalaryUsd(BigDecimal annualBaseSalaryUsd) {
        this.annualBaseSalaryUsd = annualBaseSalaryUsd;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }
}
