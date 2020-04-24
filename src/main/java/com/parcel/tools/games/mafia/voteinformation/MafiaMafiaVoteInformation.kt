package com.parcel.tools.games.mafia.voteinformation

import com.parcel.tools.games.mafia.MafiaUser
import com.parcel.tools.games.mafia.MafiaUserRoles

class MafiaMafiaVoteInformation(private val  users: ArrayList<MafiaUser>) {

    inner class TableRow
    {
        var name = ""
        var voteCount = ""
    }

    val table = ArrayList<TableRow>()

    fun toHtml(): String
    {
        putDataToTable()
        var ans = "<ul id=\"mafia_mafiaVoteTable\" >\n" +
                "            <table border=\"1\">\n" +
                "                <thead>\n" +
                "                <tr>\n" +
                "                    <th>Игрок</th>\n" +
                "                    <th>Количество голосов</th>\n" +
                "                </tr>\n" +
                "                </thead>\n" +
                "                <tbody>"

        table.forEach {
            ans=ans+ "                    <tr   class='table-data'>\n" +
                    "                        <td>${it.name}</td>\n" +
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
            if (it.role == MafiaUserRoles.CITIZEN && it.isAlife) {
                val tr = TableRow()
                tr.name = it.name
                tr.voteCount = it.votedCount.toString()
                table.add(tr)
            }
        }
    }


}