package dev.zenith.finance.zenith_backend.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CognitoJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        // Use 'sub' claim as principal name (Cognito user ID)
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Cognito stores groups in "cognito:groups" claim
        Object groupsClaim = jwt.getClaim("cognito:groups");
        if (groupsClaim instanceof List<?> groups) {
            return groups.stream()
                    .map(g -> new SimpleGrantedAuthority("ROLE_" + g.toString().toUpperCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

