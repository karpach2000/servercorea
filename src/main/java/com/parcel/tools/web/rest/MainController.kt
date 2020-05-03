package com.parcel.tools.web.rest
        //import com.github.mustachejava.DefaultMustacheFactory
import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.bodies.admin.CounterAdmin
import com.parcel.tools.constructor.bodies.counter.Counter
import com.parcel.tools.constructor.bodies.mainpage.MainPage
import com.parcel.tools.constructor.games.CounterGames
import com.parcel.tools.constructor.games.mafia.CounterGamesMafia
import com.parcel.tools.constructor.games.spy.CounterGamesSpy
import com.parcel.tools.constructor.gamesSettings.CounterGamesSettings

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

//import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import javax.servlet.http.HttpSession

@Controller
class MainController {

    @RequestMapping("/")
    @Throws(IOException::class)
    internal fun index(model: Model, session: HttpSession): String {

        val mainPage = MainPage()
        val page = Page(mainPage)
        model.addAttribute("page", page)
        return "redirect:/spy"
    }

    @RequestMapping("/spy")
    @Throws(IOException::class)
    internal fun spy(model: Model, session: HttpSession): String {
        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/spy"
    }

//    @RequestMapping("/")
//    @Throws(IOException::class)
//    internal fun index(model: Model, session: HttpSession): String {
//        val mainPage = MainPage()
//        val page = Page(mainPage)
//
//        model.addAttribute("page", page)
//        return "web/html/games/spy"
//    }

//    @RequestMapping("/games/mafia")
//    @Throws(IOException::class)
//    internal fun gamesMafia(model: Model, session: HttpSession): String {
//        val counter = CounterGames()
//        val page = Page(counter)
//        model.addAttribute("page", page)
//        return "web/html/games/mafia"
//    }

    @RequestMapping("/admin")
    @Throws(IOException::class)
    internal fun admin(model: Model, session: HttpSession): String {
        val counter = CounterAdmin()
        val page = Page(counter)

        model.addAttribute("page", page)
        return "web/html/admin"
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