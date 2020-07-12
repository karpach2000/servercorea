package ru.parcel.sitemqtt.games.gamesession.thirtyyears

import dnl.utils.text.table.TextTable
import org.springframework.boot.test.context.SpringBootTest

private val TIMEOUT = 5L

@SpringBootTest
class DisconnectTest {


    val page1 = WebPage(13, 87, "Petr")
    val page2 = WebPage(13, 87, "SashaGrey")
    val page3 = WebPage(13, 87, "Gena")
    val page4 = WebPage(13, 87, "Vasa")

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
        excude.add("Вызвал начальник")
        excude.add("Работа")
        excude.add("Я в походе не могу,от крокодила я бегу")
        excude.add("Митинг в поддержку навального")

        excude.forEach { falshExcude.add("_$it") }



    }


    fun test()
    {

        println("\n_______________________")
        println("_____DisconnectTest_____")
        println("__________________________\n\n")
        round1()




    }


    fun round1()
    {
        println("\n________________")
        println("_____ROUND1_____")
        println("________________\n")
        println("\nCONNECT")
        pages[0].createSessionIfNotExists()
        for(i in 1 until pages.size)
            pages[i].connectToSession()

        println("\nADD USER")
        //добавляем пользователей
        pages.forEach{it.addUser()}; Thread.sleep(TIMEOUT)//иначе тесты убегают вперед
        //проверяем статус страниц
        checkPagesStatus(WebPageStates.ADD_USER)

        println("\nSTART_GAME")
        //запускаем игру
        pages[0].startGame(); Thread.sleep(TIMEOUT)//иначе тесты убегают вперед
        //проверяем удаление и добавление
        deleteAndAddPage("SashaGrey")
        //проверяем статус страниц
        checkPagesStatus(WebPageStates.ENTER_REAL_EXCUTE_EVENT)
        //распечатываем отчет
        printPages()

        println("\nADD EXCUDE")
        //вводим отмазку
        for(i in 0 until pages.size)
        {
            pages[i].setRealExcude(excude[i])
        }
        //проверяем удаление и добавление
        deleteAndAddPage("SashaGrey")
        //проверяем статус страниц
        checkPagesStatus(WebPageStates.ENTER_FALSH_EXCUTE_EVENT)
        //распечатываем отчет
        printPages()

        println("\nPRINT FALSHE EXCUDE")
        //вводим фальшивую отмазку
        for(i in 0 until pages.size)
        {
            pages[i].setFalshExcude(falshExcude[i])
        }
        //проверяем удаление и добавление
        deleteAndAddPage("SashaGrey")
        //проверяем статус страниц
        checkPagesStatus(WebPageStates.VOTE_EVENT)
        //распечатываем отчет
        printPages()
        //распичатываем пришедшие варианты за кого можно проголосовать
        pages.forEach {
            println("Vote variants(${it.name}): ${it.voteVariants.toString()}")
        }

        println("\nVOTE")
        //голосуем
        //page1.setVote(page1.voteVariants.table.rows[1].anser)
        pages[1].setVote(pages[1].voteVariants.table.rows[0].anser)
        pages[2].setVote(pages[2].voteVariants.table.rows[1].anser)
        pages[3].setVote(pages[3].voteVariants.table.rows[0].anser)
        //проверяем статус страниц
        checkPagesStatus(WebPageStates.SHOW_RESULTS_EVENT)
        //распечатываем отчет
        printPages()
        println("\nSHOW_RESULTS")
        page1.voteInformation.toTextTable().printTable()
        //Thread.sleep(5000)
        println("\nROUND")
        //завершаем раунд
        page1.setRound();Thread.sleep(TIMEOUT)
        //проверяем удаление и добавление
        deleteAndAddPage("SashaGrey")
        //проверяем статус страниц
        checkPagesStatus(WebPageStates.ENTER_FALSH_EXCUTE_EVENT)
        //распечатываем отчет
        printPages()
        Thread.sleep(TIMEOUT)
    }


    fun deleteAndAddPage(name: String)
    {
        for(p in pages)
        {
            if(p.name == name){
                p.disconnect()
                pages.remove(p)
                break
            }
        }

        val newPage = WebPage(13, 87, name)
        pages.add(newPage)
        newPage.connectToSession()
        newPage.getGameStatus()
        Thread.sleep(TIMEOUT)
    }

    /**
     * Проверить состояние веб страниц.
     * @param ожидаемое состояние
     */
    private fun checkPagesStatus(state : WebPageStates)
    {
        pages.forEach {
            if(it.webPageState!=state)
            {
                println("Ошибка проверки статуса веб страницы пользователя ${it.name}" +
                        "Ожидали статус $state получили статус ${it.webPageState}.")
                assert(false)
            }
        }

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