package com.incubyte.salary.web;

import com.incubyte.salary.repository.ExchangeRateRepository;
import com.incubyte.salary.web.dto.ExchangeRateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@Tag(name = "Exchange Rates", description = "Seeded reference foreign exchange rates to USD")
public class ExchangeRateController {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateController(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @GetMapping
    @Operation(summary = "List all exchange rates", description = "Returns conversion rates to USD for all supported currencies")
    public List<ExchangeRateResponse> getAllRates() {
        return exchangeRateRepository.findAll().stream()
                .map(ExchangeRateResponse::fromEntity)
                .toList();
    }
}
