package ru.parcel.sitemqtt.games.gamesession.thirtyyears

import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteInformation
import com.parcel.tools.games.games.thirtyyears.comunicateinformation.ThirtyYearsVoteVariants
import com.parcel.tools.web.websockets.games.thirtyyears.Commands
import com.parcel.tools.web.websockets.games.thirtyyears.json.ThirtyYearsMessage
import ru.parcel.sitemqtt.websocket.WebSocketMessageInterface
import ru.parcel.sitemqtt.websocket.WebsocketClientEndpoint

/**
 * Клас WEB страницы.
 */
class WebPage(val sessionId: Long,val sessionPass: Long,val name: String)
{

    class InRequestEvent(private  var webPage: WebPage):WebSocketMessageInterface()
    {

        override fun handleMessage(message: ThirtyYearsMessage) {
            parseInMessage(message)
            message.isAnserOnRequest=true
            message.data = ""
            sendAnser(message)
        }

        private fun parseInMessage(message: ThirtyYearsMessage)
        {
            if(message.command==Commands.START_GAME)
            {
                //webPage.webPageState=WebPageStates.START_GAME

            }
            else if(message.command==Commands.ENTER_REAL_EXCUTE_EVENT)
            {
                webPage.webPageState=WebPageStates.ENTER_REAL_EXCUTE_EVENT
                webPage.myEvent = message.data
            }
            else if(message.command==Commands.ENTER_FALSH_EXCUTE_EVENT)
            {
                webPage.round()
                webPage.currentEvent = message.data
                webPage.webPageState=WebPageStates.ENTER_FALSH_EXCUTE_EVENT
            }

            else if(message.command==Commands.VOTE_EVENT)
            {
                webPage.webPageState=WebPageStates.VOTE_EVENT
                webPage.voteVariants.fromJson(message.data)
            }
            else if(message.command==Commands.SHOW_FINAL_RESULTS_EVENT)
            {
                webPage.webPageState=WebPageStates.SHOW_FINAL_RESULTS_EVENT

            }
            else if(message.command==Commands.SHOW_RESULTS_EVENT)
            {
                webPage.webPageState=WebPageStates.SHOW_RESULTS_EVENT
                webPage.voteInformation.fromJson(message.data)
            }

            else if(message.command==Commands.ADD_USER)
            {
                webPage.users = message.data
            }
        }

    }//class




    private var webSocketClient = WebsocketClientEndpoint("ws://127.0.0.1:8080/games/thirtyyears/ws")


    /**
     * Список игроков.
     */
    var users = ""

    /**
     * Событие от которого отмазывается пользователь
     */
    var myEvent = ""

    /**
     * Отмазка пользователя
     */
    var myExcude = ""

    /**
     * Событие от которого все отмазываются сейчас
     */
    var currentEvent = ""
    /**
     * Отмазка от текущего события
     */
    var currentExcude = ""


    /**
     * Варианты за которые можно проголосовать
     */
    var voteVariants = ThirtyYearsVoteVariants()

    /**
     * Информация о текущих результатах пользователей.
     */
    var voteInformation = ThirtyYearsVoteInformation()

    /**
     * Текущее состояние страницы.
     */
    var webPageState = WebPageStates.ADD_USER

    private val inRequestEvent = InRequestEvent(this)


    //сначало вызывается инит
    init {
        webSocketClient.addMessageHandler(inRequestEvent)
    }
    //затем конструкор
    constructor(sessionId: Long,sessionPass: Long,name: String,webSocketClient: WebsocketClientEndpoint)
            :this(sessionId,sessionPass,name)
    {
        this.webSocketClient = webSocketClient
        this.webSocketClient.addMessageHandler(inRequestEvent)
    }



    /**
     * Добавить пользователя.
     */
    fun connect()
    {
        request(Commands.CONNECT)
    }

    /**
     * Добавить пользователя.
     */
    fun addUser()
    {
        request(Commands.ADD_USER)
    }

    /**
     * Начать игру
     */
    fun startGame()
    {
        request(Commands.START_GAME)
    }

    /**
     * Остановить игру.
     */
    fun stopGame()
    {
        request(Commands.STOP_GAME)
    }

    /**
     * Ввести реальную отмазку.
     */
    fun setRealExcude(excude: String)
    {
        myExcude = excude
        request(Commands.SET_REAL_EXCUTE, excude)
    }

    /**
     * Ввести фальшивую отмазку.
     */
    fun setFalshExcude(excude: String)
    {
        currentExcude = excude
        request(Commands.SET_FALSH_EXCUTE, excude)
    }

    /**
     * Проголосовать.
     */
    fun setVote(anser: String)
    {
        request(Commands.SET_VOTE, anser)
    }
    /**
     * Завершить раунд
     */
    fun setRound()
    {
        request(Commands.ROUND)
    }



    /**
     * Возвращяет строку таблицы данных о страницы для
     * отчета по тестам.
     */
    fun getDataRow(): Array<String?>
    {
        return arrayOf(name, webPageState.toString(), myEvent, myExcude, currentEvent, currentExcude)
    }


    /**
     * Методл вызывается после перехода в состояние ENTER_FALSH_EXCUTE (конец раунда)
     * применяется для отчистки ВЕБ страницы от информации от прошлого раунда.
     */
    private fun round()
    {
        currentExcude = ""
    }


    /**
     * Отправляю я запрос "разрази меня понос"
     */
    private fun request(command: Commands, data: String = ""): ThirtyYearsMessage
    {
        val message = ThirtyYearsMessage(sessionId, sessionPass, name)
        message.data = data
        message.command = command
        return webSocketClient.sendRequest(message)
    }

}