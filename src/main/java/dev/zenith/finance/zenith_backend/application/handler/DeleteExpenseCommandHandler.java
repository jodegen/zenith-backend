package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.command.CommandHandler;
import dev.zenith.finance.zenith_backend.application.command.DeleteExpenseCommand;
import dev.zenith.finance.zenith_backend.domain.exception.ExpenseNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.model.Expense;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.ExpenseRepository;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteExpenseCommandHandler implements CommandHandler<DeleteExpenseCommand, Void> {

    private final UserAccountRepository userAccountRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public Void handle(DeleteExpenseCommand command) {
        UserAccount owner = userAccountRepository.findByCognitoSub(command.cognitoSub())
                .orElseThrow(() -> new UserAccountNotFoundException(command.cognitoSub()));

        Expense expense = expenseRepository.findByIdAndOwnerId(command.expenseId(), owner.getId())
                .orElseThrow(() -> new ExpenseNotFoundException(command.expenseId()));

        expense.delete();
        expenseRepository.save(expense);
        return null;
    }
}

