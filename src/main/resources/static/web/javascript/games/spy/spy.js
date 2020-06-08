
/**********WEB SOCKETS*******/

var SEPORATOR = "_"
var spyWsConnectionUri = "ws://" + document.location.host +"/games/spy/ws";
var spyWebsocketConnection = new WebSocket(spyWsConnectionUri);
spyWebsocketConnection.onerror = function(evt) { onConnectionError(evt) };
spyWebsocketConnection.onopen = function(evt) { onConnectionOpen(evt) };
spyWebsocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
spyWebsocketConnection.onclose = function(evt) { onclose(evt) };

function onConnectionError(evt) {
    console.log("Connection error.")
}

function onConnectionOpen() {
    spyWebsocketConnection.send("ping")
}
function onclose() {
    //websocketConnection.close()
    spyWebsocketConnection = new WebSocket(spyWsConnectionUri);
    spyWebsocketConnection.onerror = function(evt) { onConnectionError(evt) };
    spyWebsocketConnection.onopen = function(evt) { onConnectionOpen(evt) };
    spyWebsocketConnection.onmessage = function(evt) { onConnectionMessage(evt) };
    spyWebsocketConnection.onclose = function(evt) { onclose(evt) };
}

function onConnectionMessage(evt) {
    console.log("received: " + evt.data);
    var command = evt.data.split(SEPORATOR )[0]
    var data = evt.data.split(SEPORATOR )[1]
    if(command=="addUserEvent") {
        spyWebsocketConnection.send("ok_"+document.getElementById("userName").value)
        document.getElementById("users").textContent = data

    }
    else if(command=="startGameEvent") {
        spyWebsocketConnection.send("ok")
        startGame()
    }
    else if(command=="stopGameEvent") {
        spyWebsocketConnection.send("ok")
        alert("Игра закончена! Шпион " + data)
        stopGamePosition()

    }
    else if(command=="spyIsNotSecretEvent") {
        spyWebsocketConnection.send("ok")
        document.getElementById("gamerInformation").textContent =
            "!!!Шпион: "+data + "!!!\n" + document.getElementById("gamerInformation").textContent
        alert("Шпион " + data)
    }
    else if(command=="updateLocationList")
    {
        spyWebsocketConnection.send("ok")
        LocationsTable.generateTables(data)
        LocationsTable.show()
        alert(data)

    }
}

/**********POSITIONS****************/
function beforGamePosition() {
    document.getElementById("user").hidden      = true;
    document.getElementById("game").hidden      = true;
    document.getElementById("gameInfo").hidden  = true;
    document.getElementById("beforGame").hidden = false;

    document.getElementById("leftTip").innerHTML =  '<p>Убедитесь, что все желающие знают ID и пароль сессии! '+
                                                    'дождитесь, пока все присоединятся к игре.</p>'+
                                                    '<p>После нажатия на кнопку "Начать игру" присоединиться до конца раунда будет невозможно!</p>'+
                                                    '<p>Для начала игры вас должно быть не меньше 3 человек</p>';
}
function stopGamePosition() {
    document.getElementById("user").hidden      = false;
    document.getElementById("game").hidden      = true;
    document.getElementById("gameInfo").hidden  = true;
    document.getElementById("beforGame").hidden = true;

    document.getElementById("leftTip").innerHTML =  '<p>Для создания новой игры:</p>'+
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

function gamePosition(location,isSpy) {
    document.getElementById("user").hidden      = true;
    document.getElementById("game").hidden      = false;
    document.getElementById("gameInfo").hidden  = false;
    document.getElementById("beforGame").hidden = true;

    //role
    if (isSpy == true){
        document.getElementById("leftTip").innerHTML =  '<p>поздравляем, вы тот самый шпион!</p>' +
                                                        '<p>Вам придется очень внимательно отвечать на вопросы, и еще внимательнее '+
                                                        'их задавать другим - вы ведь не хотите, чтобы Вас раскрыли?'+
                                                        ' Ну и конечно слушать ответы других: давно известно, что болтун - находка для шпиона!</p>' +
                                                        '<p>Как только кто-то нажмет кнопку "Закончить игру", все узнают кто был шпионом. ' + 
                                                        'Назвать локацию нужно раньше, чем за тебя проголосуют!</p>';
    } else {
        document.getElementById("leftTip").innerHTML =  '<p>ваша локация - <strong>'+ location +'</strong>, но кто-то из Вас об этом не знает, но очень хочет узнать. ' +
                                                        'Сохрани ее в тайне, найди злого шпиона, и постарайся не быть слишком подозрительным - а то могут и тебя за шпиона принять.</p>'+
                                                        '<p>Как только кто-то нажмет кнопку "Закончить игру", все узнают кто был шпионом. ' + 
                                                        'Проголосовать за шпиона нужно раньше, чем шпион рассекретит локацию!</p>';
    }
}

/***********************************/
var getUserAction = false

function spy_login() {
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
        spyWebsocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
        //lert("Игра создана.")
    }
    else if(xmlHttp.responseText=="false")
    {
        spyWebsocketConnection.send("init"+SEPORATOR+sessionId +" "+sessionPas+" "+userName)
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
    //   parser
    let incomingData = JSON.parse(xmlHttp.responseText)
    
    if (incomingData.spy == true){
        incomingData.location = "Локация неизвестна";
        incomingData.name +=" (ШПИОН)"
    } 
    // switch divs and hints
    gamePosition(incomingData.location,incomingData.spy)
    // put data into the page
    document.getElementById("gamerInformation").textContent = "Ваше имя: " + incomingData.name + 
        "\nЛокация: " + incomingData.location + 
        "\nПрисоединилось игроков: " + incomingData.usersCount + "\n" +
        incomingData.allUsers;

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



