package com.incubyte.salary.seeder;

import com.incubyte.salary.domain.Employee;
import com.incubyte.salary.domain.ExchangeRate;
import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.repository.ExchangeRateRepository;
import com.incubyte.salary.service.CurrencyConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class DataSeederService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeederService.class);
    private static final int TARGET_EMPLOYEES = 10_000;
    private static final int BATCH_SIZE = 1_000;

    private final ExchangeRateRepository exchangeRateRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrencyConversionService conversionService;

    public DataSeederService(ExchangeRateRepository exchangeRateRepository,
                             EmployeeRepository employeeRepository,
                             CurrencyConversionService conversionService) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.employeeRepository = employeeRepository;
        this.conversionService = conversionService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedExchangeRates();
        conversionService.refreshCache();
        seedEmployees();
    }

    private void seedExchangeRates() {
        if (exchangeRateRepository.count() > 0) {
            log.info("Exchange rates already seeded. Skipping.");
            return;
        }

        List<ExchangeRate> rates = List.of(
            new ExchangeRate("USD", "$", "United States", new BigDecimal("1.000000")),
            new ExchangeRate("EUR", "€", "Germany", new BigDecimal("1.090000")),
            new ExchangeRate("GBP", "£", "United Kingdom", new BigDecimal("1.300000")),
            new ExchangeRate("INR", "₹", "India", new BigDecimal("0.012000")),
            new ExchangeRate("CAD", "C$", "Canada", new BigDecimal("0.740000")),
            new ExchangeRate("JPY", "¥", "Japan", new BigDecimal("0.006900")),
            new ExchangeRate("AUD", "A$", "Australia", new BigDecimal("0.670000")),
            new ExchangeRate("SGD", "S$", "Singapore", new BigDecimal("0.760000"))
        );

        exchangeRateRepository.saveAll(rates);
        log.info("Successfully seeded {} reference exchange rates.", rates.size());
    }

    private void seedEmployees() {
        long currentCount = employeeRepository.count();
        if (currentCount >= TARGET_EMPLOYEES) {
            log.info("Employee database already contains {} records. Seeding skipped.", currentCount);
            return;
        }

        log.info("Starting deterministic synthetic seeding of {} employees...", TARGET_EMPLOYEES);
        long startTime = System.currentTimeMillis();

        Random random = new Random(42); // Fixed seed for 100% deterministic repeatability

        String[] firstNames = {
            "Alexander", "Amara", "Carlos", "David", "Elena", "Fatima", "Grace", "Hans",
            "Ibrahim", "Julia", "Kenji", "Liam", "Mei", "Nikolai", "Olivia", "Priya",
            "Quinn", "Rahul", "Sophia", "Tariq", "Uma", "Victor", "Wei", "Xavier", "Yuki", "Zara",
            "Lucas", "Ananya", "Arjun", "Chloe", "Daniel", "Emma", "Felix", "Hana", "Isaac"
        };

        String[] lastNames = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
            "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Patel", "Sharma", "Singh",
            "Kumar", "Sato", "Suzuki", "Takahashi", "Tanaka", "Watanabe", "Tremblay", "Roy",
            "Wilson", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin"
        };

        record Location(String country, String[] cities, String currency, int minLocal, int maxLocal) {}
        Location[] locations = {
            new Location("United States", new String[]{"San Francisco", "New York", "Austin", "Seattle"}, "USD", 75_000, 215_000),
            new Location("Germany", new String[]{"Berlin", "Munich", "Frankfurt", "Hamburg"}, "EUR", 52_000, 125_000),
            new Location("United Kingdom", new String[]{"London", "Manchester", "Edinburgh", "Cambridge"}, "GBP", 45_000, 115_000),
            new Location("India", new String[]{"Bengaluru", "Hyderabad", "Pune", "Gurgaon"}, "INR", 900_000, 4_500_000),
            new Location("Japan", new String[]{"Tokyo", "Osaka", "Kyoto", "Yokohama"}, "JPY", 5_500_000, 16_000_000),
            new Location("Canada", new String[]{"Toronto", "Vancouver", "Montreal", "Calgary"}, "CAD", 70_000, 165_000)
        };

        record DeptConfig(String name, String[] titles, double salaryMultiplier) {}
        DeptConfig[] departments = {
            new DeptConfig("Engineering", new String[]{"Software Engineer", "Senior Software Engineer", "Staff Engineer", "QA Engineer", "DevOps Engineer", "Engineering Manager"}, 1.25),
            new DeptConfig("Product", new String[]{"Associate Product Manager", "Product Manager", "Senior Product Manager", "Product Designer", "VP of Product"}, 1.18),
            new DeptConfig("Sales", new String[]{"Account Executive", "Senior Account Executive", "Sales Development Rep", "Sales Director", "Enterprise Account Lead"}, 1.10),
            new DeptConfig("Marketing", new String[]{"Content Strategist", "Growth Marketing Lead", "Digital Marketing Specialist", "Brand Manager"}, 0.95),
            new DeptConfig("Human Resources", new String[]{"HR Generalist", "Senior HR Business Partner", "Talent Acquisition Specialist", "People Operations Manager"}, 0.90),
            new DeptConfig("Finance", new String[]{"Financial Analyst", "Senior Accountant", "Finance Controller", "Payroll Specialist"}, 1.05),
            new DeptConfig("Operations", new String[]{"Operations Coordinator", "Operations Specialist", "Supply Chain Analyst", "Director of Operations"}, 0.92)
        };

        List<Employee> batch = new ArrayList<>(BATCH_SIZE);
        Set<String> generatedEmails = new HashSet<>(TARGET_EMPLOYEES);

        for (int i = 1; i <= TARGET_EMPLOYEES; i++) {
            String empId = String.format("EMP-%05d", i);
            String first = firstNames[random.nextInt(firstNames.length)];
            String last = lastNames[random.nextInt(lastNames.length)];

            String emailCandidate = (first.toLowerCase() + "." + last.toLowerCase() + i + "@acme.corp");
            String email = emailCandidate.replaceAll("[^a-z0-9.@]", "");

            Location loc = locations[random.nextInt(locations.length)];
            String city = loc.cities()[random.nextInt(loc.cities().length)];

            DeptConfig dept = departments[random.nextInt(departments.length)];
            String title = dept.titles()[random.nextInt(dept.titles().length)];

            // Base calculation using local range, department multiplier, and random distribution
            int baseRange = loc.maxLocal() - loc.minLocal();
            double factor = Math.pow(random.nextDouble(), 1.15); // Slight right-skew typical for compensation
            double rawSalary = (loc.minLocal() + (baseRange * factor)) * dept.salaryMultiplier();

            // Round to sensible clean denominations depending on currency
            BigDecimal localSalary;
            if (loc.currency().equals("INR")) {
                long rounded = Math.round(rawSalary / 10_000.0) * 10_000;
                localSalary = BigDecimal.valueOf(rounded).setScale(2, RoundingMode.HALF_UP);
            } else if (loc.currency().equals("JPY")) {
                long rounded = Math.round(rawSalary / 50_000.0) * 50_000;
                localSalary = BigDecimal.valueOf(rounded).setScale(2, RoundingMode.HALF_UP);
            } else {
                long rounded = Math.round(rawSalary / 500.0) * 500;
                localSalary = BigDecimal.valueOf(rounded).setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal usdSalary = conversionService.convertToUsd(localSalary, loc.currency());

            Employee employee = new Employee(
                empId, first, last, email, dept.name(), title, loc.country(), city, loc.currency(), localSalary, usdSalary
            );
            batch.add(employee);

            if (batch.size() == BATCH_SIZE) {
                employeeRepository.saveAll(batch);
                batch.clear();
                log.info("Seeded {} / {} employees...", i, TARGET_EMPLOYEES);
            }
        }

        if (!batch.isEmpty()) {
            employeeRepository.saveAll(batch);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Completed seeding {} employees in {} ms ({} seconds).", TARGET_EMPLOYEES, elapsed, elapsed / 1000.0);
    }
}
