package com.incubyte.salary.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incubyte.salary.service.EmployeeService;
import com.incubyte.salary.web.dto.CreateEmployeeRequest;
import com.incubyte.salary.web.dto.EmployeeResponse;
import com.incubyte.salary.web.dto.UpdateEmployeeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("GET /api/v1/employees returns paginated list of employees")
    void shouldReturnPaginatedEmployees() throws Exception {
        EmployeeResponse emp = new EmployeeResponse(
            1L, "EMP-00001", "Jane", "Doe", "Jane Doe", "jane.doe@acme.corp",
            "Engineering", "Software Engineer", "United States", "San Francisco",
            "USD", new BigDecimal("120000.00"), new BigDecimal("120000.00")
        );

        when(employeeService.getEmployees(any(), any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(emp)));

        mockMvc.perform(get("/api/v1/employees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].employeeId").value("EMP-00001"))
            .andExpect(jsonPath("$.content[0].department").value("Engineering"));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} returns single employee")
    void shouldReturnSingleEmployee() throws Exception {
        EmployeeResponse emp = new EmployeeResponse(
            1L, "EMP-00001", "Jane", "Doe", "Jane Doe", "jane.doe@acme.corp",
            "Engineering", "Software Engineer", "United States", "San Francisco",
            "USD", new BigDecimal("120000.00"), new BigDecimal("120000.00")
        );

        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(emp));

        mockMvc.perform(get("/api/v1/employees/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeId").value("EMP-00001"));
    }

    @Test
    @DisplayName("POST /api/v1/employees creates employee and returns 201")
    void shouldCreateEmployee() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
            "Alice", "Wonder", "alice@acme.corp", "Engineering",
            "DevOps Engineer", "United States", "Austin", "USD", new BigDecimal("130000.00")
        );

        EmployeeResponse created = new EmployeeResponse(
            10001L, "EMP-10001", "Alice", "Wonder", "Alice Wonder", "alice@acme.corp",
            "Engineering", "DevOps Engineer", "United States", "Austin",
            "USD", new BigDecimal("130000.00"), new BigDecimal("130000.00")
        );

        when(employeeService.createEmployee(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.employeeId").value("EMP-10001"))
            .andExpect(jsonPath("$.fullName").value("Alice Wonder"));
    }

    @Test
    @DisplayName("PUT /api/v1/employees/{id} updates employee and returns 200")
    void shouldUpdateEmployee() throws Exception {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest(
            "Engineering", "Staff Engineer", new BigDecimal("160000.00")
        );

        EmployeeResponse updated = new EmployeeResponse(
            1L, "EMP-00001", "Jane", "Doe", "Jane Doe", "jane.doe@acme.corp",
            "Engineering", "Staff Engineer", "United States", "San Francisco",
            "USD", new BigDecimal("160000.00"), new BigDecimal("160000.00")
        );

        when(employeeService.updateEmployee(eq(1L), any())).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jobTitle").value("Staff Engineer"));
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} deletes employee and returns 204")
    void shouldDeleteEmployee() throws Exception {
        when(employeeService.deleteEmployee(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/employees/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} returns 404 when employee not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(employeeService.getEmployeeById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employees/999"))
            .andExpect(status().isNotFound());
    }
}
