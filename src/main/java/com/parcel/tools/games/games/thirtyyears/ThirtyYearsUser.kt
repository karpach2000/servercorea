package com.parcel.tools.games.games.thirtyyears

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsEventAndUserInformation
import com.parcel.tools.games.gamesuser.GameUser

class ThirtyYearsUser (name:String) : GameUser(name) {

    /**
     * Отмазывается ли пользователь сейчас (или делает вид что отмазывается как соперник)
     */
    @Expose(serialize = false, deserialize =false)
    var isExcuting = false
    /**
     * Событие от которого отмазывется пользователь.
     */
    @Expose(serialize = false, deserialize =false)
    var event = ""
    /**
     * Отмазка которую придумал пользователь.
     */
    @Expose(serialize = false, deserialize =false)
    var excute = "Пользователь отправил прекрасное нихуя!"
    /**
     * Псевдоотмазка которую придумывает пользователь за место своего соперника.
     */
    @Expose(serialize = false, deserialize =false)
    var falshExcute = "Пользователь отправил прекрасное нихуя!"

    /**
     * Количество очков набраное пользователем за раунд.
     */
    @Expose(serialize = false, deserialize =false)
    var points = 0
    /**
     * Суммарное количество очков набраное пользователем.
     */
    @Expose(serialize = false, deserialize =false)
    var totalPoints = 0

    /**
     * Отчищает все данные о фальшивых отмазках и голосовании.
     * (подготавливает к новому раунду)
     */
    fun clear()
    {
        falshExcute = ""
    }

    override fun toJson():String{
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }

    fun getThirtyYearsEventAndUserInformation() =
            ThirtyYearsEventAndUserInformation(event, this!!.name!!)


}