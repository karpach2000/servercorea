package com.parcel.tools.web.websockets.config;


import com.parcel.tools.web.websockets.games.CardsWebSocketController;
import com.parcel.tools.web.websockets.games.MafiaWebSocketController;
import com.parcel.tools.web.websockets.games.SpyWebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements  WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SpyWebSocketController(), "/games/spy/ws").setAllowedOrigins("*");
        registry.addHandler(new MafiaWebSocketController(), "/games/mafia/ws").setAllowedOrigins("*");
        registry.addHandler(new CardsWebSocketController(), "/games/cards/ws").setAllowedOrigins("*");//ThirtyYearsWebSocketController
    }
}
