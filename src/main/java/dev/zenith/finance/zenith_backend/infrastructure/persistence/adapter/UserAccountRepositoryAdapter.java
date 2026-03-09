package dev.zenith.finance.zenith_backend.infrastructure.persistence.adapter;

import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import dev.zenith.finance.zenith_backend.infrastructure.mapper.UserAccountMapper;
import dev.zenith.finance.zenith_backend.infrastructure.persistence.repository.UserAccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAccountRepositoryAdapter implements UserAccountRepository {

    private final UserAccountJpaRepository jpaRepository;
    private final UserAccountMapper mapper;

    @Override
    public Optional<UserAccount> findByCognitoSub(String cognitoSub) {
        return jpaRepository.findByCognitoSub(cognitoSub).map(mapper::toDomain);
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(userAccount)));
    }

    @Override
    public boolean existsByCognitoSub(String cognitoSub) {
        return jpaRepository.existsByCognitoSub(cognitoSub);
    }
}

