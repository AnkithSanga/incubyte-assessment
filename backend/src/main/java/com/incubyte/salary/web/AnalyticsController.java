package com.incubyte.salary.web;

import com.incubyte.salary.service.SalaryAnalyticsService;
import com.incubyte.salary.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Compensation intelligence, KPI summaries, and distribution statistics")
public class AnalyticsController {

    private final SalaryAnalyticsService analyticsService;

    public AnalyticsController(SalaryAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/kpis")
    @Operation(summary = "Get overall compensation KPIs", description = "Returns total employees, total payroll in USD, average and median salaries")
    public OverallKpiResponse getKpis() {
        return analyticsService.getOverallKpis();
    }

    @GetMapping("/departments")
    @Operation(summary = "Get department compensation breakdown", description = "Returns headcount, total payroll, and average/min/max salary per department")
    public List<DepartmentAnalyticsResponse> getDepartmentAnalytics() {
        return analyticsService.getDepartmentAnalytics();
    }

    @GetMapping("/countries")
    @Operation(summary = "Get geographic compensation breakdown", description = "Returns headcount, currency, and normalized payroll per country")
    public List<CountryAnalyticsResponse> getCountryAnalytics() {
        return analyticsService.getCountryAnalytics();
    }

    @GetMapping("/distribution")
    @Operation(summary = "Get salary band histogram", description = "Returns employee counts across defined USD compensation brackets")
    public List<SalaryBandDistribution> getSalaryDistribution() {
        return analyticsService.getSalaryDistribution();
    }
}
