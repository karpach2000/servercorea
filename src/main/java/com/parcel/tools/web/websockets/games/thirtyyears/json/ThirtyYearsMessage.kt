package com.parcel.tools.web.websockets.games.thirtyyears.json

import com.google.gson.GsonBuilder
import com.parcel.tools.web.websockets.games.thirtyyears.Commands

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
        this.sessionPas = sessionPas
        this.userName = userName
    }

    var userName = ""
    var sessionId = 0L
    var sessionPas = 0L

    var command = Commands.PING
    var data = ""

    private fun fromJson(json: String)
    {
        val builder = GsonBuilder()
        val thirtyYearsMessage = builder.create().fromJson(json, ThirtyYearsMessage::class.java)
        userName = thirtyYearsMessage.userName
        sessionId = thirtyYearsMessage.sessionId
        sessionPas = thirtyYearsMessage.sessionPas

        command = thirtyYearsMessage.command
        data = thirtyYearsMessage.data
    }


    fun toJson():String
    {
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }



}