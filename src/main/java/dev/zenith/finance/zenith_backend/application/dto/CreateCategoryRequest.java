package dev.zenith.finance.zenith_backend.application.dto;

import dev.zenith.finance.zenith_backend.domain.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
        @NotBlank String name,
        @NotNull CategoryType type
) {}

