package dev.zenith.finance.zenith_backend.infrastructure.mapper;

import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.infrastructure.persistence.entity.UserAccountJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccount toDomain(UserAccountJpaEntity entity);

    UserAccountJpaEntity toEntity(UserAccount domain);
}

