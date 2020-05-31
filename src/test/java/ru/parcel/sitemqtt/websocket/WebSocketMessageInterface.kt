package ru.parcel.sitemqtt.websocket

import com.parcel.tools.web.websockets.games.thirtyyears.json.ThirtyYearsMessage
import java.util.*
import javax.websocket.Session

/**
 * Интерфейс обрабатывающий входные реквесты.
 */
abstract class WebSocketMessageInterface {
    private lateinit var userSession: Session

    abstract fun handleMessage(message: ThirtyYearsMessage)
    fun addUserSession(userSession: Session)
    {
        this.userSession=userSession
    }

    @Synchronized
    protected fun sendAnser(message: ThirtyYearsMessage)
    {
        val td = message.toJson()
        val sec = Date(System.currentTimeMillis()).seconds
        println("$sec TX(server):$td")
        userSession!!.asyncRemote.sendText(td)
    }
}