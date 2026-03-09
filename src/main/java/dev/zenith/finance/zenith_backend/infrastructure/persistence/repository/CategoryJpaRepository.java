package dev.zenith.finance.zenith_backend.infrastructure.persistence.repository;

import dev.zenith.finance.zenith_backend.infrastructure.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {
    List<CategoryJpaEntity> findAllByOwnerId(UUID ownerId);
    Optional<CategoryJpaEntity> findByIdAndOwnerId(UUID id, UUID ownerId);
}

