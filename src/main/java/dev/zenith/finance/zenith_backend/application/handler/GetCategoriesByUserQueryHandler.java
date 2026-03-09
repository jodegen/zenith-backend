package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.dto.CategoryDto;
import dev.zenith.finance.zenith_backend.application.query.GetCategoriesByUserQuery;
import dev.zenith.finance.zenith_backend.application.query.QueryHandler;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.model.UserAccount;
import dev.zenith.finance.zenith_backend.domain.repository.CategoryRepository;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCategoriesByUserQueryHandler implements QueryHandler<GetCategoriesByUserQuery, List<CategoryDto>> {

    private final UserAccountRepository userAccountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> handle(GetCategoriesByUserQuery query) {
        UserAccount owner = userAccountRepository.findByCognitoSub(query.cognitoSub())
                .orElseThrow(() -> new UserAccountNotFoundException(query.cognitoSub()));

        return categoryRepository.findAllByOwnerId(owner.getId()).stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getType(), c.isDefaultCategory()))
                .toList();
    }
}

