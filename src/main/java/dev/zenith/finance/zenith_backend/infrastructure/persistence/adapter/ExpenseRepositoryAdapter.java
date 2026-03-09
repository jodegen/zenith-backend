package dev.zenith.finance.zenith_backend.infrastructure.persistence.adapter;

import dev.zenith.finance.zenith_backend.domain.model.Expense;
import dev.zenith.finance.zenith_backend.domain.model.ExpenseStatus;
import dev.zenith.finance.zenith_backend.domain.repository.ExpenseRepository;
import dev.zenith.finance.zenith_backend.infrastructure.mapper.ExpenseMapper;
import dev.zenith.finance.zenith_backend.infrastructure.persistence.repository.ExpenseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;
    private final ExpenseMapper mapper;

    @Override
    public Page<Expense> findAllByOwnerIdAndStatus(UUID ownerId, ExpenseStatus status, Pageable pageable) {
        return jpaRepository.findAllByOwnerIdAndStatus(ownerId, status, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Expense> findAllByOwnerIdAndCategoryIdAndStatus(UUID ownerId, UUID categoryId, ExpenseStatus status, Pageable pageable) {
        return jpaRepository.findAllByOwnerIdAndCategoryIdAndStatus(ownerId, categoryId, status, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Expense> findAllByOwnerIdAndStatusAndDateBetween(UUID ownerId, ExpenseStatus status, LocalDate from, LocalDate to, Pageable pageable) {
        return jpaRepository.findAllByOwnerIdAndStatusAndDateBetween(ownerId, status, from, to, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Expense> findByIdAndOwnerId(UUID expenseId, UUID ownerId) {
        return jpaRepository.findByIdAndOwnerId(expenseId, ownerId).map(mapper::toDomain);
    }

    @Override
    public Expense save(Expense expense) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(expense)));
    }
}

