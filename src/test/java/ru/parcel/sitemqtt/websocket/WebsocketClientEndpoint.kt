package ru.parcel.sitemqtt.websocket

import com.parcel.tools.web.websockets.games.thirtyyears.json.ThirtyYearsMessage
import java.net.URI;
import java.util.*
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
open class WebsocketClientEndpoint(endpointURI: URI?) {



    var userSession: Session? = null
    private var messageHandler: WebSocketMessageInterface? = null

    /**
     * Имя страницы на которой используется этот веб соккет коннектор.
     * (нужно чтобы отличать в логах коннекторы нескольких страниц)
     */
    private var pageName: String = ""

    /**
     * Время которое мы ждкм ответа на реквест.
     */
    private val TIMEOUT = 10000L


    /*********БУФЕР РЕКВЕСТА*******/
    /**
     * Буфер куда кладется ответ на реквест.
     */
    private var outRequestMessageBufer = ThirtyYearsMessage()

    /**
     * У нас есть ответ ответ на реквест.
     */
    private var wateAnser = false


    //private val logger = org.apache.log4j.Logger.getLogger(WebsocketClientEndpoint::class.java!!)


    constructor(url: String, pageName : String):this(URI(url))
    {
        this.pageName = pageName
    }

    init {
        try {
            val container = ContainerProvider.getWebSocketContainer()
            container.connectToServer(this, endpointURI)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    fun onOpen(userSession: Session?) {
        println("opening websocket")
        this.userSession = userSession
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    fun onClose(userSession: Session?, reason: CloseReason?) {
        println("closing websocket")
        this.userSession = null
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param messageLine The text message
     */
    @OnMessage
    fun onMessage(messageLine: String?) //:String
    {


        val message = ThirtyYearsMessage(messageLine!!)

        //если нам прислали реквест
        if (!message.isAnserOnRequest) {
            val sec = Date(System.currentTimeMillis()).seconds
            println("[$pageName]RX(server):$messageLine")
            Thread(Runnable { messageHandler!!.handleMessage(message) }).start()

        }
        //если ответ на реквест
        else
        {
            println("[$pageName]RX(client):$messageLine")
            outRequestMessageBufer = message
            wateAnser = false
        }
        //return messageLine
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    fun addMessageHandler(msgHandler: WebSocketMessageInterface?) {
        messageHandler = msgHandler
        messageHandler!!.addUserSession(userSession!!)
    }

    /**
     * Send a message.
     *
     * @param message
     */
    fun sendRequest(message: ThirtyYearsMessage) :ThirtyYearsMessage{
        val td = message.toJson()
        println("[$pageName]TX(client):$td")
        userSession!!.asyncRemote.sendText(td)
        wateAnser = true
        var timer = TIMEOUT
        while (timer>0 && wateAnser)
        {
            Thread.sleep(1)
            timer -= 1
        }
        return outRequestMessageBufer
    }


}