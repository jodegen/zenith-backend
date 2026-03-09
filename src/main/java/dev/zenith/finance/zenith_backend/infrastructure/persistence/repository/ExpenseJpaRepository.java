package dev.zenith.finance.zenith_backend.infrastructure.persistence.repository;

import dev.zenith.finance.zenith_backend.domain.model.ExpenseStatus;
import dev.zenith.finance.zenith_backend.infrastructure.persistence.entity.ExpenseJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseJpaEntity, UUID> {
    Page<ExpenseJpaEntity> findAllByOwnerIdAndStatus(UUID ownerId, ExpenseStatus status, Pageable pageable);
    Page<ExpenseJpaEntity> findAllByOwnerIdAndCategoryIdAndStatus(UUID ownerId, UUID categoryId, ExpenseStatus status, Pageable pageable);
    Page<ExpenseJpaEntity> findAllByOwnerIdAndStatusAndDateBetween(UUID ownerId, ExpenseStatus status, LocalDate from, LocalDate to, Pageable pageable);
    Optional<ExpenseJpaEntity> findByIdAndOwnerId(UUID id, UUID ownerId);
}

