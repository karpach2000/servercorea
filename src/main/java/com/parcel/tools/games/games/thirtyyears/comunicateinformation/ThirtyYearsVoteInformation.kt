package com.parcel.tools.games.games.thirtyyears.comunicateinformation

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsUser

class TableRow
{
    /**
     * Имя игрока.
     */
    var name = ""
    /**
     * Данное событие предназначалось для этого игрока.
     */
    var trueTeller = false

    /**
     * Ответ пользователя на вопрос
     */
    var anser = ""
    /**
     * Это строка принадлежит пользователю, которому передали страницу
     */
    var itsMe = false

    /**
     * Количество очков за этот раунд.
     */
    var pointsCount = 0
    /**
     * Количество очков всего.
     */
    var totalPointsCount = 0
}

/**
 * Данные таблицы отображаемой пользователям.
 */
class Table{
    var event = ""
    val rows = ArrayList<TableRow>()
}


class ThirtyYearsVoteInformation(
        @Expose(serialize = false, deserialize = false) private val user: ThirtyYearsUser,
        @Expose(serialize = false, deserialize = false) private val trueTeller: ThirtyYearsUser,
        @Expose(serialize = false, deserialize = false) private val users: ArrayList<ThirtyYearsUser>
) {

    val table = Table()


    fun toJson():String
    {
        putDataToTable()
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(table)
    }

    private fun putDataToTable()
    {
        table.event = trueTeller.event
        users.forEach {
            val tr = TableRow()
            tr.name = it.name
            if(it.name == trueTeller.name) {
                tr.trueTeller = true
            }
            else
            {
                tr.anser = it.excute
            }
            if(it.name==user.name)
            {
                tr.itsMe = true
            }
            //голоса
            tr.pointsCount = it.gameUserVote.votedCount
            tr.totalPointsCount = it.points
            table.rows.add(tr)
        }
    }


}