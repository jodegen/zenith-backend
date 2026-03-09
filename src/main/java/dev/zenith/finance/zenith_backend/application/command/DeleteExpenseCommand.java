package dev.zenith.finance.zenith_backend.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteExpenseCommand(
        @NotNull UUID expenseId,
        @NotBlank String cognitoSub
) {}

