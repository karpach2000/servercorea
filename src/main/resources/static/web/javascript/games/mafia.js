/********VARS***********/
var role = ""


/**********WEB SOCKETS*******/

var SEPORATOR = "_"
var mafiaWsConnectionUri = "ws://" + document.location.host +"/games/mafia";
var mafiaWebsocketConnection = new WebSocket(mafiaWsConnectionUri);
mafiaWebsocketConnection.onerror = function(evt) { mafia_onConnectionError(evt) };
mafiaWebsocketConnection.onopen = function(evt) { mafia_onConnectionOpen(evt) };
mafiaWebsocketConnection.onmessage = function(evt) { mafia_onConnectionMessage(evt) };
mafiaWebsocketConnection.onclose = function(evt) { mafia_onclose(evt) };

function mafia_onConnectionError(evt) {
    alert("Ошибка соединения с сервером после востановления соединения !!!перезагрузите!!! страницу и войдите в игру (Шпион) указав " +
        "свой ID сессии пароль сессии и логин.")
}

function mafia_onConnectionOpen() {
    mafiaWebsocketConnection.send("ping")
    var userName = document.getElementById("mafia_userName").value
    if(userName!="")
        mafia_addUser()
}
function mafia_onclose() {
    //websocketConnection.close()
    mafiaWebsocketConnection = new WebSocket(mafiaWsConnectionUri);
    mafiaWebsocketConnection.onerror = function(evt) { onConnectionError(evt) };
    mafiaWebsocketConnection.onopen = function(evt) { onConnectionOpen(evt) };
    mafiaWebsocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
    mafiaWebsocketConnection.onclose = function(evt) { onclose(evt) };
}

function mafia_onConnectionMessage(evt) {
    console.log("received: " + evt.data);
    var command = evt.data.split(SEPORATOR )[0]
    var data = evt.data.split(SEPORATOR )[1]
    if(command=="addUserEvent") {
        mafiaWebsocketConnection.send("ok_"+document.getElementById("mafia_userName").value)
        document.getElementById("mafia_users").textContent = data

    }
    else if(command=="startGameEvent") {
        mafiaWebsocketConnection.send("ok")
        mafia_startGame()
    }
    else if(command=="stopGameEvent") {
        mafiaWebsocketConnection.send("ok")
        alert("Игра закончена!")
        mafia_stopGamePosition()

    }
    else if(command=="leaderChandged") {
        mafiaWebsocketConnection.send("ok")
        document.getElementById("mafia_leader").textContent ="Ведущий: " +   data
    }
    else if(command=="updateVoteTable") {
        mafiaWebsocketConnection.send("ok")
        document.getElementById("mafia_userVoteTable").innerHTML = data
    }


    else if(command=="openMafiaVote") {
        mafiaWebsocketConnection.send("ok")
        alert("Игрок:" + data + " мертв!")
        mafia_gamePositionMafiaVote()
        mafia_getMafiaVoteVariants()

    }
    else if(command=="openСitizensVote") {
        mafiaWebsocketConnection.send("ok")
        alert("Игрок:" + data + " мертв!")
        mafia_gamePositionCitizenVote()
        mafia_getСitizenVoteVariants()
    }



}

/*****POSITIONS*****/

function mafia_stopGamePosition() {
    document.getElementById("mafia_user").hidden = false
    document.getElementById("mafia_beforGame").hidden = true
    document.getElementById("mafia_game").hidden = true
}
function mafia_beforGamePosition() {
    document.getElementById("mafia_user").hidden = true
    document.getElementById("mafia_beforGame").hidden = false
    document.getElementById("mafia_game").hidden = true
}
function mafia_gamePositionCitizenVote() {
    document.getElementById("mafia_user").hidden = true
    document.getElementById("mafia_beforGame").hidden = true
    document.getElementById("mafia_game").hidden = false
    document.getElementById("mafia_citizenVote").hidden = false
    document.getElementById("mafia_mafiaVote").hidden = true
}
function mafia_gamePositionMafiaVote() {
    document.getElementById("mafia_user").hidden = true
    document.getElementById("mafia_beforGame").hidden = true
    document.getElementById("mafia_game").hidden = false
    document.getElementById("mafia_citizenVote").hidden = true
    document.getElementById("mafia_mafiaVote").hidden = false
}
/******GAME******/
function mafia_startGame(){
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_count_users?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if(xmlHttp.responseText<3)
    {
        alert("Минимальное количество игроков 5!")
        return
    }
    mafia_gamePositionCitizenVote()
    mafia_startGameCommand()
    mafia_getRole()
    mafia_getСitizenVoteVariants()
    //alert(xmlHttp.responseText)

}

/**
 *Отдает команду на запуск игры. В не зависимости от того была ли игра уже запущена, возвращает
 * и выводет на страницу таблицу пользователей.
 */
function mafia_startGameCommand() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_startGame?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null)
    document.getElementById("mafia_userVoteTable").innerHTML = xmlHttp.responseText
}

function mafia_stopGame(){
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_stopGame?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
}

/*******USERS*******/

/**
 * Добавить пользователя.
 * (создает сессию если таковой нет и добавляет пользователя)
 * (применяется в том числе для для реконекта)
 */
function mafia_addUser(){
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value

    if(userName == "")
    {
        alert("Заполните поле \"Имя пользователя\"")
        return
    }
    else if(sessionId == "")
    {
        alert("Заполните поле \"ID сессии\"")
        return
    }
    else if(sessionPas == "")
    {
        alert("Заполните поле \"Пароль сессии\"")
        return
    }

    //create session
    xmlHttp.open("GET", "/games/mafia_add_session?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if(xmlHttp.responseText=="true") {
        mafiaWebsocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
        //lert("Игра создана.")
    }
    else if(xmlHttp.responseText=="false")
    {
        mafiaWebsocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
        //alert("Ничего не делаем.")
    }
    else
    {

        alert(xmlHttp.responseText)
        return
    }


    //Add user
    xmlHttp.open("GET", "/games/mafia_addUser?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if(xmlHttp.responseText=="true")
    {
        var gameState = mafia_getGameState()
        if(gameState == "ADD_USERS") {
            mafia_beforGamePosition()
            mafia_getUsers()
            mafia_getLeader()
        }
        else if(gameState == "CITIZEN_VOTE")
        {
            mafia_gamePositionCitizenVote()
            mafia_getСitizenVoteVariants()
            //mafia_voteVote()//применяем голосование за пустую ячкйку для получения таблицы
            mafia_getRole()
            mafia_startGameCommand()
        }
        else if(gameState == "MAFIA_VOTE")
        {
            mafia_gamePositionMafiaVote()
            mafia_getMafiaVoteVariants()
            //mafia_voteVote()//применяем голосование за пустую ячейку для получения таблицы
            mafia_getRole()
            mafia_startGameCommand()
        }
    }
    else
    {
        alert(xmlHttp.responseText)
    }

}

/**
 * Получить состояние в котором сейчас находится игра.
 * добавление пользователей ADD_USERS
 * голосование города CITIZEN_VOTE
 * голосование мафии MAFIA_VOTE
 */
function mafia_getGameState() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getGameState?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    return xmlHttp.responseText
}

function mafia_getUsers()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getUsers?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    document.getElementById("mafia_users").textContent = xmlHttp.responseText
}

function mafia_getRole()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getRole?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    var role = xmlHttp.responseText
    //прописываем роль пользователю
    document.getElementById("mafia_role").textContent = "Роль: " +  xmlHttp.responseText
    //если пользователь не лидер убираем кнопку голосования
    if( xmlHttp.responseText=="LEADING")
    {
        document.getElementById("mafia_voteСitizenButton").hidden = false
        document.getElementById("mafia_voteMafiaButton").hidden = false
        document.getElementById("mafia_voteVariants").hidden = true

    }
    else
    {
        document.getElementById("mafia_voteСitizenButton").hidden = true
        document.getElementById("mafia_voteMafiaButton").hidden = true
    }
}

/*********LEADER********/

function mafia_wanToBeaLeader()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_becomeALeader?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if(xmlHttp.responseText!="true")
    {
        alert(xmlHttp.responseText)
    }
    else
    {
        document.getElementById("mafia_leader").textContent = "Ведущий: " +   userName
    }
}

function mafia_getLeader()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getLeader?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    document.getElementById("mafia_leader").textContent = "Ведущий: " +  xmlHttp.responseText
}


/*********VOTE***********/
/**
 * Отдать голоса
 */
function mafia_voteVote() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    var vote = document.getElementById("mafia_voteVariants").value
    xmlHttp.open("GET", "/games/mafia_voteVote?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas+"&vote="+vote, false); // false for synchronous request
    xmlHttp.send(null);
}

/**
 * Завершить шолосование горожан
 */
function mafia_citizenVote() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getCitizenVoteResult?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
}

/**
 * Завершить голосование мафиозе
 */
function mafia_mafiaVote() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getMafiaVoteResult?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
}


/**
 * Получить варианты за кого можно проголосовать.
 * (список кандидатов)
 */
function mafia_getMafiaVoteVariants()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getUsersForVoteMafia?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    //комбобокс прописываем
    var voteVariants = xmlHttp.responseText.split(SEPORATOR)
    document.getElementById("mafia_voteVariants").innerHTML =""
    document.getElementById("mafia_voteVariants").innerHTML =
        document.getElementById("mafia_voteVariants").innerHTML + "<option></option>"
    for(var i=0; i< voteVariants.length; i=i+1) {
        if(voteVariants[i].length>0)
            document.getElementById("mafia_voteVariants").innerHTML =
                document.getElementById("mafia_voteVariants").innerHTML + "<option>" + voteVariants[i] + "</option>"
    }
}

/**
 * Получить варианты за кого можно проголосовать когда голосует город.
 * (список кандидатов)
 */
function mafia_getСitizenVoteVariants()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getUsersForVoteСitizen?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    //комбобокс прописываем
    var voteVariants = xmlHttp.responseText.split(SEPORATOR)
    document.getElementById("mafia_voteVariants").innerHTML ="<option></option>"
    for(var i=0; i< voteVariants.length; i=i+1) {
        if(voteVariants[i].length>0)
            document.getElementById("mafia_voteVariants").innerHTML =
                document.getElementById("mafia_voteVariants").innerHTML + "<option>" + voteVariants[i] + "</option>"
    }
}


