package dev.zenith.finance.zenith_backend.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record GetExpensesByUserQuery(
        String cognitoSub,
        int page,
        int size,
        UUID categoryId,
        LocalDate from,
        LocalDate to
) {}

