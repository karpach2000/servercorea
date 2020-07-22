package com.parcel.tools.games.games.thirtyyears.comunicateinformation

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsUser


class ThirtyYearsVoteVariants() {

    /**
     *
     */
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
     *
     * Класс описывающий варианты за кого может проголосовать пользователь.
     * (серриализуется в JSON)
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
        val table = builder.create().fromJson(json, Table::class.java)
        this.table = table
    }

    override fun toString(): String {
        var ans = ""
        table.rows.forEach {
            if(!it.itsMe)
                ans = "$ans${it.anser},"
            else
                ans = "$ans${it.anser}(itsMe),"
        }
        return ans
    }

    private fun putDataToTable()
    {
        table.event = trueTeller.event
        table.myQuestion = user.event==trueTeller.event
        //if(checkCanIVote())
            users.forEach {
                val tr = TableRow()

                //У всех берем фальшотмазку у атора вопроса реальную
                if(it.name==trueTeller.name)
                    tr.anser = it.excute
                else
                    tr.anser = it.falshExcute

                //помечаем автора ответа
                if(it.name==user.name)
                {
                    tr.itsMe = true
                }
                //голоса
                table.rows.add(tr)
            }
    }

    /**
     * Может ли пользователь которому принадлежит таблица голосовать.
     * (если таблица принадлежит автору вопроса, он не голосует)
     */
    private fun checkCanIVote() = user.event!=table.event


}