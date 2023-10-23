package fr.konoashi.face.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class ServerWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/outToServer");//From the client
        registry.addHandler(webSocketHandler2(), "/inToClient");//In from Talos to go to the client back
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new GoToServerWS();
    }

    @Bean
    public WebSocketHandler webSocketHandler2() {
        return new BackToClientWS();
    }
}
