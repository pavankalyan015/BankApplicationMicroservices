package com.bankapplicationmicroservices.api_gateway.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-seconds}")
    private long expirationSeconds;

    public String generateToken(Long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(expirationSeconds)))
                .claim("userId", String.valueOf(userId))   // <-- ownership claim
                .claim("roles", roles)
                .build();
        try {
            var header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
            var signed = new SignedJWT(header, claims);
            signed.sign(new MACSigner(secret.getBytes()));
            return signed.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Cannot sign JWT", e);
        }
    }

    public long getExpiresIn() { return expirationSeconds; }
}
