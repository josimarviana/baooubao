package br.app.iftmparacatu.baoounao.config;

import br.app.iftmparacatu.baoounao.domain.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

public class JwtChannelInterceptor implements ChannelInterceptor {

    JwtTokenService jwtTokenService;

    @Autowired
    public JwtChannelInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = accessor.getFirstNativeHeader("Authorization");

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            String token = jwtToken.substring(7);
            // Verifica o token JWT e extrai o usuário
            String username = jwtTokenService.getSubjectFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Cria a autenticação com base no token
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        return message;
    }
}