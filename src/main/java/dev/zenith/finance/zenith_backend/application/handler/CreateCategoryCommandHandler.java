package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.command.CommandHandler;
import dev.zenith.finance.zenith_backend.application.command.CreateCategoryCommand;
import dev.zenith.finance.zenith_backend.application.dto.CategoryDto;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.model.Category;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.CategoryRepository;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCategoryCommandHandler implements CommandHandler<CreateCategoryCommand, CategoryDto> {

    private final UserAccountRepository userAccountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto handle(CreateCategoryCommand command) {
        UserAccount owner = userAccountRepository.findByCognitoSub(command.cognitoSub())
                .orElseThrow(() -> new UserAccountNotFoundException(command.cognitoSub()));

        Category category = Category.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .name(command.name())
                .type(command.type())
                .defaultCategory(false)
                .build();

        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName(), saved.getType(), saved.isDefaultCategory());
    }
}

