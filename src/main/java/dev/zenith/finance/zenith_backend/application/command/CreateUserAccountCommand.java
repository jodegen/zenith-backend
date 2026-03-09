package dev.zenith.finance.zenith_backend.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserAccountCommand(
        @NotBlank String cognitoSub,
        @NotBlank @Email String email,
        String displayName
) {}

