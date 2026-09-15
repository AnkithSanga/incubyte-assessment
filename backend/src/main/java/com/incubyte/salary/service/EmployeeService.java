package com.incubyte.salary.service;

import com.incubyte.salary.domain.Employee;
import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.web.dto.CreateEmployeeRequest;
import com.incubyte.salary.web.dto.EmployeeResponse;
import com.incubyte.salary.web.dto.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CurrencyConversionService conversionService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           CurrencyConversionService conversionService) {
        this.employeeRepository = employeeRepository;
        this.conversionService = conversionService;
    }

    public Page<EmployeeResponse> getEmployees(String department, String country, String search, Pageable pageable) {
        String cleanDept = (department != null && !department.isBlank()) ? department.trim() : null;
        String cleanCountry = (country != null && !country.isBlank()) ? country.trim() : null;
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        return employeeRepository.findByFilters(cleanDept, cleanCountry, cleanSearch, pageable)
                .map(EmployeeResponse::fromEntity);
    }

    public Optional<EmployeeResponse> getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(EmployeeResponse::fromEntity);
    }

    public List<String> getDistinctDepartments() {
        return employeeRepository.findDistinctDepartments();
    }

    public List<String> getDistinctCountries() {
        return employeeRepository.findDistinctCountries();
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        long nextNum = employeeRepository.count() + 1;
        String empId = String.format("EMP-%05d", nextNum);

        BigDecimal usdSalary = conversionService.convertToUsd(
            request.annualBaseSalaryLocal(),
            request.currencyCode()
        );

        Employee employee = new Employee(
            empId,
            request.firstName().trim(),
            request.lastName().trim(),
            request.email().trim().toLowerCase(),
            request.department().trim(),
            request.jobTitle().trim(),
            request.country().trim(),
            request.city().trim(),
            request.currencyCode().trim().toUpperCase(),
            request.annualBaseSalaryLocal(),
            usdSalary
        );

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(saved);
    }

    @Transactional
    public Optional<EmployeeResponse> updateEmployee(Long id, UpdateEmployeeRequest request) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setDepartment(request.department().trim());
            employee.setJobTitle(request.jobTitle().trim());
            employee.setAnnualBaseSalaryLocal(request.annualBaseSalaryLocal());

            BigDecimal usdSalary = conversionService.convertToUsd(
                request.annualBaseSalaryLocal(),
                employee.getCurrencyCode()
            );
            employee.setAnnualBaseSalaryUsd(usdSalary);

            Employee updated = employeeRepository.save(employee);
            return EmployeeResponse.fromEntity(updated);
        });
    }

    @Transactional
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
