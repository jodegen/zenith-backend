package dev.zenith.finance.zenith_backend.application.query;

import java.util.UUID;

public record GetExpenseByIdQuery(
        UUID expenseId,
        String cognitoSub
) {}

