package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.command.CommandHandler;
import dev.zenith.finance.zenith_backend.application.command.CreateExpenseCommand;
import dev.zenith.finance.zenith_backend.application.dto.ExpenseDto;
import dev.zenith.finance.zenith_backend.domain.exception.CategoryNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.model.Category;
import dev.zenith.finance.zenith_backend.domain.model.Expense;
import dev.zenith.finance.zenith_backend.domain.model.ExpenseStatus;
import dev.zenith.finance.zenith_backend.domain.model.Money;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.CategoryRepository;
import dev.zenith.finance.zenith_backend.domain.repository.ExpenseRepository;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateExpenseCommandHandler implements CommandHandler<CreateExpenseCommand, ExpenseDto> {

    private final UserAccountRepository userAccountRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public ExpenseDto handle(CreateExpenseCommand command) {
        UserAccount owner = userAccountRepository.findByCognitoSub(command.cognitoSub())
                .orElseThrow(() -> new UserAccountNotFoundException(command.cognitoSub()));

        Category category = categoryRepository.findByIdAndOwnerId(command.categoryId(), owner.getId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        Expense expense = Expense.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .category(category)
                .amount(Money.of(command.amount(), command.currency()))
                .description(command.description())
                .date(command.date())
                .status(ExpenseStatus.ACTIVE)
                .build();

        Expense saved = expenseRepository.save(expense);
        return toDto(saved);
    }

    static ExpenseDto toDto(Expense e) {
        return new ExpenseDto(
                e.getId(),
                e.getCategory().getId(),
                e.getCategory().getName(),
                e.getAmount().amount(),
                e.getAmount().currency().getCurrencyCode(),
                e.getDescription(),
                e.getDate()
        );
    }
}

