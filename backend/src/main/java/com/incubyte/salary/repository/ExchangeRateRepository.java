package com.incubyte.salary.repository;

import com.incubyte.salary.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByCurrencyCode(String currencyCode);
    boolean existsByCurrencyCode(String currencyCode);
}
