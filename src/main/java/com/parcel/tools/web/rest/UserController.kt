package com.parcel.tools.web.rest

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.bodies.admin.CounterAdmin
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import javax.servlet.http.HttpServletRequest


@Controller
class UserController {




    /**
     * Разлогинится.
     */
    @RequestMapping("/logout")
    @Throws(IOException::class)
    internal fun logout( request: HttpServletRequest): String {
        val httpSession = request.getSession()
        httpSession.invalidate()//команда киляет сессию
        return "redirect:/"
    }

    /**
     * Разлогинится.
     */
    @RequestMapping("/session/getCurrentLogin")
    @Throws(IOException::class)
    @ResponseBody
    internal fun getCurrentLogin( request: HttpServletRequest): String {
        val httpSession = request.getSession()
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val name: String = auth.getName() //get logged in username
        return name
    }


    /**
     * Добавить пользователя.
     */
    @RequestMapping("/addUser")
    @Throws(IOException::class)
    internal fun addUser(model: Model, request: HttpServletRequest,
                         @RequestParam("login") login: String,
                         @RequestParam("password") password: String,
                         @RequestParam("role") role: String): String {

       com.parcel.tools.Globals.userManager.addUser(login, password, role)
        val counter = CounterAdmin()
        val page = Page(counter)

        model.addAttribute("page", page)
        return "web/html/admin"
    }
    /**
     * Удалить пользователя.
     */
    @RequestMapping("/deleteUser")
    @Throws(IOException::class)
    internal fun dellUser(model: Model, request: HttpServletRequest,
                          @RequestParam("login") login: String) : String {
        com.parcel.tools.Globals.userManager.dellUser(login)
        val counter = CounterAdmin()
        val page = Page(counter)

        model.addAttribute("page", page)
        return "web/html/admin"
    }

}