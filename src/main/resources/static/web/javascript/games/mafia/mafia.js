/**
 * Изменяет состояние окна.
 * (приводит его в состояние соответсвующее текущей таблицы состояния)
 */
let ws = new WindowStates()
/**
 * Рисует таблицу.
 */
let vtg =new VoteTableGenerator()

/**********WEB SOCKETS*******/

var SEPORATOR = "_"
var mafiaWsConnectionUri = "ws://" + document.location.host +"/games/mafia/ws";
var mafiaWebsocketConnection = new WebSocket(mafiaWsConnectionUri);
mafiaWebsocketConnection.onerror = function(evt) { mafia_onConnectionError(evt) };
mafiaWebsocketConnection.onopen = function(evt) { mafia_onConnectionOpen(evt) };
mafiaWebsocketConnection.onmessage = function(evt) { mafia_onConnectionMessage(evt) };
mafiaWebsocketConnection.onclose = function(evt) { mafia_onclose(evt) };

function mafia_onConnectionError(evt) {
    //alert("Ошибка соединения с сервером после востановления соединения !!!перезагрузите!!! страницу и войдите в игру (Шпион) указав " +
    //    "свой ID сессии пароль сессии и логин.")
    console.log("Connection error.")
}

function mafia_onConnectionOpen() {
    console.log("mafia_onConnectionOpen()")
    mafiaWebsocketConnection.send("ping")
    //var userName = document.getElementById("mafia_userName").value
    //if(userName.length>0) {
    //    mafia_addUser()
    //}
}
function mafia_onclose() {
    console.log("mafia_onclose()")
    mafiaWebsocketConnection = new WebSocket(mafiaWsConnectionUri);
    mafiaWebsocketConnection.onerror = function(evt) { mafia_onConnectionError(evt) };
    mafiaWebsocketConnection.onopen = function(evt) { mafia_onConnectionOpen(evt) };
    mafiaWebsocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
    mafiaWebsocketConnection.onclose = function(evt) { mafia_onclose(evt) };

}

function mafia_onConnectionMessage(evt) {
    console.log("received: " + evt.data);
    var command = evt.data.split(SEPORATOR )[0]
    var data = evt.data.split(SEPORATOR )[1]
    //DATA...
    if(command=="addUserEvent") {
        mafiaWebsocketConnection.send("ok_"+document.getElementById("mafia_userName").value)
        document.getElementById("mafia_users").textContent = data

    }
    else if(command=="leaderChandged") {
        mafiaWebsocketConnection.send("ok")
        document.getElementById("mafia_leader").textContent ="Ведущий: " +   data
    }
    else if(command=="updateVoteTable") {
        mafiaWebsocketConnection.send("ok")

        document.getElementById("mafia_userVoteTable").innerHTML = vtg.generate(data)
    }
    //STATES...
    else if(command=="startGameEvent") {
        mafiaWebsocketConnection.send("ok")
        updateWindowByState("CITIZEN_VOTE")
    }
    else if(command=="stopGameEvent") {
        mafiaWebsocketConnection.send("ok")
        alert("Игра закончена!")
        updateWindowByState("STOP_GAME")
    }
    else if(command=="openMafiaVote") {
        mafiaWebsocketConnection.send("ok")
        if(data!="")
            alert("Игрок:" + data + " мертв!")
        else
            alert("К сожалению все живы.")
        mafia_getSheriffCheckVariants()
        updateWindowByState("MAFIA_VOTE")

    }
    else if(command=="openСitizensVote") {
        mafiaWebsocketConnection.send("ok")
        if(data!="")
            alert("Игрок:" + data + " мертв!")
        else
            alert("К сожалению все живы.")
        //mafia_checkUserSheriff()
        updateWindowByState("CITIZEN_VOTE")
    }
    else if(command=="pong")
    {
        var userName = document.getElementById("mafia_userName").value
        if(userName.length>0) {
            mafia_login()
        }
    }
    else if(command=="sheriffCheckedUser")//КОЛЯ ЗДЕСЬ!!!
    {
        alert("Шериф выбрал для проверки  " + data)
    }
}


/******PUBLIC******/

function mafia_startGame(){
    console.log("mafia_startGame()")
    var count = mafia_request("mafia_count_users")
    if(count<5)
    {
        alert("Минимальное количество игроков 5!")
        return
    }
    mafia_request("mafia_startGame")
}


function mafia_stopGame(){
    console.log("mafia_stopGame()")
    mafia_request("mafia_stopGame")
}



/**
 * Добавить пользователя.
 * (создает сессию если таковой нет и добавляет пользователя)
 * (применяется в том числе для для реконекта)
 */
function mafia_login(){
    console.log("mafia_login()")
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
    createSessionIfNotExists()
    //Add user
    ans = addUserIfNotExist()
    if(ans=="true")
    {
        var gameState = mafia_getGameState()
        updateWindowByState(gameState)
    }
    else
    {
        alert(ans)
    }

}

function mafia_wanToBeaLeader()
{
    console.log("mafia_wanToBeaLeader()")
    mafia_request("mafia_becomeALeader")
}

/**
 * Завершить шолосование горожан
 */
function mafia_citizenVote() {
    console.log("mafia_citizenVote()")
    //mafia_getSheriffCheckVariants()
    mafia_request("mafia_getCitizenVoteResult")
}

/**
 * Завершить голосование мафиозе
 */
function mafia_mafiaVote() {
    console.log("mafia_mafiaVote()")
    //mafia_checkUserSheriff()
    mafia_request("mafia_getMafiaVoteResult")
}

/**
 * Отдать голос
 */
function mafia_voteVote() {
    console.log("mafia_voteVote()")
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
 * Шериф проверяет игрока.
 */
function mafia_selectCheckUserSheriff() {
    console.log("GET TX: mafia_checkUserSheriff")
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    var checkUser = document.getElementById("mafia_checkUserSheriffVariants").value
    if(checkUser.length>0) {
        xmlHttp.open("GET", "/games/mafia_selectCheckUserSheriff?userName=" + userName + "&sessionId=" + sessionId +
            "&sessionPas=" + sessionPas + "&checkedUserName=" + checkUser, false); // false for synchronous request
        xmlHttp.send(null);
        console.log("GET RX: " + xmlHttp.responseText)
        return xmlHttp.responseText
    }
}
/******PRIVATE STATES******/

/**
 * Обновляет окно пользвателя в соответсвии с состоянием.
 * @param gameState
 */
function updateWindowByState(gameState) {
    if(gameState == "ADD_USERS") {
        ws.beforGamePosition()
        mafia_getLeader()
    }
    else if(gameState == "CITIZEN_VOTE")
    {
        ws.gamePositionCitizenVote()
        mafia_getСitizenVoteVariants()
        mafia_updateWindowByRole()
        mafia_startGame()
    }
    else if(gameState == "MAFIA_VOTE")
    {
        ws.gamePositionMafiaVote()
        mafia_getMafiaVoteVariants()
        mafia_updateWindowByRole()
        mafia_startGame()
    }
    else if(gameState = "STOP_GAME")
    {
        ws.stopGamePosition()
    }
}

/**
 * Получить состояние в котором сейчас находится игра.
 * добавление пользователей ADD_USERS
 * голосование города CITIZEN_VOTE
 * голосование мафии MAFIA_VOTE
 */
function mafia_getGameState() {
    return mafia_request("mafia_getGameState")
}

/**
 * Приводит окно к виду соответсвующему роли данного игрока в игре мафия.
 * пр. показывает кнопку "Завершить голосование"  лидеру и прячет ее у мафии и горожан.
 */
function mafia_updateWindowByRole()
{

    var role = mafia_request("mafia_getRole")
    vtg.role = role
    //прописываем роль пользователю
    document.getElementById("mafia_role").textContent = "Роль: " +  role
    //если пользователь не лидер убираем кнопку голосования
    if( role=="LEADING")
    {
        ws.leaderPosition()
    }
    else if(role=="CITIZEN")
    {
        ws.citizenPosition()
    }
    else if(role=="MAFIA")
    {
        ws.mafiaPosition()
    }
    else if(role=="SHERIFF")
    {
        ws.sheriffPosition()
    }
}

/******PRIVATE******/

function addUserIfNotExist() {
    return mafia_request("mafia_addUser")
}

function createSessionIfNotExists() {
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    var ans = mafia_request("mafia_add_session")
    if(ans=="true") {
        mafiaWebsocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
        //lert("Игра создана.")
    }
    else if(ans=="false")
    {
        mafiaWebsocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
        //alert("Ничего не делаем.")
    }
    else
    {

        alert(ans)
        return
    }
}


function mafia_getLeader()
{
    var ans = mafia_request("mafia_getLeader")
    document.getElementById("mafia_leader").textContent = "Ведущий: " +  ans
}


/**
 * Получить варианты за кого можно проголосовать.
 * (список кандидатов)
 */
function mafia_getMafiaVoteVariants()
{
    var voteVariants =  mafia_request("mafia_getUsersForVoteMafia").split(SEPORATOR)
    document.getElementById("mafia_voteVariants").innerHTML ="<option></option>"
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
    var voteVariants =  mafia_request("mafia_getUsersForVoteСitizen").split(SEPORATOR)
    document.getElementById("mafia_voteVariants").innerHTML ="<option></option>"
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
function mafia_getSheriffCheckVariants()
{
    var variants =  mafia_request("mafia_getCheckUserSheriffVariants").split(SEPORATOR)
    document.getElementById("mafia_checkUserSheriffVariants").innerHTML ="<option></option>"
    for(var i=0; i< variants.length; i=i+1) {
        if(variants[i].length>0)
            document.getElementById("mafia_checkUserSheriffVariants").innerHTML =
                document.getElementById("mafia_checkUserSheriffVariants").innerHTML + "<option>" + variants[i] + "</option>"
    }
}



function mafia_request(command) {
    console.log("GET TX: " + command)
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/"+command+"?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    console.log("GET RX: " + xmlHttp.responseText)
    return xmlHttp.responseText

}
