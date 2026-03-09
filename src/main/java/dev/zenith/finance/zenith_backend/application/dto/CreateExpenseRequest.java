package dev.zenith.finance.zenith_backend.application.dto;

import dev.zenith.finance.zenith_backend.domain.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateExpenseRequest(
        @NotNull UUID categoryId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency,
        String description,
        @NotNull LocalDate date
) {}

