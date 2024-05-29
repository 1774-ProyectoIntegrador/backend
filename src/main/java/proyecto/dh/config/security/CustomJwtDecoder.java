package proyecto.dh.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomJwtDecoder implements JwtDecoder {

    private final JWTVerifier jwtVerifier;

    public CustomJwtDecoder(SecretKey secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getEncoded());
        this.jwtVerifier = JWT.require(algorithm).build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            Map<String, Object> headers = new HashMap<>();
            headers.put("alg", decodedJWT.getAlgorithm());
            headers.put("typ", decodedJWT.getType());

            Map<String, Object> claims = new HashMap<>();
            decodedJWT.getClaims().forEach((k, v) -> claims.put(k, v.as(Object.class)));

            return new Jwt(token, decodedJWT.getIssuedAt().toInstant(), decodedJWT.getExpiresAt().toInstant(), headers, claims);
        } catch (JwtException e) {
            throw new JwtException("Token inválido o expirado");
        }
    }
}
