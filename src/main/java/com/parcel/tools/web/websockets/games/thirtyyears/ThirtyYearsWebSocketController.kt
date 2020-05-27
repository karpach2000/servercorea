package com.parcel.tools.web.websockets.games.thirtyyears

import com.parcel.tools.games.games.mafia.MafiaSessionManager
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsEvent
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsSessionManager
import com.parcel.tools.web.websockets.games.thirtyyears.json.ThirtyYearsMessage
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


//логгер
private val logger = org.apache.log4j.Logger.getLogger(ThirtyYearsWebSocketController::class.java!!)

@Component
class ThirtyYearsWebSocketController : TextWebSocketHandler() {

    class ThirtyYearsEventHandler(private var session: WebSocketSession, private var name: String):
            ThirtyYearsEvent
    {

        /**
         * Имя пользователяя которому должен прилететь Event.
         */
        override var userName : String = ""

        override fun ENTER_REAL_EXCUTE_event() {
            TODO("Not yet implemented")
        }

        override fun ENTER_FALSH_EXCUTE_event() {
            TODO("Not yet implemented")
        }

        override fun VOTE_event() {
            TODO("Not yet implemented")
        }

        override fun SHOW_FINAL_RESULTS_event(table: String) {
            TODO("Not yet implemented")
        }

        override fun SHOW_RESULTS_event(table: String) {
            TODO("Not yet implemented")
        }

        override fun addUserEvent(userList: String) {
            TODO("Not yet implemented")
        }

        override fun startGameEvent() {
            TODO("Not yet implemented")
        }

        override fun stopGameEvent(spyName: String) {
            TODO("Not yet implemented")
        }
    }

    @Override
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("Web socket connection closed.")
        super.afterConnectionClosed(session, status)
    }
    @Override
    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Web socket connected")
        super.afterConnectionEstablished(session)
    }

    @Override
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {

        val rx = message.payload
        logger.info("RX :${rx} ")
        val inMessage = ThirtyYearsMessage(rx)
        if(inMessage.command == Commands.CONNECT.toString())
        {
            val id= inMessage.sessionId
            val pas = inMessage.sessionPas
            val name = inMessage.userName
            logger.info("Web socket connection subscribe. id: $id, pas: $pas name: $name")
            val thirtyYearsEventHandler = ThirtyYearsEventHandler(session, name)
            thirtyYearsEventHandler.userName = name
           ThirtyYearsSessionManager.subscribeGameSessionEvent(id, pas, thirtyYearsEventHandler)
        }
        else if(inMessage.command == Commands.ADD_USER.toString())
        {
            logger.info("TX:pong")
            session.sendMessage(TextMessage("pong"))
        }
        super.handleTextMessage(session, message)
    }





}