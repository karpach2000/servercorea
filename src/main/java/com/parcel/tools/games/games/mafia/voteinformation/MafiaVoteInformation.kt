package com.parcel.tools.games.games.mafia.voteinformation

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.parcel.tools.games.games.mafia.MafiaSessionState
import com.parcel.tools.games.games.mafia.MafiaUser
import com.parcel.tools.games.games.mafia.MafiaUserRoles
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
    var sheriffChecked = "SECRET"
    var isItMe = "false"
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
class MafiaVoteInformation(
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

    /*
    //УСТАРЕЛ!!!!
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
    */

    /**
     * Преобразует информацию из классов users в информацию передаваемую непосредственно пользователю user
     */
    private fun putDataToTable()
    {
        table.rows.clear()
        users.forEach {

            val tr = TableRow()
            tr.name = it.name!!
            tr.isAlife = it.isAlife.toString()

            //маркер что строка относится к пользователю
            if(user.name==it.name)
                tr.isItMe = true.toString()


            /*****ИНФА ДЛЯ ВЕДУЩЕГО*****/
            if(user.mafiaUserRole==MafiaUserRoles.LEADING)
            {
                tr.sheriffChecked = it.sheriffOptions.checked.toString()
            }



            /***ПОДСЧЕТ ГОЛОСОВ*****/
            //горожане не видят как голосует мафия
            if(user.mafiaUserRole != MafiaUserRoles.MAFIA && user.mafiaUserRole != MafiaUserRoles.LEADING && this.mafiaSessionState == MafiaSessionState.MAFIA_VOTE)
            {
                tr.voteCount = "SECRET"
            }
            else
                tr.voteCount = it.votedCount.toString()


            /****РОЛИ*****/
            //горожане не видят ролей пользователей
            if (user.mafiaUserRole == MafiaUserRoles.CITIZEN) {
                if(it.isAlife && it.mafiaUserRole!=MafiaUserRoles.LEADING && it.name!=user.name)
                    tr.role = "SECRET"
                else
                    tr.role = it.mafiaUserRole.toString()

            }
            //шериф не видит ролей пользователей
            else if(user.mafiaUserRole == MafiaUserRoles.SHERIFF || it.mafiaUserRole== MafiaUserRoles.LEADING)
            {
                //но если он проверил пользователя то видит
                if(user.sheriffOptions.checkedUserNames.contains(it.name!!)||it.mafiaUserRole==MafiaUserRoles.LEADING || it.name==user.name)
                    tr.role = it.mafiaUserRole.toString()
                else
                    tr.role = "SECRET"
            }
            //мафиозе видят только мафиозе, горожан, лидера. Остальных игроков принимают за горожан (пока аони живы).
            else if (user.mafiaUserRole == MafiaUserRoles.MAFIA) {
                if(!it.isAlife || it.mafiaUserRole== MafiaUserRoles.LEADING || it.mafiaUserRole==MafiaUserRoles.MAFIA || it.mafiaUserRole==MafiaUserRoles.CITIZEN)
                    tr.role = it.mafiaUserRole.toString()
                else
                    tr.role = MafiaUserRoles.CITIZEN.toString()

            } else if (user.mafiaUserRole == MafiaUserRoles.LEADING) {
                tr.role = it.mafiaUserRole.toString()
            }

            table.rows.add(tr)
            //throw MafiaCitizenVoteInformationException("User role not correct!")
        }
    }
}