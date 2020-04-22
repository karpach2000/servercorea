package com.parcel.tools.web.websockets.config;


import com.parcel.tools.web.websockets.SpyWebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements  WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SpyWebSocketController(), "/games/spy").setAllowedOrigins("*");
    }
}
