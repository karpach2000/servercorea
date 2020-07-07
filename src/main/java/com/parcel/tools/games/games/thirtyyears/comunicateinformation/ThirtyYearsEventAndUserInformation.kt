package com.parcel.tools.games.games.thirtyyears.comunicateinformation

import com.google.gson.GsonBuilder

/**
 * Класс хранящий в себе информацию о событии и пользователе который в нем учавствует.
 */
class ThirtyYearsEventAndUserInformation()
{
    constructor(event:String, user:String):this()
    {
        this.event = event
        this.user = user
    }

    /**
     * Имя учавствующего пользователя.
     */
    var event = ""

    /**
     * Название события.
     */
    var user = ""

    fun toJson():String
    {
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }

    fun fromJson(json: String)
    {
        val builder = GsonBuilder()
        val data = builder.create().fromJson(json, ThirtyYearsEventAndUserInformation::class.java)
        this.user = data.user
        this.event = data.event
    }
}