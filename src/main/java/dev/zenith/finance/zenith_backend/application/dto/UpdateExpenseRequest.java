package dev.zenith.finance.zenith_backend.application.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateExpenseRequest(
        UUID categoryId,
        @Positive BigDecimal amount,
        String currency,
        String description,
        LocalDate date
) {}

