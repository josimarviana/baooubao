package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.domain.model.UserEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class JwtTokenService {
    private static final String SECRET_KEY="3F29F32F2LKF2889FDSFHSK";
    private static final String ISSUER = "baoounao-api";

    public String generateToken(UserEntity user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(ISSUER) //define o emissor
                    .withIssuedAt(creationDate()) //define a data de emissao
                    .withExpiresAt(expirationDate()) //define a data de expiracao
                    .withSubject(user.getUsername()) //define o assunto do token
                    .sign(algorithm); //assina o token usando o algoritmo

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

    public static class UserTokenInfo {
        private String username;
        private Integer userId;

        public UserTokenInfo(String username, Integer userId) {
            this.username = username;
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public Integer getUserId() {
            return userId;
        }
    }

    public UserTokenInfo validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            var verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            var decodedJWT = verifier.verify(token);
            String username = decodedJWT.getSubject();
            Integer userId = decodedJWT.getClaim("userId").asInt(); // Extrai o id do usuário

            return new UserTokenInfo(username, userId); //username provavelmente é o email.
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

}
