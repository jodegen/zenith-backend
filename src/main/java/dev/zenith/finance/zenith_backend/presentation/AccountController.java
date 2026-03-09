package dev.zenith.finance.zenith_backend.presentation;

import dev.zenith.finance.zenith_backend.application.handler.GetUserAccountQueryHandler;
import dev.zenith.finance.zenith_backend.application.query.GetUserAccountQuery;
import dev.zenith.finance.zenith_backend.presentation.assembler.UserAccountModelAssembler;
import dev.zenith.finance.zenith_backend.presentation.model.UserAccountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "User account endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final GetUserAccountQueryHandler getUserAccountQueryHandler;
    private final UserAccountModelAssembler userAccountModelAssembler;

    @GetMapping(value = "/me", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "getMe", summary = "Get the current user's account")
    public ResponseEntity<EntityModel<UserAccountResponse>> getMe() {
        var dto = getUserAccountQueryHandler.handle(new GetUserAccountQuery(cognitoSub()));
        var response = new UserAccountResponse()
                .id(dto.id())
                .email(dto.email())
                .displayName(dto.displayName())
                .createdAt(dto.createdAt().atOffset(ZoneOffset.UTC));
        return ResponseEntity.ok(userAccountModelAssembler.toModel(response));
    }

    private String cognitoSub() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT found in SecurityContext");
        }
        return jwt.getSubject();
    }
}
