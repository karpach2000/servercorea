package com.parcel.tools.games.games.thirtyyears.comunicateinformation

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsUser
import com.parcel.tools.web.websockets.games.thirtyyears.json.ThirtyYearsMessage


class ThirtyYearsVoteVariants() {

    constructor(user: ThirtyYearsUser, trueTeller: ThirtyYearsUser, users: ArrayList<ThirtyYearsUser>):this()
    {
        this.user = user
        this.trueTeller = trueTeller
        this.users = users
    }

    class TableRow
    {
        /**
         * Ответ пользователя на вопрос
         */
        var anser = ""
        /**
         * Это строка принадлежит пользователю, которому передали страницу
         */
        var itsMe = false
    }

    /**
     * Данные таблицы отображаемой пользователям.
     */
    class Table{
        /**
         * Событие от которого все отмазываются.
         */
        var event = ""

        /**
         * Я автор данного вопроса.
         */
        var myQuestion = false

        /**
         * Строки таблицы с вариантами голосования.
         */
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
        val thirtyYearsMessage = builder.create().fromJson(json, ThirtyYearsVoteVariants::class.java)
        table = thirtyYearsMessage.table
    }

    private fun putDataToTable()
    {
        table.event = trueTeller.event
        users.forEach {
            val tr = TableRow()
            tr.anser = it.excute

            if(it.name==user.name)
            {
                tr.itsMe = true
            }
            //голоса
            table.rows.add(tr)
        }
    }


}