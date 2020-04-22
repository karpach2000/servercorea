package com.parcel.tools.web.websockets

import com.parcel.tools.spy.SpyEvent
import com.parcel.tools.spy.SpySessionManager
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
//логгер
private val logger = org.apache.log4j.Logger.getLogger(SpyWebSocketController::class.java!!)
private val SEPORATOR = "_"

@Component
class SpyWebSocketController : TextWebSocketHandler()  {



    private val spyEventHandlers = ArrayList<SpyEventHandler>()



    class SpyEventHandler(private var session: WebSocketSession,private var name: String): SpyEvent
    {



        override fun addUserEvent(userList: String) {
            sendMessage("addUserEvent$SEPORATOR$userList")
        }

        override fun startGameEvent() {
            sendMessage("startGameEvent")
        }

        override fun stopGameEvent(spyName: String) {
            sendMessage("stopGameEvent$SEPORATOR$spyName")
        }

        override fun spyIsNotSecretEvent(spyName: String) {
            sendMessage("spyIsNotSecretEvent$SEPORATOR$spyName")
        }

        fun updateSession(session: WebSocketSession)
        {
            this.session = session
        }
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
        val command = getComand(rx)
        if(command == "init")
        {
            val items = getData(rx).split(" ")
            val id= items[0].toLong()
            val pas = items[1].toLong()
            val name = items[2]
            logger.info("Web socket connection subscribe. id: $id, pas: $pas name: $name")
            SpySessionManager.subscribeSpySessionEvent(id, pas, SpyEventHandler(session,name))
        }
        else if(command == "ping")
        {
            logger.info("TX:pong")
            session.sendMessage(TextMessage("pong"))
        }
        else if(command == "ok")
        {
            //стандартный ответ на запрос
        }
        super.handleTextMessage(session, message)






    }

    private fun getComand(message: String):String
    {
        return message.split(SEPORATOR)[0]
    }

    private fun getData(message: String):String
    {
        return message.split(SEPORATOR)[1]
    }

}