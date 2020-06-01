package ru.parcel.sitemqtt.games.gamesession.thirtyyears

import dnl.utils.text.table.TextTable
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class ThirtyYearsTest {

    //private var webSocketClient = WebsocketClientEndpoint("ws://127.0.0.1:8080/games/thirtyyears/ws")

    val page1 = WebPage(14, 88, "Petr")
    val page2 = WebPage(14, 88, "Victor")
    val page3 = WebPage(14, 88, "Gena")
    val page4 = WebPage(14, 88, "Vasa")

    val pages = ArrayList<WebPage>()
    val excude = ArrayList<String>()
    val falshExcude = ArrayList<String>()

    init {
        pages.add(page1)
        pages.add(page2)
        pages.add(page3)
        pages.add(page4)

        excude.add("Жирная жопа")
        excude.add("Худые ляшки")
        excude.add("Я унылое говно")
        excude.add("Понос")
        excude.add("Артрит")
        excude.add("Артроз")
        excude.add("Опестархоз")
        excude.add("Газовая плита")
        excude.add("Непогашеная сигарета")

        excude.forEach { falshExcude.add("_$it") }



    }

    fun test()
    {



        println("\nCONNECT")
        pages.forEach{it.connect()}

        println("\nADD USER")
        pages.forEach{it.addUser()}

        println("\nSTART_GAME")
        pages[0].startGame()
        printPages()

        println("\nADD EXCUDE")
        for(i in 0 until pages.size)
        {
            pages[i].setRealExcude(excude[i])
        }
        printPages()

        println("\nPRINT FALSHE EXCUDE")
        for(i in 0 until pages.size)
        {
            pages[i].setFalshExcude(falshExcude[i])
        }
        printPages()
        pages.forEach {
            println("Vote variants(${it.name}): ${it.voteVariants.toString()}")
        }

        println("\nVOTE")
        //page1.setVote(page1.voteVariants.table.rows[1].anser)
        page2.setVote(page2.voteVariants.table.rows[0].anser)
        page3.setVote(page3.voteVariants.table.rows[1].anser)
        page4.setVote(page4.voteVariants.table.rows[0].anser)
        printPages()

        println("\nSHOW_RESULTS")
        page1.voteInformation.toTextTable().printTable()

        Thread.sleep(5000)

    }



    private fun printPages()
    {
        val columnNames =
                arrayOf<String>("name", "webPageStat", "myEvent", "myExcude", "currentEvent","currentExcude")
        val rows = Array(pages.size) { arrayOfNulls<String>(columnNames.size) }
        for(i in 0 until pages.size)
        {
            rows[i]=pages[i].getDataRow()
        }
        val textTable = TextTable(columnNames, rows)
        textTable.setAddRowNumbering(true)
        textTable.printTable()

    }

}