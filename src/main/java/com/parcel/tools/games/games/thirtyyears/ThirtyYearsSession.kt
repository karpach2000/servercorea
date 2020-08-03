package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.Globals
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteInformation
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteVariants
import com.parcel.tools.games.games.thirtyyears.settings.ThirtyYearsSettings
import com.parcel.tools.games.gamesession.GamesSession

class ThirtyYearsSessionException(message: String):Exception(message)
class ThirtyYearsSessionNotFatalException(message: String):Exception(message)
{
    constructor(thirtyYearsErrors: ThirtyYearsErrors):this(thirtyYearsErrors.toString())
}

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
    private var gameState = GameState.ADDING_USERS

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

    /**
     * Обработчик событий таймера.
     */
    private val thirtyYearsTimerEvent = ThirtyYearsTimerEvent(this)

    init {
        updateEvents()
        //после всех тестов  обновление перенесем в старт программы
        ThirtyYearsSettings.update()
        //подписываемся
        gameSessionTimer.subscribeTimerEvents(thirtyYearsTimerEvent)
    }

    override fun addUser(name: String ): Boolean {
       val user = ThirtyYearsUser(name)
        return super.addUser(user)
    }

    override fun stopGame():Boolean
    {

        //logger.debug("stopGame()...")
        goTo_STOP_GAME()
        return super.stopGame()
    }


    override fun startGame()
    {

        if(!started && users.count()>=ThirtyYearsSettings.points.usersMin)
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
        else if(users.count()<ThirtyYearsSettings.points.usersMin)
        {
            logger.warn("REQUIRED_USERS_${ThirtyYearsSettings.points.usersMin}_HAVE_${users.count()}")
            throw ThirtyYearsSessionNotFatalException("${ThirtyYearsErrors.REQUIRED_USERS}_${ThirtyYearsSettings.points.usersMin}")
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

        //аходим пользователя, которому принадлежит ответ
        users.forEach {
            if(it.falshExcute==anser || it.excute==anser)//AHTUNG BAG AND COSTILE
            {
                super.gameSessionVote.vote(userName, it.name!!)
            }
        }
        countThirtyYearsUserVote++
        updateByStateMashine()
        return true
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
        if(!getUser(userName).isExcuting) {
            getUser(userName).falshExcute = falshExcute
            countThirtyYearsUserExcute++
            updateByStateMashine()
        }

        else
        {
            val errorMessage = "${ThirtyYearsErrors.REQUIRED_USERS} This player cannot enter a fake excuse as he is currently excuse"
            logger.error(errorMessage)
            throw ThirtyYearsSessionNotFatalException(errorMessage)
        }

        return true
    }

    /**
     * Закончить раунд.
     */
    fun round(): Boolean
    {
        logger.debug("round()")

        if(gameState == GameState.SHOW_RESULTS)
        {
            users[indexThirtyYearsUserExcute].isExcuting = false
            indexThirtyYearsUserExcute++
            users[indexThirtyYearsUserExcute].isExcuting = true
            users.forEach { it.clear() }
            goTo_ENTER_FALSH_EXCUTE_event()
            return true
        }
        else if(gameState == GameState.SHOW_FINAL_RESULTS)
        {
            goTo_STOP_GAME()
            return true
        }
        return false


    }

    /**
     * Произоводит обновление в соответсвии с машиной состояния.
     * (ну и рассылка евентов соответсвенно)
     */
    fun updateByStateMashine()
    {


        if(gameState == GameState.ENTER_REAL_EXCUTE)
        {
            if(countThirtyYearsUserExcute >= users.size || gameSessionTimer.checkTimer())
            {
                gameSessionTimer.stopTimer()
                countThirtyYearsUserExcute = 0
                goTo_ENTER_FALSH_EXCUTE_event()
            }
        }
        else if(gameState == GameState.ENTER_FALSH_EXCUTE)
        {
            if(countThirtyYearsUserExcute >= users.size - 1 || gameSessionTimer.checkTimer())
            {
                gameSessionTimer.stopTimer()
                countThirtyYearsUserExcute = 0
                goTo_VOTE_event()
            }
        }
        else if(gameState == GameState.VOTE)
        {
            //Обновляет очки после голосования

            //если все проголосовали
            if(countThirtyYearsUserVote>=users.size-1 || gameSessionTimer.checkTimer()) {
                updatePoints(users[indexThirtyYearsUserExcute].name!!)
                gameSessionTimer.stopTimer()
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
                    user.points = user.points + ThirtyYearsSettings.points.selectedTrueTellerPoints
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
                        points = points + ThirtyYearsSettings.points.somewoneVotedYouPoints
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

    /********REQUESTS*******/
    /**
     * Возвращает игроку текущий статус игры.
     * Плюс создает евент досылающий информацию ор текущем состоянии страницы.
     */
    fun getGameStatus(userName: String) : String
    {
        getGameEvents(userName).addUserEvent(getAllUsers())
        when(gameState)
        {

            GameState.ENTER_FALSH_EXCUTE ->
            {
                val data = getUser(userName).getThirtyYearsEventAndUserInformation().toJson()
                getGameEvents(userName).ENTER_FALSH_EXCUTE_event(data)
            }
            GameState.SHOW_FINAL_RESULTS -> {
                val data = ThirtyYearsVoteInformation(getUser(userName),
                        users[indexThirtyYearsUserExcute],users).toJson()
                getGameEvents(userName).SHOW_FINAL_RESULTS_event(data)
            }
            GameState.ENTER_REAL_EXCUTE -> {
                val data = ThirtyYearsVoteInformation(getUser(userName),
                        users[indexThirtyYearsUserExcute],users).toJson()
                getGameEvents(userName).ENTER_REAL_EXCUTE_event(data)
            }
            GameState.SHOW_RESULTS -> {
                val data = ThirtyYearsVoteInformation(getUser(userName),
                        users[indexThirtyYearsUserExcute],users).toJson()
                getGameEvents(userName).SHOW_RESULTS_event(data)
            }
            GameState.VOTE -> {
                val data = ThirtyYearsVoteVariants(getUser(userName),
                        users[indexThirtyYearsUserExcute],users).toJson()
                getGameEvents(userName).VOTE_event(data)
            }

        }
        return gameState.toString()
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
        gameSessionTimer.stopTimer()
        gameSessionTimer.startTimer(ThirtyYearsSettings.points.ENTER_REAL_EXCUTE_time)
    }
    /**
     * Событие перевода в статус введение фальшивой отмазки.
     */
    private fun goTo_ENTER_FALSH_EXCUTE_event()
    {
        gameState = GameState.ENTER_FALSH_EXCUTE
        users.forEach { it.falshExcute = "Пользователь отправил прекрасное нихуя!" }//чистим буфер с фальшивыми отмазками.
        gameEvent.forEach {
            it.ENTER_FALSH_EXCUTE_event(users[indexThirtyYearsUserExcute].getThirtyYearsEventAndUserInformation().toJson())
        }
        gameSessionTimer.stopTimer()
        gameSessionTimer.startTimer(ThirtyYearsSettings.points.ENTER_FALSH_EXCUTE_time)
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
        gameSessionTimer.stopTimer()
        gameSessionTimer.startTimer(ThirtyYearsSettings.points.VOTE_time)
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

    private fun goTo_STOP_GAME()
    {
        gameEvent.forEach { it.STOP_GAME_event() }
    }

}