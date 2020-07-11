package com.parcel.tools.web.websockets.games.thirtyyears

import com.parcel.tools.games.games.thirtyyears.ThirtyYearsEvent
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsSessionManager
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsSessionNotFatalException
import com.parcel.tools.games.gamesession.GameSessionNotFatalException
import com.parcel.tools.web.websockets.games.thirtyyears.json.MessageStatus
import com.parcel.tools.web.websockets.games.thirtyyears.json.ThirtyYearsMessage
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
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
        /**
         * Идентефикатор по которому можно отличить 2 разных пользователей с одинаковыми именами пользователя
         */
        override var identeficator = ""

        var sessionId = 0L
        var sessionPas = 0L

        /**
         * Флаг ожидания ответва от веб страницы.
         */
        var wateAnser = false
        private  set

        /**
         *Таймаут ожидания ответа от страницы.
         */
        val wateAnserTimeout = 10000L

        /**
         * Кэш хранения входящего сообщения
         */
        var inMessageCash = ""

        /**
         * Событие перевода в статус ввведения реальной отмазки.
         */
        override fun ENTER_REAL_EXCUTE_event(event: String) {
            request(Commands.ENTER_REAL_EXCUTE_EVENT, event)
        }
        /**
         * Событие перевода в статус фальшивой отмазки.
         */
        override fun ENTER_FALSH_EXCUTE_event(event: String) {
            request(Commands.ENTER_FALSH_EXCUTE_EVENT, event)
        }
        /**
         * Событие перевода в статус голосования.
         */
        override fun VOTE_event(variants: String) {
            request(Commands.VOTE_EVENT, variants)
        }
        /**
         * Событие Показываения пользователю результаты всей игры.
         */
        override fun SHOW_FINAL_RESULTS_event(table: String) {
            request(Commands.SHOW_FINAL_RESULTS_EVENT, table)
        }
        /**
         * События демонстрации результатов голосования пользователям.
         */
        override fun SHOW_RESULTS_event(table: String) {
            request(Commands.SHOW_RESULTS_EVENT, table)
        }
        /**
         * Запустить таймер.
         */
        override fun START_TIMER_event(miles: Long) {
            request(Commands.START_TIMER_EVENT, miles.toString())
        }

        override fun addUserEvent(userList: String) {
            request(Commands.ADD_USER_EVENT, userList)
        }

        override fun startGameEvent() {
            request(Commands.START_GAME_EVENT)
        }

        override fun stopGameEvent(spyName: String) {
            request(Commands.STOP_GAME_EVENT)
        }

        /**
         * Опускает в данный класс входящее сообщение.
         */
        override  fun setInMessage(message: String)
        {
            inMessageCash = message
            wateAnser = false
        }

        private fun request(command: Commands, data: String= ""): String
        {
            sendMessage(command, data)
            wateAnser = true
            var timer = wateAnserTimeout
            while(timer>0 && wateAnser)
            {
                Thread.sleep(10)
                timer = timer-10
            }
            //logger.info("inMessageCash = $inMessageCash")
            return inMessageCash
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
        //session.
        val id = getUserIdenteficator(session)
        ThirtyYearsSessionManager.deSubscribeGameSessionEvent(id)
        super.afterConnectionClosed(session, status)
    }
    @Override
    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Web socket connected ${getUserIdenteficator(session)}")
        super.afterConnectionEstablished(session)
    }

    @Override
    /**
     * Событие прихода сообщения
     * */
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        super.handleTextMessage(session, message)//хз что это(легаси от класса родителя)
        val rx = message.payload//стринга с текстом сообщения

        try {
            val inMessage = ThirtyYearsMessage(rx)//дессириализуем стрингу в объект
            //Если Веб страница сделала реквест мне
            if (!inMessage.isAnserOnRequest)//если не стоит флаг что это ответ на реквест, обрабатываем его как сервер
            {
                logger.info("RX(server):${rx} ")
                //тебе все дальнейшие действия необходимо запустить в в отдельном потоке т.к.
                //они создают реквеств которые ожидают ответы улавливаемые этим методом,
                //который сука синхронизирован
                Thread(Runnable { webPageRequestsParser(session, inMessage)}).start()
            }
            else//если же флаг стоит, то это ответ на мой запрос
            {
                logger.info("RX(client):${rx} ")
                val event = ThirtyYearsSessionManager.getGameSessionEvents(
                        inMessage.sessionId, inMessage.sessionPas, inMessage.userName)
                event.setInMessage(message.payload)
            }
        }
        catch (ex: java.lang.Exception)
        {
            logger.error("MESSAGE_PARSING_ERROR")
            sendMessage(session, Commands.ERROR, "MESSAGE_PARSING_ERROR", true, MessageStatus.ERROR)
        }

    }

    /**
     * Обрабатываем реквесты пришедшие от WEB страницы.
     */
    private fun webPageRequestsParser(session: WebSocketSession, inMessage: ThirtyYearsMessage)
    {
        try {
            if (inMessage.command == Commands.PING) {
                sendMessage(session, Commands.PONG, "", true)
            } else if (inMessage.command == Commands.CREATE_SESSION_IF_NOT_EXIST) {
                val id = inMessage.sessionId
                val pas = inMessage.sessionPas
                val name = inMessage.userName
                logger.info("Web socket connection subscribe. id: $id, pas: $pas name: $name")
                val thirtyYearsEventHandler = ThirtyYearsEventHandler(session, name)
                thirtyYearsEventHandler.userName = name
                thirtyYearsEventHandler.sessionPas = pas
                thirtyYearsEventHandler.sessionId = id
                thirtyYearsEventHandler.identeficator = getUserIdenteficator(session)
                if(ThirtyYearsSessionManager.addSessionIfNotExist(id, pas)) {
                    ThirtyYearsSessionManager.subscribeGameSessionEvent(id, pas, thirtyYearsEventHandler)
                    sendMessageAnser(session, inMessage)
                }
                else
                {
                    sendMessageAnser(session, inMessage, "SESSION_IS_EXIST", MessageStatus.ERROR)

                }
            }
            else if (inMessage.command == Commands.CONNECT_TO_SESSION) {
                val id = inMessage.sessionId
                val pas = inMessage.sessionPas
                val name = inMessage.userName
                logger.info("Web socket connection subscribe. id: $id, pas: $pas name: $name")
                val thirtyYearsEventHandler = ThirtyYearsEventHandler(session, name)
                thirtyYearsEventHandler.userName = name
                thirtyYearsEventHandler.sessionPas = pas
                thirtyYearsEventHandler.sessionId = id
                thirtyYearsEventHandler.identeficator = getUserIdenteficator(session)
                if(ThirtyYearsSessionManager.isSessionExists(id)) {
                    ThirtyYearsSessionManager.subscribeGameSessionEvent(id, pas, thirtyYearsEventHandler)
                    sendMessageAnser(session, inMessage)
                }
                else
                {
                    sendMessageAnser(session, inMessage, "SESSION_IS_NOT_EXIST", MessageStatus.ERROR)

                }
            } else if (inMessage.command == Commands.ADD_USER) {
                sendMessageAnser(session, inMessage)
                ThirtyYearsSessionManager.addUser(inMessage.sessionId, inMessage.sessionPas, inMessage.userName)


            } else if (inMessage.command == Commands.START_GAME) {
                    ThirtyYearsSessionManager.startGame(inMessage.sessionId, inMessage.sessionPas)
                    sendMessageAnser(session, inMessage)
            } else if (inMessage.command == Commands.SET_REAL_EXCUTE) {
                ThirtyYearsSessionManager.setRealExcude(inMessage.sessionId, inMessage.sessionPas,
                        inMessage.userName, inMessage.data)
                sendMessageAnser(session, inMessage)
            } else if (inMessage.command == Commands.SET_FALSH_EXCUTE) {
                ThirtyYearsSessionManager.setFalshExcute(inMessage.sessionId, inMessage.sessionPas,
                        inMessage.userName, inMessage.data)
                sendMessageAnser(session, inMessage)
            } else if (inMessage.command == Commands.SET_VOTE) {
                ThirtyYearsSessionManager.vote(inMessage.sessionId, inMessage.sessionPas,
                        inMessage.userName, inMessage.data)
                sendMessageAnser(session, inMessage )
            } else if (inMessage.command == Commands.ROUND) {
                ThirtyYearsSessionManager.round(inMessage.sessionId, inMessage.sessionPas,
                        inMessage.userName)
                sendMessageAnser(session, inMessage )
            }
            else if(inMessage.command == Commands.GET_GAME_STATUS) {
                val data = ThirtyYearsSessionManager.getGameStatus(inMessage.sessionId, inMessage.sessionPas,
                        inMessage.userName)
                sendMessageAnser(session, inMessage, data )
            }
            else
            {
                logger.warn("Unsorted command")
                sendMessageAnser(session, inMessage, "COMMAND_NO_SUPPORTED",  MessageStatus.ERROR)
            }
        }
        catch (ex: GameSessionNotFatalException)
        {
            sendMessageAnser(session, inMessage, "${ex.message}",  MessageStatus.ERROR)
        }
        catch (ex: ThirtyYearsSessionNotFatalException)
        {
            sendMessageAnser(session, inMessage, "${ex.message}",  MessageStatus.ERROR)
        }
        catch (ex: GameSessionNotFatalException)
        {
            sendMessageAnser(session, inMessage, "${ex.message}",  MessageStatus.ERROR)
        }
        catch(ex: Exception)
        {
            sendMessageAnser(session, inMessage, "${ex.message}",  MessageStatus.FATAL)
        }
    }


    /**
     * Сгенерировать ответ на сообщение.
     */
    private fun sendMessageAnser(session: WebSocketSession, inMessage: ThirtyYearsMessage, data: String ="",
                                 messageStatus : MessageStatus = MessageStatus.GOOD) =
            sendMessage(session, inMessage.command, data, true, messageStatus,
            inMessage.sessionId, inMessage.sessionPas, inMessage.userName)


    /**
     * Отправить сообщение WEB страницам.
     */
    private fun sendMessage(session: WebSocketSession, command: Commands,data: String ="",
                            isAnserOnRequest :Boolean = false, messageStatus : MessageStatus = MessageStatus.GOOD,
                            sessionId: Long =-1, sessioinPas: Long =-1, userName: String = "")
    {
        val message = ThirtyYearsMessage(sessionId, sessioinPas, userName)
        message.command = command
        message.data=data
        message.isAnserOnRequest = isAnserOnRequest
        message.messageStatus = messageStatus
        val tx = message.toJson()
        logger.info("TX(server):${tx} ")
        session.sendMessage(TextMessage(tx))

    }

    /**
     * Получить идентефикатор по которому можно отличить 2 разных пользователей с одинаковыми именами пользователя
     */
    private fun getUserIdenteficator(session: WebSocketSession): String
    {
    /*
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val name: String = auth.getName() //get logged in username
        if(name!="anonymousUser")
            return name
        else
            return auth.hashCode().toString()

     */
        return session.id
    }





}