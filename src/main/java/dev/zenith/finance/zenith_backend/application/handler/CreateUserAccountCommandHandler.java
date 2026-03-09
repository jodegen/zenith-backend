package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.command.CommandHandler;
import dev.zenith.finance.zenith_backend.application.command.CreateUserAccountCommand;
import dev.zenith.finance.zenith_backend.application.dto.UserAccountDto;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserAccountCommandHandler implements CommandHandler<CreateUserAccountCommand, UserAccountDto> {

    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public UserAccountDto handle(CreateUserAccountCommand command) {
        if (userAccountRepository.existsByCognitoSub(command.cognitoSub())) {
            return userAccountRepository.findByCognitoSub(command.cognitoSub())
                    .map(u -> new UserAccountDto(u.getId(), u.getEmail(), u.getDisplayName(), u.getCreatedAt()))
                    .orElseThrow();
        }

        UserAccount account = UserAccount.builder()
                .id(UUID.randomUUID())
                .cognitoSub(command.cognitoSub())
                .email(command.email())
                .displayName(command.displayName() != null ? command.displayName() : command.email())
                .createdAt(LocalDateTime.now())
                .build();

        UserAccount saved = userAccountRepository.save(account);
        return new UserAccountDto(saved.getId(), saved.getEmail(), saved.getDisplayName(), saved.getCreatedAt());
    }
}

