package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.Globals
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteInformation
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteVariants
import com.parcel.tools.games.games.thirtyyears.settings.ThirtyYearsSettings
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
    private var indexThirtyYearsUserExcute = 0
    /**
     * Количество пользователей которое проголосовало.
     */
    private var countThirtyYearsUserVote = 0
    /**
     * Количество пользователей которое написавшее отмазку.
     */
    private var countThirtyYearsUserExcute = 0

    init {
        updateEvents()
        //после всех тестов  обновление перенесем в старт программы
        ThirtyYearsSettings.update()
    }

    override fun addUser(name: String): Boolean {
       val user = ThirtyYearsUser(name)
        return super.addUser(user)
    }

    override fun startGame()
    {
        if(!started)
        {
            started = true
            logger.debug("startGame()...")
            //обновляем список локаций
            updateEvents()
            //Присвоить каждому пользователю локацию.
            generateEvents()
            //отмазывается 0 пользователь в списке
            users[0].isExcuting = true
            //сообщим всем о радостном событии начала игры
            startGameEvent()
            //преводим игру в состояние введения реальной отмазки
            goTo_ENTER_REAL_EXCUTE_event()
        }
    }



    /**
     * Проголосовать
     * @param userName имя голосующего пользователя
     * @param anser стринговый ответ за который голосует пользователь
     */
    fun vote(userName: String, anser: String): Boolean
    {
        logger.debug("vote($userName, $anser)")

        var voteName = ""
        //аходим пользователя, которому принадлежит ответ
        users.forEach {
            if(it.falshExcute==anser || it.excute==anser)//AHTUNG BAG AND COSTILE
            {
                voteName=it.name
            }
        }
        val ans = super.gameSessionVote.vote(userName, voteName)
        countThirtyYearsUserVote++
        updateByStateMashine()
        return ans
    }

    /**
     * Ввести честную отмазку.
     */
    fun setRealExcute(userName: String, excute: String):Boolean
    {
        logger.debug("setRealExcute($userName, $excute)")
        getUser(userName).excute = excute
        countThirtyYearsUserExcute++
        updateByStateMashine()
        return true
    }
    /**
     * Ввести фальшивую отмазку.
     */
    fun setFalshExcute(userName: String, falshExcute: String):Boolean
    {
        logger.debug("setFalshExcute($userName, $falshExcute)")
        getUser(userName).falshExcute = falshExcute
        countThirtyYearsUserExcute++
        updateByStateMashine()
        return true
    }

    /**
     * Закончить раунд.
     */
    fun round(): Boolean
    {
        logger.debug("round()")
        users[indexThirtyYearsUserExcute].isExcuting = false
        indexThirtyYearsUserExcute++
        users[indexThirtyYearsUserExcute].isExcuting = true
        users.forEach { it.clear() }
        goTo_ENTER_FALSH_EXCUTE_event()
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
            if(countThirtyYearsUserExcute >= users.size)
            {
                countThirtyYearsUserExcute = 0
                goTo_ENTER_FALSH_EXCUTE_event()
            }
        }
        else if(gameState == GameState.ENTER_FALSH_EXCUTE)
        {
            if(countThirtyYearsUserExcute >= users.size)
            {
                countThirtyYearsUserExcute = 0
                goTo_VOTE_event()
            }
        }
        else if(gameState == GameState.VOTE)
        {
            //Обновляет очки после голосования

            //если все проголосовали
            if(countThirtyYearsUserVote>=users.size-1) {
                updatePoints(users[indexThirtyYearsUserExcute].name)
                if (indexThirtyYearsUserExcute < users.size-1) {
                    goTo_SHOW_RESULTS_event()
                } else
                    goTo_SHOW_FINAL_RESULTS_event()
                countThirtyYearsUserVote=0
            }
        }
        else if(gameState == GameState.SHOW_RESULTS)
        {
            //ничего не делаем, выход из этого состояния происходит от внешней команды ROUND
        }
    }

    /**
     * Обновляет очки после голосования.
     * @param trueTellerName имя пользователя который честно отвечал
     */
    private fun updatePoints(trueTellerName: String)
    {
        /**
         * Считаем правильно ли ты проголосовал.
         */
        fun myVoteTrue() {
            //var pointsCount = 0
            for (user in users) {
                //если проголосавал за правдивый ответ от создателя
                if (user.gameUserVote.voteName == trueTellerName) {
                    user.points = user.points + ThirtyYearsSettings.points.selectedTrueTeller
                } //if
            }//for
        }//fun

        /**
         * считаем проголосовали ли за тебя
         */
        fun myVotesCount()
        {
            for(user in users)
            {
                var points = 0
                users.forEach {
                    if(it.gameUserVote.voteName==user.name)
                        points = points + ThirtyYearsSettings.points.somewoneVotedYou
                }
                user.points = user.points + points
            }
        }

        /**
         * Сбросить очки голосования для всех пользователей
         */
        fun clearPoints()
        {
            users.forEach { it.points = 0 }
        }

        /**
         * Обновить суммарное количество очков набраное пользователем.
         */
        fun updateTotalPoints()
        {
            users.forEach { it.totalPoints = it.totalPoints + it.points }
        }

        clearPoints()
        myVoteTrue()
        myVotesCount()
        updateTotalPoints()
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
        logger.debug("updateEvents()")
        events.clear()
        Globals.thirtyYearsEventManager.getAllEventsAsString().forEach { events.add(it) }
        return true
    }

    /********EVENTS *********/

    //STATE MASHINE

    /**
     * Событие перевода в статус ввведения реальной отмазки.
     */
    private fun goTo_ENTER_REAL_EXCUTE_event()
    {
        gameState = GameState.ENTER_REAL_EXCUTE
        gameEvent.forEach {
            it.ENTER_REAL_EXCUTE_event(getUser(it.userName).event)
        }

    }
    /**
     * Событие перевода в статус введение фальшивой отмазки.
     */
    private fun goTo_ENTER_FALSH_EXCUTE_event()
    {
        gameState = GameState.ENTER_FALSH_EXCUTE
        gameEvent.forEach {
            it.ENTER_FALSH_EXCUTE_event(users[indexThirtyYearsUserExcute].event)
        }
    }
    /**
     * Событие перевода в статус голосования.
     */
    private fun goTo_VOTE_event()
    {
        gameState = GameState.VOTE
        gameEvent.forEach {
            val table = ThirtyYearsVoteVariants(getUser(it.userName),
                    users[indexThirtyYearsUserExcute],users).toJson()
            it.VOTE_event(table)
        }
    }
    /**
     * Событие перевода в статус Показываения пользователю результаты всей игры.
     */
    private fun  goTo_SHOW_FINAL_RESULTS_event()
    {
        gameState = GameState.SHOW_FINAL_RESULTS
        gameEvent.forEach {
            val table = ThirtyYearsVoteInformation(getUser(it.userName),
                    users[indexThirtyYearsUserExcute],users).toJson()
            it.SHOW_FINAL_RESULTS_event(table)

        }
    }
    /**
     * События перевода в статус демонстрации результатов голосования пользователям.
     */
    private fun  goTo_SHOW_RESULTS_event()
    {
        gameState = GameState.SHOW_RESULTS
        gameEvent.forEach {
            val table = ThirtyYearsVoteInformation(getUser(it.userName),
                    users[indexThirtyYearsUserExcute],users).toJson()
            it.SHOW_RESULTS_event(table)

        }
        gameSessionVote.clear()
    }

}