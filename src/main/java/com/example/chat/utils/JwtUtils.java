package com.example.chat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-live}")
    private String accessLive;

    @Value("${jwt.refresh-live}")
    private String refreshLive;

    private final TimeUtils timeUtils;

    public UUID getSubject(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);
        return UUID.fromString(decodedJWT.getSubject());
    }

    public String generateAccessToken(UUID sub) throws JWTCreationException {
        return generateToken(sub, timeUtils.parseString(accessLive));
    }

    public String generateRefreshToken(UUID sub) throws JWTCreationException {
        return generateToken(sub, timeUtils.parseString(refreshLive));
    }

    private String generateToken(UUID sub, Instant expiresAt) throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer(issuer)
                .withExpiresAt(expiresAt)
                .withSubject(sub.toString())
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }
}
