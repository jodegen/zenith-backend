package dev.zenith.finance.zenith_backend.application.dto;

import dev.zenith.finance.zenith_backend.domain.model.CategoryType;

import java.util.UUID;

public record CategoryDto(
        UUID id,
        String name,
        CategoryType type,
        boolean defaultCategory
) {}

