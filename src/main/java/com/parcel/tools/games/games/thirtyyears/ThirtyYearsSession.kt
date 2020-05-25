package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.Globals
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.games.spy.SpyEvent
import com.parcel.tools.games.games.spy.SpyUser
import com.parcel.tools.games.gamesession.GamesSession

class ThirtyYearsSessionException(message: String):Exception(message)

/**
 * Объект игры 30 лет.
 * Правила:
    * Игра 30 лет (помесь джетбокса и эмоджионариума)
    *   1) Есть список мероприятий, каждому игроку рандомом выдается по своему мероприятию
    *   2)Каждый игрок отвечает на вопрос почему он не может посетить данное мероприятие
    *   3)Дальше всем игрокам задается один и тот же вопрос (пр. почему Валера не может пойти прыгать с веревкой).
    *   4)Задача игроков ответить так как это сделал бы Валера
    *   5)Дальше все пытаются угадать вариант валеры
    *   Очки начисляются как в эмоджинариуме:
    *   - угадал +1
    *   - тебя отгадали 5 человек, тебе + 5 очков
    *   - тебя угадали все, ты остался без очков
    *   таймер прикручивать правда придется
    *   голосовалка вроде есть уже, я ее вынесу из мафии в родителя
 */
class ThirtyYearsSession(sessionId: Long, sessionPas: Long) :
        GamesSession<ThirtyYearsUser, ThirtyYearsEvent>(sessionId, sessionPas) {

    private val logger = org.apache.log4j.Logger.getLogger(ThirtyYearsSession::class.java!!)

    private val events = ArrayList<String>()

    /**
     * Состояние игры согласно машине состояний.
     */
    private var gameState = GameState.ENTER_REAL_EXCUTE

    /**
     * Время требуемое на написание правдивой отмазки
     */
    val trueAnserTime = 30L*1000L
    /**
     * Время требуемое на написание ложной отмазки
     */
    val lyingAnserTime = 30L*1000L

    /**
     * Игдекс пользователя который отмазывается в данный момент времени.
     */
    private var excuteThirtyYearsUserIndex = 0

    init {
        updateEvents()
    }

    override fun startGame()
    {
        if(!started)
        {
            started = true
            logger.info("startGame()...")
            //обновляем список локаций
            updateEvents()
            //Присвоить каждому пользователю локацию.
            generateEvents()
            //отмазывается 0 пользователь в списке
            users[0].isExcuting = true
        }
    }

    /**
     * Ввести честную отмазку.
     */
    fun setRealExcute(userName: String, excute: String):Boolean
    {
        logger.info("setRealExcute($userName, $excute)")
        getUser(userName).excute = excute
        return true
    }

    /**
     * Ввести фальшивую отмазку.
     */
    fun setFalshExcute(userName: String, falshExcute: String):Boolean
    {
        logger.info("setFalshExcute($userName, $falshExcute)")
        getUser(userName).falshExcute = falshExcute
        return true
    }

    /**
     * Произоводит обновление в соответсвии с машиной состояния.
     * (ну и рассылка евентов соответсвенно)
     */
    private fun updateByStateMashine()
    {
        if(gameState == GameState.ENTER_REAL_EXCUTE)
        {

        }
        else if(gameState == GameState.ENTER_FALSH_EXCUTE)
        {

        }
        else if(gameState == GameState.VOTE)
        {

        }
    }



    /********GAME EVENTS*********/

    /**
     * Присвоить каждому пользователю локацию.
     */
    private fun generateEvents()
    {
        users.forEach {
            val eventIndex = GlobalRandomiser.getRundom(events.size)
            it.event = events[eventIndex]
            if(events.size!=1)
            {
                events.removeAt(eventIndex)
            }
        }
    }



    /**
     * Обновить список мероприятий из БД.
     */
    @Synchronized
    private fun updateEvents() : Boolean{
        logger.info("updateEvents()")
        events.clear()
        Globals.thirtyYearsEventManager.getAllEventsAsString().forEach { events.add(it) }
        return true
    }
}