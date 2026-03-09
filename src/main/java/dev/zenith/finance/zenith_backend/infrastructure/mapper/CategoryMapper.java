package dev.zenith.finance.zenith_backend.infrastructure.mapper;

import dev.zenith.finance.zenith_backend.domain.model.Category;
import dev.zenith.finance.zenith_backend.infrastructure.persistence.entity.CategoryJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserAccountMapper.class})
public interface CategoryMapper {

    @Mapping(target = "defaultCategory", source = "defaultCategory")
    Category toDomain(CategoryJpaEntity entity);

    @Mapping(target = "defaultCategory", source = "defaultCategory")
    CategoryJpaEntity toEntity(Category domain);
}

