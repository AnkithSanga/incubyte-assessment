package com.incubyte.salary.repository;

import com.incubyte.salary.domain.Employee;
import com.incubyte.salary.repository.projection.CountrySalaryStats;
import com.incubyte.salary.repository.projection.DepartmentSalaryStats;
import com.incubyte.salary.repository.projection.OverallSalaryStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
        SELECT e FROM Employee e
        WHERE (:department IS NULL OR e.department = :department)
          AND (:country IS NULL OR e.country = :country)
          AND (:search IS NULL OR
               LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<Employee> findByFilters(
            @Param("department") String department,
            @Param("country") String country,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(e) as totalEmployees,
               COALESCE(SUM(e.annualBaseSalaryUsd), 0) as totalPayrollUsd,
               COALESCE(AVG(e.annualBaseSalaryUsd), 0) as averageSalaryUsd,
               COALESCE(MIN(e.annualBaseSalaryUsd), 0) as minSalaryUsd,
               COALESCE(MAX(e.annualBaseSalaryUsd), 0) as maxSalaryUsd
        FROM Employee e
    """)
    OverallSalaryStats getOverallStats();

    @Query("""
        SELECT e.department as department,
               COUNT(e) as headcount,
               SUM(e.annualBaseSalaryUsd) as totalPayrollUsd,
               AVG(e.annualBaseSalaryUsd) as averageSalaryUsd,
               MIN(e.annualBaseSalaryUsd) as minSalaryUsd,
               MAX(e.annualBaseSalaryUsd) as maxSalaryUsd
        FROM Employee e
        GROUP BY e.department
        ORDER BY SUM(e.annualBaseSalaryUsd) DESC
    """)
    List<DepartmentSalaryStats> getDepartmentStats();

    @Query("""
        SELECT e.country as country,
               e.currencyCode as currencyCode,
               COUNT(e) as headcount,
               SUM(e.annualBaseSalaryUsd) as totalPayrollUsd,
               AVG(e.annualBaseSalaryUsd) as averageSalaryUsd,
               MIN(e.annualBaseSalaryUsd) as minSalaryUsd,
               MAX(e.annualBaseSalaryUsd) as maxSalaryUsd
        FROM Employee e
        GROUP BY e.country, e.currencyCode
        ORDER BY COUNT(e) DESC
    """)
    List<CountrySalaryStats> getCountryStats();

    @Query("SELECT e.annualBaseSalaryUsd FROM Employee e ORDER BY e.annualBaseSalaryUsd ASC")
    List<BigDecimal> findAllSalariesUsdSorted();

    @Query("SELECT DISTINCT e.department FROM Employee e ORDER BY e.department ASC")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT e.country FROM Employee e ORDER BY e.country ASC")
    List<String> findDistinctCountries();
}
