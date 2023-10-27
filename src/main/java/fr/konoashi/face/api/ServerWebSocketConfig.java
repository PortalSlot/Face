package fr.konoashi.face.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class ServerWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/outToTalos");//
        registry.addHandler(webSocketHandler2(), "/inToClient");//In from Talos to go to the client back
        registry.addHandler(webSocketHandler3(), "/newPlayerLoggedInFace");//From the client
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new GoToTalosWS();
    }

    @Bean
    public WebSocketHandler webSocketHandler2() {
        return new BackToClientWS();
    }

    @Bean
    public WebSocketHandler webSocketHandler3() {
        return new NewPlayerLoggedInFace();
    }
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(1024);
        container.setMaxBinaryMessageBufferSize(1024*1024*10);
        return container;
    }
}
