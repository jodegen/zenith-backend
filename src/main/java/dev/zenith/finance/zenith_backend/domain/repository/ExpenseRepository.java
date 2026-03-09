package dev.zenith.finance.zenith_backend.domain.repository;

import dev.zenith.finance.zenith_backend.domain.model.Expense;
import dev.zenith.finance.zenith_backend.domain.model.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {
    Page<Expense> findAllByOwnerIdAndStatus(UUID ownerId, ExpenseStatus status, Pageable pageable);
    Page<Expense> findAllByOwnerIdAndCategoryIdAndStatus(UUID ownerId, UUID categoryId, ExpenseStatus status, Pageable pageable);
    Page<Expense> findAllByOwnerIdAndStatusAndDateBetween(UUID ownerId, ExpenseStatus status, LocalDate from, LocalDate to, Pageable pageable);
    Optional<Expense> findByIdAndOwnerId(UUID expenseId, UUID ownerId);
    Expense save(Expense expense);
}

