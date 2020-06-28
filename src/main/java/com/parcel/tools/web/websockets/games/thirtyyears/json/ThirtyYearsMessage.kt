package com.parcel.tools.web.websockets.games.thirtyyears.json

import com.google.gson.GsonBuilder
import com.parcel.tools.web.websockets.games.thirtyyears.Commands

/**
 * Объект сообщений взаимодействия с сервером.
 *
 *          Описание архитектуры взаимодействия.
 *              Клинетн серверная архитектура.
 *    После установления соединения ВЕБ страница общается с сервером игры при помощи реквестов.
 *  Реквест представляет из себя пару: запрос ответ. Сторона отправляющая реквест называется клиентом,
 *  принимающая реквест - сервером. Отправлять и принимать реквесты может как сервер игры, так и ВЕБ страница.
 *  Для упрощения разбора сообщений, во всех сообщениях клиента флаг isAnserOnRequest имеет значениях false,
 *  а в сообщениях сервер true.
 *
 *              Устанговление соединения для создания новой сессии
 *  1) Web страница при необходимости взаимодействия с сервером игры при помощи
 *      веб сокетов устанавливает коннект.
 *  2) Web страница отправляет реквест с командой CREATE_SESSION_IF_NOT_EXIST в котором соответсвующих полях
 *      передается имя пользователя, id и пароль сессии.
 *
 *              Устанговление соединения для соединения с существующей скссией
 *  1) Web страница при необходимости взаимодействия с сервером игры при помощи
 *      веб сокетов устанавливает коннект.
 *  2) Web страница отправляет реквест с командой CONNECT_TO_SESSION в котором соответсвующих полях
 *      передается имя пользователя, id и пароль сессии.
 *
 */
class ThirtyYearsMessage() {

    /**
     * Конструктор серриализующий объект из JSON.
     */
    constructor(json: String):this()
    {
        fromJson(json)
    }
    /**
     * Конструктор серриализующий объект из JSON.
     */
    constructor(sessionId: Long, sessionPass: Long, userName: String):this()
    {
        this.sessionId = sessionId
        this.sessionPas = sessionPass
        this.userName = userName
    }

    /**
     * Имя пользователя игры отправившего сообщение
     */
    var userName = ""

    /**
     * ID сессии.
     */
    var sessionId = 0L

    /**
     * Парооль сессии.
     */
    var sessionPas = 0L

    /**
     * Передаваемая команда.
     */
    var command = Commands.PING

    /**
     * Передаваемое сообщение.
     * (при передаче json -а необходимо пердавать его как текст. )
     */
    var data = ""

    /**
     * Является ли это сообщение ответом на реквест
     */
    var isAnserOnRequest = false

    /**
     * Статус сообщения.
     */
    var messageStatus = MessageStatus.GOOD


    private fun fromJson(json: String)
    {
        val builder = GsonBuilder()
        val thirtyYearsMessage = builder.create().fromJson(json, ThirtyYearsMessage::class.java)
        userName = thirtyYearsMessage.userName
        sessionId = thirtyYearsMessage.sessionId
        sessionPas = thirtyYearsMessage.sessionPas

        command = thirtyYearsMessage.command
        data = thirtyYearsMessage.data

        isAnserOnRequest = thirtyYearsMessage.isAnserOnRequest
    }


    fun toJson():String
    {
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }



}