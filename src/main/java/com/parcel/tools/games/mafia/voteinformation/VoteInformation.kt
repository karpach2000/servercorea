package com.parcel.tools.games.mafia.voteinformation

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.parcel.tools.games.mafia.MafiaSessionState
import com.parcel.tools.games.mafia.MafiaUser
import com.parcel.tools.games.mafia.MafiaUserRoles
import java.lang.Exception

class MafiaVoteInformationException(message: String): Exception(message)

/**
 * Строка таблицы пользователей и ролей, отображаемая игрокам.
 */
class TableRow
{
    var name = ""
    var role = ""
    var isAlife = ""
    var voteCount = ""
}
/**
 * Таблица пользователей и ролей, отображаемая игрокам.
 */
class Table {
    val rows = ArrayList<TableRow>()
}

/**
 * Класс предназначен для работы с информацией отображаемой пользователю в таблице при голосовании.
 */
class VoteInformation(
        @Expose(serialize = false, deserialize = false) private val user: MafiaUser,
        @Expose(serialize = false, deserialize = false) private val users: ArrayList<MafiaUser>,
        @Expose(serialize = false, deserialize = false) private val mafiaSessionState: MafiaSessionState) {



    val table = Table()


    fun toJson():String
    {
        putDataToTable()
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(table)
    }

    fun toHtml(): String
    {
        putDataToTable()
        var ans = "<ul  >\n" +
                "            <table border=\"1\">\n" +
                "                <thead>\n" +
                "                <tr>\n" +
                "                    <th>Игрок</th>\n" +
                "                    <th>Роль</th>\n" +
                "                    <th>Статус (жив/мертв)</th>\n" +
                "                    <th>Количество голосов [шт.]</th>\n" +
                "                </tr>\n" +
                "                </thead>\n" +
                "                <tbody>"

        table.rows.forEach {
            ans=ans+ "                    <tr   class='table-data'>\n" +
                    "                        <td>${it.name}</td>\n" +
                    "                        <td>${it.role}</td>\n" +
                    "                        <td>${it.isAlife}</td>\n" +
                    "                        <td>${it.voteCount}</td>\n" +
                    "                    </tr>\n"
        }
        ans = ans + "                </tbody>\n" +
                "            </table>\n" +
                "        </ul>"
        return ans
    }


    /**
     * Преобразует информацию из классов users в информацию передаваемую непосредственно пользователю user
     */
    private fun putDataToTable()
    {
        table.rows.clear()
        users.forEach {

            val tr = TableRow()
            tr.name = it.name
            tr.isAlife = it.isAlife.toString()

            //горожане не видят как голосует мафия
            if(user.role == MafiaUserRoles.CITIZEN && this.mafiaSessionState == MafiaSessionState.MAFIA_VOTE)
            {
                tr.voteCount = "SECRET"
            }
            else
                tr.voteCount = it.votedCount.toString()

            //горожане не видят ролей пользователей
            if (user.role == MafiaUserRoles.CITIZEN) {
                if(it.isAlife)
                    tr.role = "SECRET"
                else
                    tr.role = it.role.toString()

            } else if (user.role == MafiaUserRoles.MAFIA) {
                tr.role = it.role.toString()

            } else if (user.role == MafiaUserRoles.LEADING) {
                tr.role = it.role.toString()
            }

            table.rows.add(tr)
            //throw MafiaCitizenVoteInformationException("User role not correct!")
        }
    }
}