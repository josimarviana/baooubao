package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.domain.dtos.output.UserTokenInfo;
import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.issuer}")
    private String ISSUER;

    public String generateToken(UserEntity user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            List<String> roles = user.getAuthorities().stream()
                                     .map(GrantedAuthority::getAuthority)
                                     .collect(Collectors.toList());
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate())
                    .withSubject(user.getName())
                    .withClaim("roles",roles)
                    .sign(algorithm);

        }catch (JWTCreationException exception){
            throw new JWTCreationException("Erro ao gerar token", exception);
        }
    }

    public String getSubjectFromToken(String token){

        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER) //define o emissor
                    .build()
                    .verify(token)//verifica a validade
                    .getSubject(); //obtem o assunto

        }catch (JWTCreationException exception){
            throw new JWTCreationException("Erro ao gerar token", exception);
        }
    }

    private Instant creationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    private Instant expirationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(4).toInstant();
    }

    public UserTokenInfo validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            var verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            var decodedJWT = verifier.verify(token);
            String username = decodedJWT.getSubject();

            return new UserTokenInfo(username);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

}
