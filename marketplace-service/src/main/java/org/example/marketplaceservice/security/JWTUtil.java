package org.example.marketplaceservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.models.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(JWTDTO jwtdto){
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());

        return JWT.create().withSubject("User details")
                .withClaim("id",jwtdto.getId())
                .withClaim("name",jwtdto.getLogin())
                .withClaim("role",jwtdto.getRole())
                .withIssuedAt(new Date())
                .withIssuer("Kanayro")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public JWTDTO validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("Kanayro")
                .build();

        DecodedJWT jwt = verifier.verify(token);

        JWTDTO jwtdto = new JWTDTO();
        jwtdto.setId(jwt.getClaim("id").asInt());
        jwtdto.setLogin(jwt.getClaim("name").asString());
        jwtdto.setRole(jwt.getClaim("role").asString());

        return jwtdto;
    }

    public String getJWT(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }else{
            return null;
        }
    }
}
