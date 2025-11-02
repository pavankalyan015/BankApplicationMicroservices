package com.bankapplicationmicroservices.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Map "roles" claim -> ROLE_* authorities */
    @Bean
    public org.springframework.core.convert.converter.Converter<Jwt, Mono<JwtAuthenticationToken>> jwtAuthConverter() {
        return (Jwt jwt) -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null) {
                Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                if (realmAccess != null) {
                    Object ra = realmAccess.get("roles");
                    if (ra instanceof Collection<?> c) {
                        roles = c.stream().filter(String.class::isInstance).map(String.class::cast).toList();
                    }
                }
            }
            if (roles == null) roles = List.of();
            var authorities = roles.stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            return Mono.just(new JwtAuthenticationToken(jwt, authorities, jwt.getSubject()));
        };
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity http,
                                           org.springframework.core.convert.converter.Converter<Jwt, Mono<JwtAuthenticationToken>> converter) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        http.authorizeExchange(ex -> ex
                // public endpoints for auth
                .pathMatchers("/auth/**").permitAll()
                // === CUSTOMER-SERVICE
                .pathMatchers(HttpMethod.GET, "/customer-service/customers/all").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/customer-service/customers/**").hasAnyRole("USER","ADMIN")
                .pathMatchers(HttpMethod.POST,   "/customer-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT,    "/customer-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/customer-service/**").hasRole("ADMIN")
                .pathMatchers("/customer-service/**").authenticated()

                // === BANK-SERVICE  (FIXED: remove extra '/bank' segment)
                .pathMatchers(HttpMethod.GET, "/bank-service/accounts/all").hasRole("ADMIN")          // admin-only list
                .pathMatchers(HttpMethod.GET, "/bank-service/accounts/**").hasAnyRole("USER","ADMIN") // other GETs
                .pathMatchers(HttpMethod.POST,   "/bank-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT,    "/bank-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/bank-service/**").hasRole("ADMIN")
                .pathMatchers("/bank-service/**").authenticated()

                // === TRANSACTION-SERVICE
                .pathMatchers(HttpMethod.GET, "/transaction-service/trans/all").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/transaction-service/trans/**").hasAnyRole("USER","ADMIN")
                .pathMatchers(HttpMethod.POST,   "/transaction-service/**").hasAnyRole("USER","ADMIN")
                .pathMatchers(HttpMethod.PUT,    "/transaction-service/**").hasAnyRole("USER","ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/transaction-service/**").hasRole("ADMIN")
                .pathMatchers("/transaction-service/**").authenticated()


                .anyExchange().authenticated()
        );

        http.oauth2ResourceServer(oauth -> oauth.jwt(j -> j.jwtAuthenticationConverter(converter)));
        return http.build();
    }
}
