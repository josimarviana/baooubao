package br.app.iftmparacatu.baoounao.config;

import br.app.iftmparacatu.baoounao.domain.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    JwtTokenService jwtTokenService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configura o Message Broker
        config.enableSimpleBroker("/topic","/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Adiciona o interceptor para validar o token JWT
        registration.interceptors(new JwtChannelInterceptor(jwtTokenService));
    }
}