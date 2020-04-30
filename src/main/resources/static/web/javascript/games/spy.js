
/**********WEB SOCKETS*******/

var SEPORATOR = "_"
var wsConnectionUri = "ws://" + document.location.host +"/games/spy";
var websocketConnection = new WebSocket(wsConnectionUri);
websocketConnection.onerror = function(evt) { onConnectionError(evt) };
websocketConnection.onopen = function(evt) { onConnectionOpen(evt) };
websocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
websocketConnection.onclose = function(evt) { onclose(evt) };

function onConnectionError(evt) {
alert("Ошибка соединения с сервером, перезагрузите страницу!")
}

function onConnectionOpen() {
    websocketConnection.send("ping")
}
function onclose() {
    //websocketConnection.close()
    websocketConnection = new WebSocket(wsConnectionUri);
    websocketConnection.onerror = function(evt) { onConnectionError(evt) };
    websocketConnection.onopen = function(evt) { onConnectionOpen(evt) };
    websocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
    websocketConnection.onclose = function(evt) { onclose(evt) };
}

function onConnectionMessage(evt) {
    console.log("received: " + evt.data);
    var command = evt.data.split(SEPORATOR )[0]
    var data = evt.data.split(SEPORATOR )[1]
    if(command=="addUserEvent") {
        websocketConnection.send("ok_"+document.getElementById("userName").value)
        document.getElementById("users").textContent = data

    }
    else if(command=="startGameEvent") {
        websocketConnection.send("ok")
        startGame()
    }
    else if(command=="stopGameEvent") {
        websocketConnection.send("ok")
        alert("Игра закончена! Шпион " + data)
        stopGamePosition()

    }
    else if(command=="spyIsNotSecretEvent") {
        websocketConnection.send("ok")
        document.getElementById("gamerInformation").textContent =
            "!!!Шпион: "+data + "!!!\n" + document.getElementById("gamerInformation").textContent
        alert("Шпион " + data)
    }
}

/**********POSITIONS****************/
function beforGamePosition() {
    document.getElementById("user").hidden = true
    document.getElementById("game").hidden = true
    document.getElementById("beforGame").hidden = false
}
function stopGamePosition() {
    document.getElementById("user").hidden = false
    document.getElementById("game").hidden = true
    document.getElementById("beforGame").hidden = true

}

function gamePosition() {
    document.getElementById("user").hidden = true
    document.getElementById("game").hidden = false
    document.getElementById("beforGame").hidden = true

}

/***********************************/
var getUserAction = false

function addUser() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value

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
    xmlHttp.open("GET", "/games/spy_add_session?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if(xmlHttp.responseText=="true") {
        websocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
        //lert("Игра создана.")
    }
    else if(xmlHttp.responseText=="false")
    {
        websocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
        //alert("Ничего не делаем.")
    }
    else
    {

        alert(xmlHttp.responseText)
        return
    }


    //Add user
    xmlHttp.open("GET", "/games/spy_addUser?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if(xmlHttp.responseText=="true")
    {
        beforGamePosition()
        getUsers()//не ясно успеет ли пройти прописка на сервере когда метод добежит до сюда
        //document.getElementById("users").textContent = "    " + userName
        //alert("Пользователь добавлен.")
    }
    else
    {

        alert(xmlHttp.responseText)
    }



}



function startGame() {
    getUserAction = false
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/spy_count_users?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if(xmlHttp.responseText<3)
    {
        alert("Минимальное количество игроков 3!")
        return
    }

    xmlHttp.open("GET", "/games/spy_start_game?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    gamePosition()
    document.getElementById("gamerInformation").textContent = xmlHttp.responseText
    //alert(xmlHttp.responseText)
}
function stopGame() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/spy_stop_game?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    stopGamePosition()
    //alert(xmlHttp.responseText)

}

function showSpy() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/spy_get_spy?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);

    //document.getElementById("gamerInformation").textContent =
        //"!!!Шпион: "+xmlHttp.responseText + "!!!\n" + document.getElementById("gamerInformation").textContent
}

function isSpyShowen() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/spy_is_spy_showen?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    alert(xmlHttp.responseText)
}

function getUsers()
{
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/spy_get_users?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    document.getElementById("users").textContent = xmlHttp.responseText
}
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}



