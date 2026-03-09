package dev.zenith.finance.zenith_backend.domain.repository;

import dev.zenith.finance.zenith_backend.domain.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository {
    Optional<UserAccount> findByCognitoSub(String cognitoSub);
    Optional<UserAccount> findById(UUID id);
    UserAccount save(UserAccount userAccount);
    boolean existsByCognitoSub(String cognitoSub);
}

