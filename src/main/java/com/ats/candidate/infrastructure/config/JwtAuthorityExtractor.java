package com.ats.candidate.infrastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class JwtAuthorityExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final List<String> CLAIM_NAMES = List.of("permissions", "authorities", "roles");

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        return CLAIM_NAMES.stream()
                .flatMap(claim -> extractClaim(jwt, claim))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Stream<String> extractClaim(Jwt jwt, String claimName) {
        Object claim = jwt.getClaims().get(claimName);
        if (claim == null) return Stream.empty();

        if (claim instanceof Collection<?> collection) {
            List<String> result = new ArrayList<>();
            for (Object item : collection) {
                if (item != null) result.add(item.toString());
            }
            return result.stream();
        }
        if (claim instanceof String str) {
            return Stream.of(str);
        }
        return Stream.empty();
    }
}
