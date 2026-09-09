package com.incubyte.salary.service;

import com.incubyte.salary.web.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

@Service
public class AssistantService {

    private final SalaryAnalyticsService analyticsService;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public AssistantService(SalaryAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    public AssistantQueryResponse answerQuestion(String question) {
        if (question == null || question.isBlank()) {
            return new AssistantQueryResponse(
                "",
                "Please provide a question about organizational compensation.",
                "GENERAL",
                Map.of()
            );
        }

        String q = question.toLowerCase();
        OverallKpiResponse kpis = analyticsService.getOverallKpis();
        List<DepartmentAnalyticsResponse> depts = analyticsService.getDepartmentAnalytics();
        List<CountryAnalyticsResponse> countries = analyticsService.getCountryAnalytics();

        // 1. Highest / Lowest paying department
        if (q.contains("highest") && (q.contains("department") || q.contains("dept"))) {
            DepartmentAnalyticsResponse top = depts.stream()
                .max(Comparator.comparing(DepartmentAnalyticsResponse::averageSalaryUsd))
                .orElse(null);
            if (top != null) {
                return new AssistantQueryResponse(
                    question,
                    String.format("The highest-paying department by average base salary is %s, with an average salary of %s (Headcount: %d, Total Payroll: %s).",
                        top.department(), formatUsd(top.averageSalaryUsd()), top.headcount(), formatUsd(top.totalPayrollUsd())),
                    "DEPARTMENT_INSIGHT",
                    Map.of("department", top.department(), "averageSalaryUsd", top.averageSalaryUsd(), "headcount", top.headcount())
                );
            }
        }

        // 2. Department specific query (e.g. "engineering", "sales", "product")
        for (DepartmentAnalyticsResponse d : depts) {
            if (q.contains(d.department().toLowerCase())) {
                return new AssistantQueryResponse(
                    question,
                    String.format("In %s, ACME employs %,d people with a total payroll of %s. The average annual base salary is %s (Range: %s - %s), representing %.1f%% of global payroll.",
                        d.department(), d.headcount(), formatUsd(d.totalPayrollUsd()), formatUsd(d.averageSalaryUsd()),
                        formatUsd(d.minSalaryUsd()), formatUsd(d.maxSalaryUsd()), d.payrollPercentage()),
                    "DEPARTMENT_INSIGHT",
                    Map.of("department", d.department(), "headcount", d.headcount(), "averageSalaryUsd", d.averageSalaryUsd(), "totalPayrollUsd", d.totalPayrollUsd())
                );
            }
        }

        // 3. Country specific query (e.g. "germany", "india", "united states", "uk", "japan", "canada")
        for (CountryAnalyticsResponse c : countries) {
            if (q.contains(c.country().toLowerCase()) || (c.country().equalsIgnoreCase("United States") && (q.contains("us") || q.contains("usa"))) || (c.country().equalsIgnoreCase("United Kingdom") && q.contains("uk"))) {
                return new AssistantQueryResponse(
                    question,
                    String.format("In %s, ACME employs %,d people. Total annual payroll normalized to USD is %s, with an average annual base salary of %s (Native currency: %s).",
                        c.country(), c.headcount(), formatUsd(c.totalPayrollUsd()), formatUsd(c.averageSalaryUsd()), c.currencyCode()),
                    "COUNTRY_INSIGHT",
                    Map.of("country", c.country(), "headcount", c.headcount(), "averageSalaryUsd", c.averageSalaryUsd(), "currencyCode", c.currencyCode())
                );
            }
        }

        // 4. Median salary query
        if (q.contains("median")) {
            return new AssistantQueryResponse(
                question,
                String.format("The global median annual base salary across all 10,000 ACME employees is %s (Average is %s).",
                    formatUsd(kpis.medianSalaryUsd()), formatUsd(kpis.averageSalaryUsd())),
                "OVERALL_METRIC",
                Map.of("medianSalaryUsd", kpis.medianSalaryUsd(), "averageSalaryUsd", kpis.averageSalaryUsd())
            );
        }

        // 5. Total payroll or employee count
        if (q.contains("total") || q.contains("payroll") || q.contains("how many") || q.contains("headcount")) {
            return new AssistantQueryResponse(
                question,
                String.format("ACME Organization employs %,d total employees globally with a total annual base payroll of %s. The average salary is %s and the median is %s.",
                    kpis.totalEmployees(), formatUsd(kpis.totalPayrollUsd()), formatUsd(kpis.averageSalaryUsd()), formatUsd(kpis.medianSalaryUsd())),
                "OVERALL_METRIC",
                Map.of("totalEmployees", kpis.totalEmployees(), "totalPayrollUsd", kpis.totalPayrollUsd(), "averageSalaryUsd", kpis.averageSalaryUsd())
            );
        }

        // Default response with overview KPIs
        return new AssistantQueryResponse(
            question,
            String.format("ACME manages compensation for %,d employees globally with an annual base payroll of %s. The average salary is %s and median is %s. You can ask specifically about departments (e.g. 'Engineering', 'Sales'), countries (e.g. 'Germany', 'India'), or metrics like 'median salary'.",
                kpis.totalEmployees(), formatUsd(kpis.totalPayrollUsd()), formatUsd(kpis.averageSalaryUsd()), formatUsd(kpis.medianSalaryUsd())),
            "GENERAL_INSIGHT",
            Map.of("totalEmployees", kpis.totalEmployees(), "totalPayrollUsd", kpis.totalPayrollUsd())
        );
    }

    private String formatUsd(BigDecimal amount) {
        if (amount == null) return "$0";
        return currencyFormat.format(amount);
    }
}
