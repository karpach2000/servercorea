package com.parcel.tools.games.games.thirtyyears.comunicateinformation

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsUser
import dnl.utils.text.table.TextTable


class ThirtyYearsVoteInformation() {

    constructor(user: ThirtyYearsUser, trueTeller: ThirtyYearsUser, users: ArrayList<ThirtyYearsUser>)
            :this()
    {
        this.user = user
        this.trueTeller = trueTeller
        this.users = users
    }

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
         * Отмазка пользователя
         */
        var excude = ""
        /**
         * Пользователь за которого проголосовал пользователь.
         */
        var selectedAuthor = ""
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

        /**
         * Получить строку для вывода всей этой срани в таблицу
         */
        fun getDataRow(): Array<String?>
        {
            return arrayOf(name, trueTeller.toString(), excude,selectedAuthor, itsMe.toString(),
                    pointsCount.toString(), totalPointsCount.toString())
        }
    }

    /**
     * Данные таблицы отображаемой пользователям.
     */
    class Table{
        var event = ""
        val rows = ArrayList<TableRow>()
    }

    @Expose(serialize = false, deserialize = false)
    private lateinit var user: ThirtyYearsUser
    @Expose(serialize = false, deserialize = false)
    private lateinit var trueTeller: ThirtyYearsUser
    @Expose(serialize = false, deserialize = false)
    private lateinit var users: ArrayList<ThirtyYearsUser>


    var table = Table()


    fun toJson():String
    {
        putDataToTable()
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(table)
    }

    fun fromJson(json: String)
    {
        val builder = GsonBuilder()
        val table = builder.create().fromJson(json, Table::class.java)
        this.table = table
    }

    fun toTextTable(): TextTable {
        val columnNames =
        arrayOf<String>("name","trueTeller", "excude", "selectedAuthor","itsMe", "pointsCount", "totalPointsCount")
        val rows = Array(table.rows.size) { arrayOfNulls<String>(columnNames.size) }
        for(i in 0 until table.rows.size)
        {
            rows[i]=table.rows[i].getDataRow()
        }
        val tt = TextTable(columnNames, rows)
        tt.setAddRowNumbering(true)
        return tt
    }

    private fun putDataToTable()
    {
        table.event = trueTeller.event
        users.forEach {
            val tr = TableRow()
            tr.name = it.name

            if(it.name == trueTeller.name) {
                tr.trueTeller = true
                tr.excude = it.excute
            }
            else
            {
                tr.excude = it.falshExcute
            }
            if(it.name==user.name)
            {
                tr.itsMe = true
            }
            tr.selectedAuthor=it.gameUserVote.voteName
            //голоса
            tr.pointsCount = it.points
            tr.totalPointsCount = it.totalPoints
            table.rows.add(tr)
        }
    }


}