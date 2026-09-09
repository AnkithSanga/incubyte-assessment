package com.incubyte.salary.service;

import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.repository.projection.DepartmentSalaryStats;
import com.incubyte.salary.repository.projection.OverallSalaryStats;
import com.incubyte.salary.web.dto.OverallKpiResponse;
import com.incubyte.salary.web.dto.SalaryBandDistribution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaryAnalyticsServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private SalaryAnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new SalaryAnalyticsService(employeeRepository);
    }

    @Test
    @DisplayName("Should calculate overall KPIs and exact median for odd number of records")
    void shouldCalculateOverallKpisWithOddMedian() {
        OverallSalaryStats mockStats = mock(OverallSalaryStats.class);
        when(mockStats.getTotalEmployees()).thenReturn(5L);
        when(mockStats.getTotalPayrollUsd()).thenReturn(new BigDecimal("500000.00"));
        when(mockStats.getAverageSalaryUsd()).thenReturn(new BigDecimal("100000.00"));
        when(mockStats.getMinSalaryUsd()).thenReturn(new BigDecimal("60000.00"));
        when(mockStats.getMaxSalaryUsd()).thenReturn(new BigDecimal("150000.00"));

        when(employeeRepository.getOverallStats()).thenReturn(mockStats);
        when(employeeRepository.findAllSalariesUsdSorted()).thenReturn(List.of(
            new BigDecimal("60000.00"),
            new BigDecimal("80000.00"),
            new BigDecimal("100000.00"), // median
            new BigDecimal("110000.00"),
            new BigDecimal("150000.00")
        ));

        OverallKpiResponse kpis = analyticsService.getOverallKpis();

        assertThat(kpis.totalEmployees()).isEqualTo(5L);
        assertThat(kpis.totalPayrollUsd()).isEqualByComparingTo(new BigDecimal("500000.00"));
        assertThat(kpis.medianSalaryUsd()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(kpis.averageSalaryUsd()).isEqualByComparingTo(new BigDecimal("100000.00"));
    }

    @Test
    @DisplayName("Should calculate exact median for even number of records")
    void shouldCalculateEvenMedian() {
        OverallSalaryStats mockStats = mock(OverallSalaryStats.class);
        when(mockStats.getTotalEmployees()).thenReturn(4L);
        when(mockStats.getTotalPayrollUsd()).thenReturn(new BigDecimal("400000.00"));
        when(mockStats.getAverageSalaryUsd()).thenReturn(new BigDecimal("100000.00"));
        when(mockStats.getMinSalaryUsd()).thenReturn(new BigDecimal("60000.00"));
        when(mockStats.getMaxSalaryUsd()).thenReturn(new BigDecimal("140000.00"));

        when(employeeRepository.getOverallStats()).thenReturn(mockStats);
        when(employeeRepository.findAllSalariesUsdSorted()).thenReturn(List.of(
            new BigDecimal("60000.00"),
            new BigDecimal("80000.00"),
            new BigDecimal("120000.00"),
            new BigDecimal("140000.00")
        )); // median = (80000 + 120000) / 2 = 100000.00

        OverallKpiResponse kpis = analyticsService.getOverallKpis();
        assertThat(kpis.medianSalaryUsd()).isEqualByComparingTo(new BigDecimal("100000.00"));
    }

    @Test
    @DisplayName("Should categorize salaries into proper distribution brackets")
    void shouldCategorizeSalaryBands() {
        when(employeeRepository.findAllSalariesUsdSorted()).thenReturn(List.of(
            new BigDecimal("45000.00"),  // < 50k
            new BigDecimal("65000.00"),  // 50k - 75k
            new BigDecimal("90000.00"),  // 75k - 100k
            new BigDecimal("120000.00"), // 100k - 150k
            new BigDecimal("180000.00"), // 150k - 200k
            new BigDecimal("220000.00")  // > 200k
        ));

        List<SalaryBandDistribution> bands = analyticsService.getSalaryDistribution();

        assertThat(bands).hasSize(6);
        assertThat(bands.get(0).label()).isEqualTo("< $50K");
        assertThat(bands.get(0).count()).isEqualTo(1L);
        assertThat(bands.get(5).label()).isEqualTo("> $200K");
        assertThat(bands.get(5).count()).isEqualTo(1L);
    }
}
