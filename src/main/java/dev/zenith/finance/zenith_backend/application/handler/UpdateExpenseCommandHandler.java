package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.command.CommandHandler;
import dev.zenith.finance.zenith_backend.application.command.UpdateExpenseCommand;
import dev.zenith.finance.zenith_backend.application.dto.ExpenseDto;
import dev.zenith.finance.zenith_backend.domain.exception.CategoryNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.ExpenseNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.model.Category;
import dev.zenith.finance.zenith_backend.domain.model.Expense;
import dev.zenith.finance.zenith_backend.domain.model.Money;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.CategoryRepository;
import dev.zenith.finance.zenith_backend.domain.repository.ExpenseRepository;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateExpenseCommandHandler implements CommandHandler<UpdateExpenseCommand, ExpenseDto> {

    private final UserAccountRepository userAccountRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public ExpenseDto handle(UpdateExpenseCommand command) {
        UserAccount owner = userAccountRepository.findByCognitoSub(command.cognitoSub())
                .orElseThrow(() -> new UserAccountNotFoundException(command.cognitoSub()));

        Expense existing = expenseRepository.findByIdAndOwnerId(command.expenseId(), owner.getId())
                .orElseThrow(() -> new ExpenseNotFoundException(command.expenseId()));

        Category category = existing.getCategory();
        if (command.categoryId() != null) {
            category = categoryRepository.findByIdAndOwnerId(command.categoryId(), owner.getId())
                    .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        }

        Money amount = existing.getAmount();
        if (command.amount() != null && command.currency() != null) {
            amount = Money.of(command.amount(), command.currency());
        } else if (command.amount() != null) {
            amount = Money.of(command.amount(), existing.getAmount().currency().getCurrencyCode());
        }

        Expense updated = Expense.builder()
                .id(existing.getId())
                .owner(owner)
                .category(category)
                .amount(amount)
                .description(command.description() != null ? command.description() : existing.getDescription())
                .date(command.date() != null ? command.date() : existing.getDate())
                .status(existing.getStatus())
                .build();

        Expense saved = expenseRepository.save(updated);
        return CreateExpenseCommandHandler.toDto(saved);
    }
}

