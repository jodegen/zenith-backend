package dev.zenith.finance.zenith_backend.infrastructure.persistence.repository;

import dev.zenith.finance.zenith_backend.infrastructure.persistence.entity.UserAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountJpaEntity, UUID> {
    Optional<UserAccountJpaEntity> findByCognitoSub(String cognitoSub);
    boolean existsByCognitoSub(String cognitoSub);
}

