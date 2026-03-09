package dev.zenith.finance.zenith_backend.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseDto(
        UUID id,
        UUID categoryId,
        String categoryName,
        BigDecimal amount,
        String currency,
        String description,
        LocalDate date
) {}

