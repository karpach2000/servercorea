/**********WEB SOCKETS*******/

var SEPORATOR = "_"
var cardsWsConnectionUri = "ws://" + document.location.host + "/games/cards/ws";
var cardsWebsocketConnection = new WebSocket(cardsWsConnectionUri);
cardsWebsocketConnection.onerror = function(evt) { onConnectionError(evt) };
cardsWebsocketConnection.onopen = function(evt) { onConnectionOpen(evt) };
cardsWebsocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
cardsWebsocketConnection.onclose = function(evt) { onclose(evt) };

function onConnectionError(evt) {
    console.log("Connection error.")
}

function onConnectionOpen() {
    cardsWebsocketConnection.send("ping")
}

function onclose() {
    //websocketConnection.close()
    cardsWebsocketConnection = new WebSocket(cardsWsConnectionUri);
    cardsWebsocketConnection.onerror = function(evt) { onConnectionError(evt) };
    cardsWebsocketConnection.onopen = function(evt) { onConnectionOpen(evt) };
    cardsWebsocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
    cardsWebsocketConnection.onclose = function(evt) { onclose(evt) };
}

function onConnectionMessage(evt) {
    console.log("received: " + evt.data);
    var command = evt.data.split(SEPORATOR)[0]
    var data = evt.data.split(SEPORATOR)[1]
    if (command == "addUserEvent") {
        cardsWebsocketConnection.send("ok_" + document.getElementById("userName").value + document.getElementById("userCard").value)
        document.getElementById("users").textContent = data

    } else if (command == "startGameEvent") {
        cardsWebsocketConnection.send("ok")
        startGame()
    } else if (command == "stopGameEvent") {
        cardsWebsocketConnection.send("ok")
        alert("Игра закончена! Вот карточки игроков:\n" + data)
        stopGamePosition()

    }

}

/**********POSITIONS****************/
function beforGamePosition() {
    document.getElementById("user").hidden = true
    document.getElementById("game").hidden = true
    document.getElementById("beforGame").hidden = false

    document.getElementById("leftTip").innerHTML = '<p>Убедитесь, что все желающие знают ID и пароль сессии! ' +
        'дождитесь, пока все присоединятся к игре.</p>' +
        '<p>После нажатия на кнопку "Начать игру" присоединиться до конца раунда будет невозможно!</p>' +
        '<p>Для начала игры вас должно быть не меньше 3 человек</p>';
}

function stopGamePosition() {
    document.getElementById("user").hidden = false
    document.getElementById("game").hidden = true
    document.getElementById("beforGame").hidden = true

    document.getElementById("leftTip").innerHTML = '<p>Для создания новой игры:</p>' +
        '<ol>' +
        '    <li>Придумайте свое "Имя пользователя", "ID сессии", "Пароль сессии"</li>' +
        '    <li>Передайте "Пароль сессии" и "ID сессии" остальным игрокам</li>' +
        '    <li>Нажмите кнопку "Добавить себя"</li> ' +
        '</ol> ' +
        '<p>Для присоединения к существующей игре:</p>' +
        '<ol> ' +
        '    <li>Придумайте свое "Имя пользователя"</li> ' +
        '    <li>Заполните поля "Пароль сессии" и "ID сессии" значениями, получеными от создателя игры</li>' +
        '    <li>Нажмите кнопку "Добавить себя"</li>' +
        '</ol>';
}

function gamePosition() {
    document.getElementById("user").hidden = true
    document.getElementById("game").hidden = false
    document.getElementById("beforGame").hidden = true

    document.getElementById("leftTip").innerHTML = "<p>Задавай вопросы другим, и пытайся определить что написано на твоей карточке. Одновременно с этим, нужно отвечать на вопросы других</p>";
}

/***********************************/
var getUserAction = false

function login() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var userCard = document.getElementById("userCard").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value

    if (userName == "") {
        alert("Заполните поле \"Имя пользователя\"")
        return
    }
    if (userCard == "") {
        alert("Заполните поле \"Карточка соседа\"")
        return
    } else if (sessionId == "") {
        alert("Заполните поле \"ID сессии\"")
        return
    } else if (sessionPas == "") {
        alert("Заполните поле \"Пароль сессии\"")
        return
    }

    //create session
    xmlHttp.open("GET", "/games/cards_add_session?sessionId=" + sessionId +
        "&sessionPas=" + sessionPas, false); // false for synchronous request
    xmlHttp.send();
    if (xmlHttp.responseText == "true") {
        cardsWebsocketConnection.send("init" + SEPORATOR + sessionId + " " + sessionPas + " " + userName)
            //lert("Игра создана.")
    } else if (xmlHttp.responseText == "false") {
        cardsWebsocketConnection.send("init" + SEPORATOR + sessionId + " " + sessionPas + " " + userName)
            //alert("Ничего не делаем.")
    } else {
        alert(xmlHttp.responseText)
        return
    }

    //Add user
    xmlHttp.open("GET", "/games/cards_addUser?userName=" + userName + "&sessionId=" + sessionId +
        "&sessionPas=" + sessionPas + "&userCard=" + userCard, false); // false for synchronous request
    xmlHttp.send(null);
    if (xmlHttp.responseText == "true") {
        beforGamePosition()
        getUsers() //не ясно успеет ли пройти прописка на сервере когда метод добежит до сюда
            //document.getElementById("users").textContent = "    " + userName
            //alert("Пользователь добавлен.")
    } else {
        alert(xmlHttp.responseText)
    }
}



function startGame() {
    getUserAction = false
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var userCard = document.getElementById("userCard").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/cards_count_users?userName=" + userName + "&sessionId=" + sessionId +
        "&sessionPas=" + sessionPas, false); // false for synchronous request
    xmlHttp.send(null);
    if (xmlHttp.responseText < 3) {
        alert("Минимальное количество игроков 3!")
        return
    }

    xmlHttp.open("GET", "/games/cards_start_game?userName=" + userName + "&sessionId=" + sessionId +
        "&sessionPas=" + sessionPas + "&userCard=" + userCard, false); // false for synchronous request
    xmlHttp.send(null);
    gamePosition()
    document.getElementById("gamerInformation").textContent = xmlHttp.responseText
        //alert(xmlHttp.responseText)
}

function stopGame() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var userCard = document.getElementById("userCard").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/cards_stop_game?userName=" + userName + "&sessionId=" + sessionId +
        "&sessionPas=" + sessionPas + "&userCard=" + userCard, false); // false for synchronous request
    xmlHttp.send(null);
    stopGamePosition()
        //alert(xmlHttp.responseText)

}
/*
function showCards() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var userCard = document.getElementById("userCard").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/cards_get_cards?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas+"&userCard="+userCard, false); // false for synchronous request
    xmlHttp.send(null);
    //document.getElementById("gamerInformation").textContent =
        //"!!!Шпион: "+xmlHttp.responseText + "!!!\n" + document.getElementById("gamerInformation").textContent
}
function isCardsShowen() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var userCard = document.getElementById("userCard").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/cards_is_cards_showen?userName="+userName+"&sessionId="+sessionId+
        "&sessionPas="+sessionPas+"&userCard="+userCard, false); // false for synchronous request
    xmlHttp.send(null);
    alert(xmlHttp.responseText)
}
*/
function getUsers() {
    var xmlHttp = new XMLHttpRequest();
    var userName = document.getElementById("userName").value
    var userCard = document.getElementById("userCard").value
    var sessionId = document.getElementById("sessionId").value
    var sessionPas = document.getElementById("sessionPas").value
    xmlHttp.open("GET", "/games/cards_get_users?userName=" + userName + "&sessionId=" + sessionId +
        "&sessionPas=" + sessionPas + "&userCard=" + userCard, false); // false for synchronous request
    xmlHttp.send(null);
    document.getElementById("users").textContent = xmlHttp.responseText
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}