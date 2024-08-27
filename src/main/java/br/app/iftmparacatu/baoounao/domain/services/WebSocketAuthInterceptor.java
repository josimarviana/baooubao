package br.app.iftmparacatu.baoounao.domain.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    @Value("${jwt.secret}")
    private String SECRET_KEY = "3F29F32F2LKF2889FDSFHSK"; // Defina a sua chave secreta

    @Value("${jwt.issuer}")
    private String ISSUER = "baoounao-api";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = getTokenFromRequest(request);
        if (token != null) {
            // Validar o token JWT e extrair o ID do usuário
            String userId = validateAndExtractUserId(token);
            if (userId != null) {
                attributes.put("userId", userId);
                System.out.println("UserId extracted and added to attributes: " + userId);
                return true;
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        // Extraia o token dos parâmetros da consulta
        String query = request.getURI().getQuery();
        if (query != null) {
            return Arrays.stream(query.split("&"))
                    .filter(param -> param.startsWith("token="))
                    .map(param -> param.substring("token=".length()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private String validateAndExtractUserId(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            var verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            var decodedJWT = verifier.verify(token);
            String username = decodedJWT.getSubject();

            return username;
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }



    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Não é necessário implementar
    }
}