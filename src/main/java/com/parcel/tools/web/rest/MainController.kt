package com.parcel.tools.web.rest
        //import com.github.mustachejava.DefaultMustacheFactory
import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.bodies.admin.CounterAdmin
import com.parcel.tools.constructor.bodies.games.CounterGames
import com.parcel.tools.constructor.bodies.gamesSettings.CounterGamesSettings
import com.parcel.tools.games.games.cards.CardsSessionManager
import com.parcel.tools.games.games.mafia.MafiaSessionManager
import com.parcel.tools.games.games.spy.SpySessionManager

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

//import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import javax.servlet.http.HttpSession

@Controller
class MainController {


    @RequestMapping("/")
    @Throws(IOException::class)
    internal fun index(model: Model, session: HttpSession): String {

        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/games"
    }

    /**
     * Возвращает основную статистику по игре.
     */
    @RequestMapping("/serverstatus")
    @Throws(IOException::class)
    @ResponseBody
    internal fun serverStatus(model: Model, session: HttpSession): String {
        val spySessionCount = SpySessionManager.countSessions()
        val mafiaSessionCount = MafiaSessionManager.countSessions()
        val cardsSessionCount = CardsSessionManager.countSessions()

        return "{'spySessionCount': $spySessionCount, 'mafiaSessionCount': $mafiaSessionCount," +
                "'cardsSessionCount': $cardsSessionCount }"
    }





    @RequestMapping("/admin")
    @Throws(IOException::class)
    internal fun admin(model: Model, session: HttpSession): String {
        val counter = CounterAdmin()
        val page = Page(counter)

        model.addAttribute("page", page)
        return "web/html/admin"
    }

    @RequestMapping("/games")
    @Throws(IOException::class)
    internal fun games(model: Model, session: HttpSession): String {
        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/games"
    }

    @RequestMapping("/games_settings")
    @Throws(IOException::class)
    internal fun gamesSettings(model: Model, session: HttpSession): String {
        val counter = CounterGamesSettings()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/gamesSettings"
    }


}