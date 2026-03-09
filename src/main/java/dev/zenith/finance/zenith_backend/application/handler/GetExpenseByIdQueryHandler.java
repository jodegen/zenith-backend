package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.dto.ExpenseDto;
import dev.zenith.finance.zenith_backend.application.query.GetExpenseByIdQuery;
import dev.zenith.finance.zenith_backend.application.query.QueryHandler;
import dev.zenith.finance.zenith_backend.domain.exception.ExpenseNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.ExpenseRepository;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetExpenseByIdQueryHandler implements QueryHandler<GetExpenseByIdQuery, ExpenseDto> {

    private final UserAccountRepository userAccountRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public ExpenseDto handle(GetExpenseByIdQuery query) {
        UserAccount owner = userAccountRepository.findByCognitoSub(query.cognitoSub())
                .orElseThrow(() -> new UserAccountNotFoundException(query.cognitoSub()));

        return expenseRepository.findByIdAndOwnerId(query.expenseId(), owner.getId())
                .map(CreateExpenseCommandHandler::toDto)
                .orElseThrow(() -> new ExpenseNotFoundException(query.expenseId()));
    }
}

