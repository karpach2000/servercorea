package com.parcel.tools.spy

import com.google.gson.GsonBuilder

/**
 * Информация отображаемая каждому игроку.
 */
class UserInformation() {

    constructor(user: User, location:String, usersCount: Int, allUsers: String)
            :this()
    {
        name=user.name
        spy = user.spy
        this.location = location
        this.usersCount = usersCount
        this.allUsers = allUsers


    }
    constructor(error:String)
            :this()
    {
        this.error = error
    }

    /**
     * Списо игроков.
     */
    var allUsers = ""

    /**
     * Количество игроков
     */
    var usersCount = 0;

    /**
     * Имя текущего игрока
     */
    var name = ""

    /**
     * Является ли данный пользователь шпионом
     */
    var spy = false

    /**
     * Сообщение об ошибке
     */
    var error = ""

    /**
     * Локация либо сообщение о том -чт пользователь шпион
     */
    var location = ""
    get() {
        if(!spy)
            return field
        else
            return "ШПИОН(SPY)"
    }

    override fun toString() =
          "Name: $name\n"+
           "location: $location\n"+
           "Users in game: $usersCount\n"+
            "Users:\n$allUsers"



    fun toJson() :String
    {
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }
}