package dev.zenith.finance.zenith_backend.application.command;

import dev.zenith.finance.zenith_backend.domain.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryCommand(
        @NotBlank String cognitoSub,
        @NotBlank String name,
        @NotNull CategoryType type
) {}

