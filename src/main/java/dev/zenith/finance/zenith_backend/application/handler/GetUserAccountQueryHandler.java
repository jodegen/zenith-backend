package dev.zenith.finance.zenith_backend.application.handler;

import dev.zenith.finance.zenith_backend.application.dto.UserAccountDto;
import dev.zenith.finance.zenith_backend.application.query.GetUserAccountQuery;
import dev.zenith.finance.zenith_backend.application.query.QueryHandler;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserAccountQueryHandler implements QueryHandler<GetUserAccountQuery, UserAccountDto> {

    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserAccountDto handle(GetUserAccountQuery query) {
        return userAccountRepository.findByCognitoSub(query.cognitoSub())
                .map(u -> new UserAccountDto(u.getId(), u.getEmail(), u.getDisplayName(), u.getCreatedAt()))
                .orElseThrow(() -> new UserAccountNotFoundException(query.cognitoSub()));
    }
}

