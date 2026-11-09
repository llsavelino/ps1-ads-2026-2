package br.edu.fatecfranca.api.services.policy.secure.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;


import br.edu.fatecfranca.api.entities.Users;


@Service
public class TokenJwt {

    private final Algorithm algorithm;
    public TokenJwt(@Value("${jwt.secret}") String secret) { this.algorithm = Algorithm.HMAC256(secret); }


    public String generate(Users user) {

        Instant now = Instant.now();
        return JWT.create()
                .withIssuer("fatecfranca-api")
                .withSubject(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(now.plus(24, ChronoUnit.HOURS))
                .sign(algorithm);
    }
}

