package com.bankapplicationmicroservices.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        var key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }


    @Bean
    public Converter<Jwt, Mono<JwtAuthenticationToken>> jwtAuthConverter() {
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

            String name = jwt.getClaimAsString("userId");
            if (name == null || name.isBlank()) name = jwt.getSubject();
            if (name == null || name.isBlank()) name = jwt.getClaimAsString("preferred_username");
            if (name == null || name.isBlank()) name = "user";

            return Mono.just(new JwtAuthenticationToken(jwt, authorities, name));
        };
    }

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity http,
                                           Converter<Jwt, Mono<JwtAuthenticationToken>> converter) {

        var getCustomerById = new PathPatternParserServerWebExchangeMatcher(
                "/customer-service/customers/{id}", HttpMethod.GET);


        var updateCustomer = new PathPatternParserServerWebExchangeMatcher(
                "/customer-service/customers/{id}/update", HttpMethod.PUT);

        var deleteCustomer = new PathPatternParserServerWebExchangeMatcher(
                "/customer-service/customers/{id}/delete", HttpMethod.DELETE);

        var createCustomer = new PathPatternParserServerWebExchangeMatcher(
                "/customer-service/customers/create/{id}", HttpMethod.POST);




        //---

        var getAccountsByCustomerQuery = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts", HttpMethod.GET);

        var getAccountsByCustomerQuerySlash = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/", HttpMethod.GET);

        //------------------

        var getAccountByIdWithCustomer = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/{id}", HttpMethod.GET);

        var getBalance = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/{id}/balance", HttpMethod.GET);

        var withdraw = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/{id}/withdraw", HttpMethod.PUT);

        var deposit = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/{id}/deposit", HttpMethod.PUT);

        var createAccount = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/create", HttpMethod.POST);

        var updateAccount = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/{id}/update", HttpMethod.PUT);

        var deleteAccount = new PathPatternParserServerWebExchangeMatcher(
                "/bank-service/accounts/{id}/delete", HttpMethod.DELETE);





        //-------------------


        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        http.authorizeExchange(ex -> ex
                .pathMatchers("/auth/**").permitAll()



                .matchers(getCustomerById).access(ownsByPathVar(getCustomerById, "id"))
                .matchers(updateCustomer).access(ownsByPathVar(updateCustomer, "id"))
                .matchers(deleteCustomer).access(ownsByPathVar(deleteCustomer, "id"))
                .matchers(createCustomer).access(ownsByPathVar(createCustomer,"id"))



                // .matchers(getCustomerById).access(ownsByQueryParam("customerId"))
               // .matchers(updateCustomer).access(ownsByQueryParam("customerId"))
               // .matchers(deleteCustomer).access(ownsByQueryParam("customerId"))
               // .matchers(createCustomer).access(ownsByQueryParam("customerId"))

                .pathMatchers(HttpMethod.GET, "/customer-service/customers/all").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST,   "/customer-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT,    "/customer-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/customer-service/**").hasRole("ADMIN")
                .pathMatchers("/customer-service/**").authenticated()


                //-------------------

                .matchers(getAccountsByCustomerQuery).access(ownsByQueryParam("customerId"))
                .matchers(getAccountsByCustomerQuerySlash).access(ownsByQueryParam("customerId"))
                .matchers(getAccountByIdWithCustomer).access(ownsByQueryParam("customerId"))
                .matchers(getBalance).access(ownsByQueryParam("customerId"))
                .matchers(withdraw).access(ownsByQueryParam("customerId"))
                .matchers(deposit).access(ownsByQueryParam("customerId"))
                .matchers(createAccount).access(ownsByQueryParam("customerId"))
                .matchers(updateAccount).access(ownsByQueryParam("customerId"))
                .matchers(deleteAccount).access(ownsByQueryParam("customerId"))




                //--------------------
                .pathMatchers(HttpMethod.GET, "/bank-service/accounts/all").hasRole("ADMIN")
                .matchers(getAccountsByCustomerQuery).access(ownsByQueryParam("customerId"))
                .matchers(getAccountsByCustomerQuerySlash).access(ownsByQueryParam("customerId")) // <— ADD THIS

                .matchers(new PathPatternParserServerWebExchangeMatcher(
                        "/bank-service/accounts/{id}", HttpMethod.GET))
                .access(ownsByQueryParam("customerId"))

                .pathMatchers(HttpMethod.GET, "/bank-service/accounts/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST,   "/bank-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT,    "/bank-service/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/bank-service/**").hasRole("ADMIN")
                .pathMatchers("/bank-service/**").authenticated()



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

    private ReactiveAuthorizationManager<AuthorizationContext> ownsByPathVar(
            ServerWebExchangeMatcher matcher, String varName) {

        return (authentication, context) ->
                matcher.matches(context.getExchange())
                        .flatMap(match -> {
                            if (!match.isMatch()) return Mono.just(new AuthorizationDecision(false));
                            String requestedId = Objects.toString(match.getVariables().get(varName), null);
                            return authentication
                                    .map(auth -> new AuthorizationDecision(isAdmin(auth) || idsEqual(auth, requestedId)))
                                    .defaultIfEmpty(new AuthorizationDecision(false));
                        });
    }

    private ReactiveAuthorizationManager<AuthorizationContext> ownsByQueryParam(String param) {
        return (authentication, context) -> {
            String requestedId = context.getExchange().getRequest().getQueryParams().getFirst(param);
            return authentication.map(auth -> {
                if (isAdmin(auth)) return new AuthorizationDecision(true);
                return new AuthorizationDecision(requestedId != null && idsEqual(auth, requestedId));
            }).defaultIfEmpty(new AuthorizationDecision(false));
        };
    }


    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private boolean idsEqual(Authentication auth, String requestedId) {
        if (!(auth instanceof JwtAuthenticationToken token)) return false;
        if (requestedId == null) return false;
        String userId = token.getToken().getClaimAsString("userId");
        if (userId == null || userId.isBlank()) userId = token.getName();
        return requestedId.equals(userId);
    }
}
