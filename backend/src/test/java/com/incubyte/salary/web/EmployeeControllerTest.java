package com.incubyte.salary.web;

import com.incubyte.salary.service.EmployeeService;
import com.incubyte.salary.web.dto.EmployeeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("GET /api/v1/employees/{id} returns 404 when employee not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(employeeService.getEmployeeById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employees/999"))
            .andExpect(status().isNotFound());
    }
}
