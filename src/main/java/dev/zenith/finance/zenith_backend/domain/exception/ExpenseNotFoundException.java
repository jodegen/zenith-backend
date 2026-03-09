package dev.zenith.finance.zenith_backend.domain.exception;

import java.util.UUID;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException(UUID expenseId) {
        super("Expense not found: " + expenseId);
    }
}

