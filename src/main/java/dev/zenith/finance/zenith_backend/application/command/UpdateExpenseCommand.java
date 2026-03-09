package dev.zenith.finance.zenith_backend.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateExpenseCommand(
        @NotNull UUID expenseId,
        @NotBlank String cognitoSub,
        UUID categoryId,
        @Positive BigDecimal amount,
        String currency,
        String description,
        LocalDate date
) {}

