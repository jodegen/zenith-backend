package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.dto.ExpenseDto;
import dev.zenith.finance.zenith_backend.application.dto.PagedResponse;
import dev.zenith.finance.zenith_backend.application.query.GetExpensesByUserQuery;
import dev.zenith.finance.zenith_backend.application.query.QueryHandler;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.model.Expense;
import dev.zenith.finance.zenith_backend.domain.model.ExpenseStatus;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.ExpenseRepository;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetExpensesByUserQueryHandler implements QueryHandler<GetExpensesByUserQuery, PagedResponse<ExpenseDto>> {

    private final UserAccountRepository userAccountRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseDto> handle(GetExpensesByUserQuery query) {
        UserAccount owner = userAccountRepository.findByCognitoSub(query.cognitoSub())
                .orElseThrow(() -> new UserAccountNotFoundException(query.cognitoSub()));

        PageRequest pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "date"));

        Page<Expense> page;
        if (query.categoryId() != null) {
            page = expenseRepository.findAllByOwnerIdAndCategoryIdAndStatus(
                    owner.getId(), query.categoryId(), ExpenseStatus.ACTIVE, pageable);
        } else if (query.from() != null && query.to() != null) {
            page = expenseRepository.findAllByOwnerIdAndStatusAndDateBetween(
                    owner.getId(), ExpenseStatus.ACTIVE, query.from(), query.to(), pageable);
        } else {
            page = expenseRepository.findAllByOwnerIdAndStatus(owner.getId(), ExpenseStatus.ACTIVE, pageable);
        }

        List<ExpenseDto> content = page.getContent().stream()
                .map(CreateExpenseCommandHandler::toDto)
                .toList();

        return new PagedResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}

