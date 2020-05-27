package com.parcel.tools.web.websockets.games.thirtyyears.json

import com.google.gson.GsonBuilder

class ThirtyYearsMessage() {

    constructor(json: String):this()
    {
        fromJson(json)
    }

    var userName = ""
    var sessionId = 0L
    var sessionPas = 0L

    var command = ""
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