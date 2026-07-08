package com.ats.candidate.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/candidates", "/api/candidates/*")
                        .hasAnyAuthority("RECRUITER_READ", "CANDIDATE_READ", "ROLE_ADMIN", "ROLE_RECRUITER")
                        .requestMatchers(HttpMethod.GET, "/api/candidates/*/attachments")
                        .hasAnyAuthority("RECRUITER_READ", "CANDIDATE_READ", "ROLE_ADMIN", "ROLE_RECRUITER")
                        .requestMatchers(HttpMethod.POST, "/api/candidates", "/api/candidates/*/attachments")
                        .hasAnyAuthority("RECRUITER_WRITE", "CANDIDATE_CREATE", "ROLE_ADMIN", "ROLE_RECRUITER")
                        .requestMatchers(HttpMethod.PUT, "/api/candidates/*")
                        .hasAnyAuthority("RECRUITER_WRITE", "CANDIDATE_UPDATE", "ROLE_ADMIN", "ROLE_RECRUITER")
                        .requestMatchers(HttpMethod.PATCH, "/api/candidates/*/status")
                        .hasAnyAuthority("RECRUITER_WRITE", "CANDIDATE_UPDATE", "ROLE_ADMIN", "ROLE_RECRUITER")
                        .requestMatchers(HttpMethod.DELETE, "/api/candidates/*")
                        .hasAnyAuthority("RECRUITER_WRITE", "CANDIDATE_DELETE", "ROLE_ADMIN", "ROLE_RECRUITER")
                        .anyRequest()
                        .permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> new JwtAuthenticationToken(jwt, extractAuthorities(jwt));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        addAuthorities(authorities, jwt.getClaim("permissions"));
        addAuthorities(authorities, jwt.getClaim("authorities"));
        addAuthorities(authorities, jwt.getClaim("roles"));
        return authorities;
    }

    private void addAuthorities(List<GrantedAuthority> authorities, Object claimValue) {
        if (claimValue instanceof Collection<?> values) {
            values.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
    }
}
