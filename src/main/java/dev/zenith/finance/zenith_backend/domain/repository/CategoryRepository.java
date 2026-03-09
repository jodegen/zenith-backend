package dev.zenith.finance.zenith_backend.domain.repository;

import dev.zenith.finance.zenith_backend.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    List<Category> findAllByOwnerId(UUID ownerId);
    Optional<Category> findByIdAndOwnerId(UUID categoryId, UUID ownerId);
    Category save(Category category);
    void deleteById(UUID id);
}

