package br.app.iftmparacatu.baoounao.config;

import br.app.iftmparacatu.baoounao.domain.services.NotificationWebSocketHandler;
import br.app.iftmparacatu.baoounao.domain.services.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new NotificationWebSocketHandler(), "/ws/notifications")
                .addInterceptors(new WebSocketAuthInterceptor())
                .setAllowedOrigins("*");
    }
}
