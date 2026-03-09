package dev.zenith.finance.zenith_backend.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateExpenseCommand(
        @NotBlank String cognitoSub,
        @NotNull UUID categoryId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency,
        String description,
        @NotNull LocalDate date
) {}

