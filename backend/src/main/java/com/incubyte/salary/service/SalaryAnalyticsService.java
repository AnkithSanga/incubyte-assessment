package com.incubyte.salary.service;

import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.repository.projection.CountrySalaryStats;
import com.incubyte.salary.repository.projection.DepartmentSalaryStats;
import com.incubyte.salary.repository.projection.OverallSalaryStats;
import com.incubyte.salary.web.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SalaryAnalyticsService {

    private final EmployeeRepository employeeRepository;

    public SalaryAnalyticsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public OverallKpiResponse getOverallKpis() {
        OverallSalaryStats stats = employeeRepository.getOverallStats();
        if (stats == null || stats.getTotalEmployees() == 0) {
            return new OverallKpiResponse(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, "N/A", "N/A");
        }

        BigDecimal median = calculateMedianSalary();

        List<DepartmentSalaryStats> deptStats = employeeRepository.getDepartmentStats();
        String topDept = deptStats.isEmpty() ? "N/A" : deptStats.get(0).getDepartment();

        List<CountrySalaryStats> countryStats = employeeRepository.getCountryStats();
        String topCountry = countryStats.isEmpty() ? "N/A" : countryStats.get(0).getCountry();

        return new OverallKpiResponse(
                stats.getTotalEmployees(),
                stats.getTotalPayrollUsd().setScale(2, RoundingMode.HALF_UP),
                stats.getAverageSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                median.setScale(2, RoundingMode.HALF_UP),
                stats.getMinSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                stats.getMaxSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                topDept,
                topCountry
        );
    }

    public List<DepartmentAnalyticsResponse> getDepartmentAnalytics() {
        OverallSalaryStats overall = employeeRepository.getOverallStats();
        BigDecimal totalPayroll = (overall != null && overall.getTotalPayrollUsd() != null && overall.getTotalPayrollUsd().compareTo(BigDecimal.ZERO) > 0)
                ? overall.getTotalPayrollUsd()
                : BigDecimal.ONE;

        List<DepartmentSalaryStats> raw = employeeRepository.getDepartmentStats();
        List<DepartmentAnalyticsResponse> result = new ArrayList<>();

        for (DepartmentSalaryStats s : raw) {
            double pct = s.getTotalPayrollUsd()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalPayroll, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            result.add(new DepartmentAnalyticsResponse(
                    s.getDepartment(),
                    s.getHeadcount(),
                    s.getTotalPayrollUsd().setScale(2, RoundingMode.HALF_UP),
                    s.getAverageSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                    s.getMinSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                    s.getMaxSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                    pct
            ));
        }
        return result;
    }

    public List<CountryAnalyticsResponse> getCountryAnalytics() {
        OverallSalaryStats overall = employeeRepository.getOverallStats();
        BigDecimal totalPayroll = (overall != null && overall.getTotalPayrollUsd() != null && overall.getTotalPayrollUsd().compareTo(BigDecimal.ZERO) > 0)
                ? overall.getTotalPayrollUsd()
                : BigDecimal.ONE;

        List<CountrySalaryStats> raw = employeeRepository.getCountryStats();
        List<CountryAnalyticsResponse> result = new ArrayList<>();

        for (CountrySalaryStats s : raw) {
            double pct = s.getTotalPayrollUsd()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalPayroll, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            result.add(new CountryAnalyticsResponse(
                    s.getCountry(),
                    s.getCurrencyCode(),
                    s.getHeadcount(),
                    s.getTotalPayrollUsd().setScale(2, RoundingMode.HALF_UP),
                    s.getAverageSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                    s.getMinSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                    s.getMaxSalaryUsd().setScale(2, RoundingMode.HALF_UP),
                    pct
            ));
        }
        return result;
    }

    public List<SalaryBandDistribution> getSalaryDistribution() {
        List<BigDecimal> sortedSalaries = employeeRepository.findAllSalariesUsdSorted();
        int total = sortedSalaries.size();
        if (total == 0) return List.of();

        long b1 = 0; // < 50k
        long b2 = 0; // 50k - 75k
        long b3 = 0; // 75k - 100k
        long b4 = 0; // 100k - 150k
        long b5 = 0; // 150k - 200k
        long b6 = 0; // > 200k

        BigDecimal d50 = BigDecimal.valueOf(50_000);
        BigDecimal d75 = BigDecimal.valueOf(75_000);
        BigDecimal d100 = BigDecimal.valueOf(100_000);
        BigDecimal d150 = BigDecimal.valueOf(150_000);
        BigDecimal d200 = BigDecimal.valueOf(200_000);

        for (BigDecimal s : sortedSalaries) {
            if (s.compareTo(d50) < 0) {
                b1++;
            } else if (s.compareTo(d75) < 0) {
                b2++;
            } else if (s.compareTo(d100) < 0) {
                b3++;
            } else if (s.compareTo(d150) < 0) {
                b4++;
            } else if (s.compareTo(d200) < 0) {
                b5++;
            } else {
                b6++;
            }
        }

        return List.of(
            new SalaryBandDistribution("< $50K", BigDecimal.ZERO, d50, b1, roundPct(b1, total)),
            new SalaryBandDistribution("$50K - $75K", d50, d75, b2, roundPct(b2, total)),
            new SalaryBandDistribution("$75K - $100K", d75, d100, b3, roundPct(b3, total)),
            new SalaryBandDistribution("$100K - $150K", d100, d150, b4, roundPct(b4, total)),
            new SalaryBandDistribution("$150K - $200K", d150, d200, b5, roundPct(b5, total)),
            new SalaryBandDistribution("> $200K", d200, BigDecimal.valueOf(Long.MAX_VALUE), b6, roundPct(b6, total))
        );
    }

    private double roundPct(long count, int total) {
        if (total == 0) return 0.0;
        return BigDecimal.valueOf((double) count * 100.0 / total)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private BigDecimal calculateMedianSalary() {
        List<BigDecimal> sorted = employeeRepository.findAllSalariesUsdSorted();
        if (sorted.isEmpty()) return BigDecimal.ZERO;

        int size = sorted.size();
        if (size % 2 == 1) {
            return sorted.get(size / 2);
        } else {
            BigDecimal mid1 = sorted.get((size / 2) - 1);
            BigDecimal mid2 = sorted.get(size / 2);
            return mid1.add(mid2).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        }
    }
}
