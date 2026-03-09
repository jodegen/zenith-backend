package dev.zenith.finance.zenith_backend.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAccountDto(
        UUID id,
        String email,
        String displayName,
        LocalDateTime createdAt
) {}

