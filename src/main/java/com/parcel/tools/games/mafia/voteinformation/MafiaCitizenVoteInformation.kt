package com.parcel.tools.games.mafia.voteinformation

import com.parcel.tools.games.mafia.MafiaUser
import com.parcel.tools.games.mafia.MafiaUserRoles
import java.lang.Exception

class MafiaCitizenVoteInformationException(message: String): Exception(message)
class MafiaCitizenVoteInformation(private val user: MafiaUser, private val  users: ArrayList<MafiaUser>) {

    inner class TableRow
    {
        var name = ""
        var role = ""
        var isAlife = ""
        var voteCount = ""
    }

    val table = ArrayList<TableRow>()


    fun toHtml(): String
    {
        putDataToTable()
        var ans = "<ul id=\"mafia_users\" >\n" +
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

        table.forEach {
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


    private fun putDataToTable()
    {
        table.clear()
        users.forEach {

            val tr = TableRow()
            tr.name = it.name
            tr.isAlife = it.isAlife.toString()
            tr.voteCount = it.votedCount.toString()
            if (user.role == MafiaUserRoles.CITIZEN) {
                if(it.isAlife)
                    tr.role = "Информация скрыты"
                else
                    tr.role = it.role.toString()

            } else if (user.role == MafiaUserRoles.MAFIA) {
                tr.role = it.role.toString()

            } else if (user.role == MafiaUserRoles.LEADING) {
                tr.role = it.role.toString()
            }
            table.add(tr)
            throw MafiaCitizenVoteInformationException("User role not correct!")
        }
    }
}