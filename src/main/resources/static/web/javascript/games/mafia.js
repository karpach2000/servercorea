/*****POSITIONS*****/

function mafia_addUserPosition() {

}
function mafia_beforGamePosition() {

}
function mafia_gamePositionUserVote() {

}
function mafia_gamePositionMafiaVote() {

}

function mafia_startGame(){


}

function mafia_stopGame(){


}

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
    xmlHttp.open("GET", "/games/mafia_addUser?userName="+userName+"&sessionId="+sessionId+
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


function mafia_usrerVoteVote() {

}

function mafia_addUser() {

}