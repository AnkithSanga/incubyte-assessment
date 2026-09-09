package com.incubyte.salary.service;

import com.incubyte.salary.domain.Employee;
import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.web.dto.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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
}
