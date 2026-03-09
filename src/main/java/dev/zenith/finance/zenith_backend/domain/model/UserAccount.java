package dev.zenith.finance.zenith_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserAccount {

    private final UUID id;
    private final String cognitoSub;
    private final String email;
    private final String displayName;
    private final LocalDateTime createdAt;
}

