package com.parcel.tools.web.websockets.games.thirtyyears

import com.parcel.tools.games.games.thirtyyears.ThirtyYearsEvent
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsSessionManager
import com.parcel.tools.web.websockets.games.thirtyyears.json.ThirtyYearsMessage
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import javax.print.attribute.standard.JobOriginatingUserName


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

        var sessionId = 0L
        var sessionPas = 0L

        /**
         * Событие перевода в статус ввведения реальной отмазки.
         */
        override fun ENTER_REAL_EXCUTE_event(event: String) {
            sendMessage(Commands.ENTER_REAL_EXCUTE_EVENT, event)
        }
        /**
         * Событие перевода в статус фальшивой отмазки.
         */
        override fun ENTER_FALSH_EXCUTE_event(event: String) {
            sendMessage(Commands.ENTER_FALSH_EXCUTE_EVENT, event)
        }
        /**
         * Событие перевода в статус голосования.
         */
        override fun VOTE_event(enable: Boolean) {
            sendMessage(Commands.VOTE_EVENT, enable.toString())
        }
        /**
         * Событие Показываения пользователю результаты всей игры.
         */
        override fun SHOW_FINAL_RESULTS_event(table: String) {
            sendMessage(Commands.SHOW_FINAL_RESULTS_EVENT, table)
        }
        /**
         * События демонстрации результатов голосования пользователям.
         */
        override fun SHOW_RESULTS_event(table: String) {
            sendMessage(Commands.SHOW_RESULTS_EVENT, table)
        }

        override fun addUserEvent(userList: String) {
            sendMessage(Commands.ADD_USER)
        }

        override fun startGameEvent() {
            sendMessage(Commands.START_GAME)
        }

        override fun stopGameEvent(spyName: String) {
            sendMessage(Commands.STOP_GAME)
        }


        private fun sendMessage(command: Commands, data: String= "")
        {
            val message = ThirtyYearsMessage(sessionId, sessionPas, userName)
            message.command = command
            message.data = data
            sendMessage(message.toJson())
        }//fun

        private fun sendMessage(message: String)
        {
            try {
                logger.info("TX ($name):$message")
                session.sendMessage(TextMessage(message))
            }
            catch (ex: java.lang.IllegalStateException)
            {
                logger.error("Message send error:\n${ex.message} ")
            }
        }//fun
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
        if(inMessage.command==Commands.PING)
        {
            sendMessage(session, Commands.PONG)
        }
        else if(inMessage.command == Commands.CONNECT)
        {
            val id= inMessage.sessionId
            val pas = inMessage.sessionPas
            val name = inMessage.userName
            logger.info("Web socket connection subscribe. id: $id, pas: $pas name: $name")
            val thirtyYearsEventHandler = ThirtyYearsEventHandler(session, name)
            thirtyYearsEventHandler.userName = name
            thirtyYearsEventHandler.sessionPas = pas
            thirtyYearsEventHandler.sessionId = id
           ThirtyYearsSessionManager.subscribeGameSessionEvent(id, pas, thirtyYearsEventHandler)
            sendMessage(session, Commands.CONNECT)
        }
        else if(inMessage.command == Commands.ADD_USER)
        {
            ThirtyYearsSessionManager.addUser(inMessage.sessionId, inMessage.sessionPas, inMessage.userName)
            sendMessage(session, Commands.ADD_USER)
        }
        else if(inMessage.command == Commands.SET_REAL_EXCUTE)
        {
            ThirtyYearsSessionManager.setRealExcude(inMessage.sessionId, inMessage.sessionPas,
                    inMessage.userName, inMessage.data)
            sendMessage(session, Commands.SET_REAL_EXCUTE)
        }
        else if(inMessage.command == Commands.SET_FALSH_EXCUTE)
        {
            ThirtyYearsSessionManager.setFalshExcute(inMessage.sessionId, inMessage.sessionPas,
                    inMessage.userName, inMessage.data)
            sendMessage(session, Commands.SET_FALSH_EXCUTE)
        }
        else if(inMessage.command == Commands.SET_VOTE)
        {
            ThirtyYearsSessionManager.vote(inMessage.sessionId, inMessage.sessionPas,
                    inMessage.userName, inMessage.data)
            sendMessage(session, Commands.SET_VOTE)
        }

        super.handleTextMessage(session, message)
    }

    private fun sendMessage(session: WebSocketSession,
                             command: Commands, data: String ="")
    {
        val message = ThirtyYearsMessage(-1, -1, "")
        message.command = command
        message.data=data
        session.sendMessage(TextMessage(message.toJson()))

    }





}