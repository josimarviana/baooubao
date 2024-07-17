package br.app.iftmparacatu.baoounao.domain.services;

import br.app.iftmparacatu.baoounao.domain.security.UserDetailsImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class JwtTokenService {
    private static final String SECRET_KEY="3F29F32F2LKF2889FDSFHSK";
    private static final String ISSUER = "baoounao-api";

    public String generateToken(UserDetailsImpl user){
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

}
