package ru.parcel.sitemqtt.games.gamesession.thirtyyears

import org.springframework.boot.test.context.SpringBootTest
import ru.parcel.sitemqtt.websocket.WebsocketClientEndpoint

@SpringBootTest
class ThirtyYearsTest {

    //private var webSocketClient = WebsocketClientEndpoint("ws://127.0.0.1:8080/games/thirtyyears/ws")

    val page1 = WebPage(14, 88, "Petr")
    val page2 = WebPage(14, 88, "Victor")
    val page3 = WebPage(14, 88, "Gena")
    val page4 = WebPage(14, 88, "Vasa")


    fun test()
    {
        val pages = ArrayList<WebPage>()
        pages.add(page1)
        //pages.add(page2)
        //pages.add(page3)
        //pages.add(page4)

        pages.forEach{it.connect()}
        pages.forEach{it.addUser()}


    }


}