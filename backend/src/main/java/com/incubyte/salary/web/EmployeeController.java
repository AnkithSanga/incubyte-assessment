package com.incubyte.salary.web;

import com.incubyte.salary.service.EmployeeService;
import com.incubyte.salary.web.dto.EmployeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employees", description = "Employee directory management with pagination, search, and filtering")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @Operation(summary = "List employees with filters and pagination",
               description = "Search across 10,000 employees by name/email/id, filter by country or department, and sort")
    public Page<EmployeeResponse> getEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return employeeService.getEmployees(department, country, search, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Fetch single employee compensation and details")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/meta/departments")
    @Operation(summary = "List all distinct departments", description = "Used for UI filter dropdowns")
    public List<String> getDepartments() {
        return employeeService.getDistinctDepartments();
    }

    @GetMapping("/meta/countries")
    @Operation(summary = "List all distinct countries", description = "Used for UI filter dropdowns")
    public List<String> getCountries() {
        return employeeService.getDistinctCountries();
    }
}
