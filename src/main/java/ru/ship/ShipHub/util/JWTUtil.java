package ru.ship.ShipHub.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;

@Component
public class JWTUtil {

    private final String secret;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String generateToken(Integer id, String username){
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());

        return JWT.create()
                .withExpiresAt(expirationDate)
                .withSubject(id.toString())
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(secret));
    }

    public Integer validateTokenAndGetPersonId(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT jwt = verifier.verify(token);
        return Integer.parseInt(jwt.getSubject());
    }
}
