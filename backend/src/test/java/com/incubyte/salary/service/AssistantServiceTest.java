package com.incubyte.salary.service;

import com.incubyte.salary.web.dto.AssistantQueryResponse;
import com.incubyte.salary.web.dto.CountryAnalyticsResponse;
import com.incubyte.salary.web.dto.DepartmentAnalyticsResponse;
import com.incubyte.salary.web.dto.OverallKpiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private SalaryAnalyticsService analyticsService;

    private AssistantService assistantService;

    @BeforeEach
    void setUp() {
        when(analyticsService.getOverallKpis()).thenReturn(new OverallKpiResponse(
            10000L, new BigDecimal("850000000.00"), new BigDecimal("85000.00"),
            new BigDecimal("82000.00"), new BigDecimal("30000.00"), new BigDecimal("220000.00"),
            "Engineering", "United States"
        ));
        when(analyticsService.getDepartmentAnalytics()).thenReturn(List.of(
            new DepartmentAnalyticsResponse("Engineering", 3500L, new BigDecimal("380000000.00"),
                new BigDecimal("108500.00"), new BigDecimal("65000.00"), new BigDecimal("220000.00"), 44.7)
        ));
        when(analyticsService.getCountryAnalytics()).thenReturn(List.of(
            new CountryAnalyticsResponse("Germany", "EUR", 1500L, new BigDecimal("130000000.00"),
                new BigDecimal("86600.00"), new BigDecimal("55000.00"), new BigDecimal("140000.00"), 15.3)
        ));
        assistantService = new AssistantService(analyticsService);
    }

    @Test
    @DisplayName("Should answer questions regarding specific department")
    void shouldAnswerDepartmentQuestion() {
        AssistantQueryResponse response = assistantService.answerQuestion("What is the average salary in Engineering?");
        assertThat(response.category()).isEqualTo("DEPARTMENT_INSIGHT");
        assertThat(response.answer()).contains("Engineering");
        assertThat(response.answer()).contains("3,500 people");
    }

    @Test
    @DisplayName("Should answer questions regarding specific country")
    void shouldAnswerCountryQuestion() {
        AssistantQueryResponse response = assistantService.answerQuestion("How does ACME pay people in Germany?");
        assertThat(response.category()).isEqualTo("COUNTRY_INSIGHT");
        assertThat(response.answer()).contains("Germany");
        assertThat(response.answer()).contains("EUR");
    }

    @Test
    @DisplayName("Should answer questions regarding median salary")
    void shouldAnswerMedianQuestion() {
        AssistantQueryResponse response = assistantService.answerQuestion("What is the median salary?");
        assertThat(response.category()).isEqualTo("OVERALL_METRIC");
        assertThat(response.answer()).contains("median");
    }
}
