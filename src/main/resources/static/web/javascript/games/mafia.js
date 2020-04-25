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
    alert("Ошибка соединения с сервером, перезагрузите страницу!")
}

function mafia_onConnectionOpen() {
    mafiaWebsocketConnection.send("ping")
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
}

/*****POSITIONS*****/

function mafia_addUserPosition() {
    document.getElementById("mafia_user").hidden = false
    document.getElementById("mafia_beforGame").hidden = true
    document.getElementById("mafia_game").hidden = true
}
function mafia_beforGamePosition() {
    document.getElementById("mafia_user").hidden = true
    document.getElementById("mafia_beforGame").hidden = false
    document.getElementById("mafia_game").hidden = true
}
function mafia_gamePositionUserVote() {
    document.getElementById("mafia_user").hidden = true
    document.getElementById("mafia_beforGame").hidden = true
    document.getElementById("mafia_game").hidden = false
    document.getElementById("mafia_userVote").hidden = false
    document.getElementById("mafia_mafiaVote").hidden = true
}
function mafia_gamePositionMafiaVote() {
    document.getElementById("mafia_user").hidden = true
    document.getElementById("mafia_beforGame").hidden = true
    document.getElementById("mafia_game").hidden = false
    document.getElementById("mafia_userVote").hidden = true
    document.getElementById("mafia_mafiaVote").hidden = false
}
/******GAME******/
function mafia_startGame(){
    getUserAction = false
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

    //вынеси потом этот обзац в отдельный метод
    xmlHttp.open("GET", "/games/mafia_startGame?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    mafia_gamePositionUserVote()
    document.getElementById("mafia_userVoteTable").innerHTML = xmlHttp.responseText


    mafia_getRole()
    mafia_getVoteVariants()
    //alert(xmlHttp.responseText)

}

function mafia_stopGame(){


}

/*******USERS*******/


var getUserAction = false
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
        mafia_beforGamePosition()
        mafia_getUsers()
        mafia_getLeader()
    }
    else
    {
        alert(xmlHttp.responseText)
    }

}

function mafia_getVoteVariants()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("mafia_userName").value
    var sessionId = document.getElementById("mafia_sessionId").value
    var sessionPas = document.getElementById("mafia_sessionPas").value
    xmlHttp.open("GET", "/games/mafia_getCitizenVoteVariants?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    //комбобокс прописываем
    var voteVariants = xmlHttp.responseText.split(SEPORATOR)
    document.getElementById("mafia_voteVariants").innerHTML =""
    for(var i=0; i< voteVariants.length; i=i+1) {
        if(voteVariants[i].length>0)
            document.getElementById("mafia_voteVariants").innerHTML =
                document.getElementById("mafia_voteVariants").innerHTML + "<option>" + voteVariants[i] + "</option>"
    }
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
        document.getElementById("mafia_voteVote").hidden = false

    }
    else
    {
        document.getElementById("mafia_voteVote").hidden = true
    }

}
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

function mafia_voteVote() {

}

