package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.Globals
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteInformation
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteVariants
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
     * Проголосовать
     * @param userName имя голосующего пользователя
     * @param anser стринговый ответ за который голосует пользователь
     */
    fun vote(userName: String, anser: String): Boolean
    {
        logger.info("vote($userName, $anser)")
        var voteName = ""
        users.forEach {
            if(it.falshExcute==anser || it.excute==anser)
            {
                voteName=it.name
            }
        }
        countThirtyYearsUserVote++
        return super.gameSessionVote.vote(userName, voteName)
    }

    /**
     * Ввести честную отмазку.
     */
    fun setRealExcute(userName: String, excute: String):Boolean
    {
        logger.info("setRealExcute($userName, $excute)")
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
        logger.info("setFalshExcute($userName, $falshExcute)")
        getUser(userName).falshExcute = falshExcute
        countThirtyYearsUserExcute++
        updateByStateMashine()
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
            if(indexThirtyYearsUserExcute>= users.size)
            {
                countThirtyYearsUserExcute = 0
                goTo_ENTER_FALSH_EXCUTE_event()
            }
        }
        else if(gameState == GameState.ENTER_FALSH_EXCUTE)
        {
            if(indexThirtyYearsUserExcute>= users.size-1)
            {
                countThirtyYearsUserExcute = 0
                goTo_VOTE_event()
            }
        }
        else if(gameState == GameState.VOTE)
        {
            //Обновляет очки после голосования
            updatePoints(users[indexThirtyYearsUserExcute].name)
            if(countThirtyYearsUserVote <users.size) {
                goTo_SHOW_RESULTS_event()
            }
            else
                goTo_SHOW_FINAL_RESULTS_event()
        }
        else if(gameState == GameState.SHOW_RESULTS)
        {

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
                    user.points = user.points + user.gameUserVote.votedCount
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
                        points++
                }
                user.points = points
            }
        }
        myVoteTrue()
        myVotesCount()
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
            val table = ThirtyYearsVoteVariants(getUser(it.userName),
                    users[indexThirtyYearsUserExcute],users).toJson()
            it.ENTER_FALSH_EXCUTE_event(table)
        }
    }
    /**
     * Событие перевода в статус голосования.
     */
    private fun goTo_VOTE_event()
    {
        gameState = GameState.VOTE
        gameEvent.forEach { it.VOTE_event(
                it.userName == users[this.indexThirtyYearsUserExcute].name
        ) }
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
        indexThirtyYearsUserExcute++
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
        indexThirtyYearsUserExcute++
        gameSessionVote.clear()
    }

}