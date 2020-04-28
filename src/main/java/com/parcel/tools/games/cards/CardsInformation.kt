package com.parcel.tools.games.cards

import com.google.gson.GsonBuilder

/**
 * Информация отображаемая каждому игроку.
 */
class UserInformation() {

    constructor(user: CardsUser, usersCount: Int, allUsers: String)
            :this()
    {
        name=user.name
        userCard = user.userCard
        this.usersCount = usersCount
        this.allUsers = allUsers


    }
    constructor(error:String)
            :this()
    {
        this.error = error
    }

    /**
     * Список игроков.
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
     * Карточка текущего игрока
     */
    var userCard = ""

    /**
     * Сообщение об ошибке
     */
    var error = ""

    /**
     * Локация либо сообщение о том -чт пользователь шпион
     */

    override fun toString() =
          "Name: $name\n"+
           "Users in game: $usersCount\n"+
            "Users:\n$allUsers"



    fun toJson() :String
    {
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }
}