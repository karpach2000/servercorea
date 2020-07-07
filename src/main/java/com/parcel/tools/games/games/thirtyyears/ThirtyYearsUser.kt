package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsEventAndUserInformation
import com.parcel.tools.games.gamesuser.GameUser

class ThirtyYearsUser (name:String) : GameUser(name) {

    /**
     * Отмазывается ли пользователь сейчас (или делает вид что отмазывается как соперник)
     */
    var isExcuting = false
    /**
     * Событие от которого отмазывется пользователь.
     */
    var event = ""
    /**
     * Отмазка которую придумал пользователь.
     */
    var excute = ""
    /**
     * Псевдоотмазка которую придумывает пользователь за место своего соперника.
     */
    var falshExcute = ""

    /**
     * Количество очков набраное пользователем за раунд.
     */
    var points = 0
    /**
     * Суммарное количество очков набраное пользователем.
     */
    var totalPoints = 0

    /**
     * Отчищает все данные о фальшивых отмазках и голосовании.
     * (подготавливает к новому раунду)
     */
    fun clear()
    {
        falshExcute = ""
    }

    fun getThirtyYearsEventAndUserInformation() =
            ThirtyYearsEventAndUserInformation(event, name)


}