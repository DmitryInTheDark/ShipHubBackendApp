package ru.ship.ShipHub.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    private final String secret;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String generateToken(Long id, String username, String role){
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
        return JWT.create()
                .withExpiresAt(expirationDate)
                .withSubject(id.toString())
                .withClaim("username", username)
                .withClaim("role", role)
                .sign(Algorithm.HMAC256(secret));
    }

    public Long validateTokenAndGetPersonId(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT jwt = verifier.verify(token);
        return Long.parseLong(jwt.getSubject());
    }
}
