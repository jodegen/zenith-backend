package dev.zenith.finance.zenith_backend.infrastructure.security;

import dev.zenith.finance.zenith_backend.application.command.CreateUserAccountCommand;
import dev.zenith.finance.zenith_backend.application.handler.CreateUserAccountCommandHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CognitoUserSyncFilter extends OncePerRequestFilter {

    private final CreateUserAccountCommandHandler createUserAccountCommandHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String cognitoSub = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");

            try {
                createUserAccountCommandHandler.handle(
                        new CreateUserAccountCommand(cognitoSub, email, name));
            } catch (Exception e) {
                log.warn("Could not sync user account for sub={}: {}", cognitoSub, e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}



