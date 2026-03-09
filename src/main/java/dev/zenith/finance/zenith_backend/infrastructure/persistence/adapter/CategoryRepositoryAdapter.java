package dev.zenith.finance.zenith_backend.infrastructure.persistence.adapter;

import dev.zenith.finance.zenith_backend.domain.model.Category;
import dev.zenith.finance.zenith_backend.domain.repository.CategoryRepository;
import dev.zenith.finance.zenith_backend.infrastructure.mapper.CategoryMapper;
import dev.zenith.finance.zenith_backend.infrastructure.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryMapper mapper;

    @Override
    public List<Category> findAllByOwnerId(UUID ownerId) {
        return jpaRepository.findAllByOwnerId(ownerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Category> findByIdAndOwnerId(UUID categoryId, UUID ownerId) {
        return jpaRepository.findByIdAndOwnerId(categoryId, ownerId).map(mapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(category)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

