/*
 * Изменяет состояние окна.
 * (приводит его в состояние соответсвующее текущей таблицы состояния)
 */
let ws = new WindowStates()

/* Рисует таблицу*/
let vtg = new VoteTableGenerator()

/* Немножко глобальных переменных */
let myUserName;
let role;
let currentLeader;
// объекты HTML превращаем в константы, чтобы потом с ними работать как с объектами js
const field_userName = document.getElementById("userName");
const field_sessionId = document.getElementById("sessionId");
const field_SessionPas = document.getElementById("sessionPas");
const field_VoteVariants = document.getElementById("mafia_voteVariants");
const field_SheriffVariants = document.getElementById("mafia_checkUserSheriffVariants");

const textArea_mafiaUsers = document.getElementById("mafia_users");
const text_mafiaLeader = document.getElementById("mafia_leader");
const text_mafiaRole = document.getElementById("mafia_role");
const table_mafiaUserVoteTable = document.getElementById("mafia_userVoteTable");

const button_voteCitizen = document.getElementById("mafia_voteСitizenButton");
const button_voteMafia = document.getElementById("mafia_voteMafiaButton");
const button_startGame = document.getElementById("mafia_startGame");
const button_becameLeader = document.getElementById("mafia_wanToBeaLeader");

const div_controls = document.getElementById("mafia_controller");
const div_mainVoter = document.getElementById("mafia_voter");
const div_sherifVoter = document.getElementById("mafia_SheriffVote");

const frame_user = document.getElementById("user");
const frame_beforGame = document.getElementById("beforGame");
const frame_game = document.getElementById("game");
const frame_gameInfo = document.getElementById("gameInfo");

const span_leftTip = document.getElementById("leftTip");
const span_inGameTip = document.getElementById("inGameTip");
const span_inLobbyTip = document.getElementById("inLobbyTip");
/**********WEB SOCKETS*******/

let SEPORATOR = "_"
let mafiaWsConnectionUri = "ws://" + document.location.host + "/games/mafia/ws";
let mafiaWebsocketConnection = new WebSocket(mafiaWsConnectionUri);
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
        //var userName = field_userName.value
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
    let command = evt.data.split(SEPORATOR)[0]
    let data = evt.data.split(SEPORATOR)[1]
        //DATA...
    if (command == "addUserEvent") {
        mafiaWebsocketConnection.send("ok_" + field_userName.value)
        textArea_mafiaUsers.textContent = data

    } else if (command == "leaderChandged") {
        mafiaWebsocketConnection.send("ok")
        text_mafiaLeader.textContent = "Ведущий: " + data
        ws.whoIsLeader();
    } else if (command == "updateVoteTable") {
        mafiaWebsocketConnection.send("ok")

        table_mafiaUserVoteTable.innerHTML = vtg.generate(data)
        if ((role != "LEADING") && (role != "SHERIFF"))
            document.querySelectorAll(".tableCheck").forEach(element => element.hidden = true);
    }
    //STATES...
    else if (command == "startGameEvent") {
        mafiaWebsocketConnection.send("ok")
        updateWindowByState("CITIZEN_VOTE")
    } else if (command == "stopGameEvent") {
        mafiaWebsocketConnection.send("ok")
        alert("Игра закончена!")
        updateWindowByState("STOP_GAME")
    } else if (command == "openMafiaVote") {
        mafiaWebsocketConnection.send("ok")
        if (data != "")
            alert("Игрок:" + data + " мертв!")
        else
            alert("К сожалению все живы.")
        mafia_getSheriffCheckVariants()
        updateWindowByState("MAFIA_VOTE")
        table_mafiaUserVoteTable.innerHTML = vtg.generate(data)
        if ((role != "LEADING") && (role != "SHERIFF"))
            document.querySelectorAll(".tableCheck").forEach(element => element.hidden = true);

    } else if (command == "openСitizensVote") {
        mafiaWebsocketConnection.send("ok")
        if (data != "")
            alert("Игрок:" + data + " мертв!")
        else
            alert("К сожалению все живы.")
            //mafia_checkUserSheriff()
        updateWindowByState("CITIZEN_VOTE")
        table_mafiaUserVoteTable.innerHTML = vtg.generate(data)
        if ((role != "LEADING") && (role != "SHERIFF"))
            document.querySelectorAll(".tableCheck").forEach(element => element.hidden = true);
    } else if (command == "pong") {
        if ((field_userName.value.length > 0) && (field_sessionId.value.length > 0) && (field_SessionPas.value.length > 0)) {
            mafia_login()
        }
    } else if (command == "sheriffCheckedUser") //КОЛЯ ЗДЕСЬ!!!
    {
        alert("Шериф выбрал для проверки  " + data)
    }
}


/******PUBLIC******/

function mafia_startGame() {
    console.log("mafia_startGame()")
    let count = mafia_request("mafia_count_users")
    if (count < 5) {
        alert("Минимальное количество игроков 5!")
        return
    }
    mafia_request("mafia_startGame")
}


function mafia_stopGame() {
    if (confirm("Вы действительно хотите завершить игру для всех? Возможно, не все с этим согласны, а кнопка только одна")) {
        console.log("mafia_stopGame()")
        mafia_request("mafia_stopGame")
    }

}



/**
 * Добавить пользователя.
 * (создает сессию если таковой нет и добавляет пользователя)
 * (применяется в том числе для для реконекта)
 */
function mafia_login() {
    console.log("mafia_login()")

    if (field_userName.value == "") {
        alert("Заполните поле \"Имя пользователя\"")
        return
    } else if (field_sessionId.value == "") {
        alert("Заполните поле \"ID сессии\"")
        return
    } else if (field_SessionPas.value == "") {
        alert("Заполните поле \"Пароль сессии\"")
        return
    }

    //create session
    createSessionIfNotExists()
        //Add user
    ans = addUserIfNotExist()
    if (ans == "true") {
        let gameState = mafia_getGameState()
        updateWindowByState(gameState)
    } else {
        alert(ans)
    }

}

function mafia_wanToBeaLeader() {
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
    let xmlHttp = new XMLHttpRequest();

    xmlHttp.open("GET", "/games/mafia_voteVote?userName=" + field_userName.value + "&sessionId=" + field_SessionPas.value +
        "&sessionPas=" + field_VoteVariants.value + "&vote=" + vote, false); // false for synchronous request
    xmlHttp.send(null);
}

/**
 * Шериф проверяет игрока.
 */
function mafia_selectCheckUserSheriff() {
    console.log("GET TX: mafia_checkUserSheriff")
    let xmlHttp = new XMLHttpRequest();

    if (field_SheriffVariants.value.length > 0) {
        xmlHttp.open("GET", "/games/mafia_selectCheckUserSheriff?userName=" + field_userName.value + "&sessionId=" +
            field_sessionId.value + "&sessionPas=" + field_SessionPas.value + "&checkedUserName=" + field_SheriffVariants.value, false); // false for synchronous request
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
    if (gameState == "ADD_USERS") {
        mafia_getLeader()
        ws.beforGamePosition()
    } else if (gameState == "CITIZEN_VOTE") {
        ws.gamePositionCitizenVote()
        mafia_getСitizenVoteVariants()
        mafia_updateWindowByRole()
        mafia_startGame()
    } else if (gameState == "MAFIA_VOTE") {
        ws.gamePositionMafiaVote()
        mafia_getMafiaVoteVariants()
        mafia_updateWindowByRole()
        mafia_startGame()
    } else if (gameState = "STOP_GAME") {
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
function mafia_updateWindowByRole() {

    role = mafia_request("mafia_getRole")
    vtg.role = role
        //прописываем роль пользователю
    text_mafiaRole.textContent = "Роль: " + role
        //если пользователь не лидер убираем кнопку голосования
    if (role == "LEADING") {
        ws.leaderPosition()
    } else if (role == "CITIZEN") {
        ws.citizenPosition()
    } else if (role == "MAFIA") {
        ws.mafiaPosition()
    } else if (role == "SHERIFF") {
        ws.sheriffPosition()
    }
}

/******PRIVATE******/

function addUserIfNotExist() {
    return mafia_request("mafia_addUser")
}

function createSessionIfNotExists() {

    var ans = mafia_request("mafia_add_session")
    if (ans == "true") {
        mafiaWebsocketConnection.send("init" + SEPORATOR + field_sessionId.value + " " + field_SessionPas.value + " " + field_userName.value)
            //lert("Игра создана.")
    } else if (ans == "false") {
        mafiaWebsocketConnection.send("init" + SEPORATOR + field_sessionId.value + " " + field_SessionPas.value + " " + field_userName.value)
            //alert("Ничего не делаем.")
    } else {

        alert(ans)
        return
    }
}


function mafia_getLeader() {
    currentLeader = mafia_request("mafia_getLeader");
    text_mafiaLeader.textContent = "Ведущий: " + currentLeader;

}


/**
 * Получить варианты за кого можно проголосовать.
 * (список кандидатов)
 */
function mafia_getMafiaVoteVariants() {
    let voteVariants = mafia_request("mafia_getUsersForVoteMafia").split(SEPORATOR)
    field_VoteVariants.innerHTML = "<option></option>"
    for (let i = 0; i < voteVariants.length; i = i + 1) {
        if (voteVariants[i].length > 0)
            field_VoteVariants.innerHTML = field_VoteVariants.innerHTML + "<option>" + voteVariants[i] + "</option>"
    }
}

/**
 * Получить варианты за кого можно проголосовать когда голосует город.
 * (список кандидатов)
 */
function mafia_getСitizenVoteVariants() {
    let voteVariants = mafia_request("mafia_getUsersForVoteСitizen").split(SEPORATOR)
    field_VoteVariants.innerHTML = "<option></option>"
    for (let i = 0; i < voteVariants.length; i = i + 1) {
        if (voteVariants[i].length > 0)
            field_VoteVariants.innerHTML =
            field_VoteVariants.innerHTML + "<option>" + voteVariants[i] + "</option>"
    }
}

/**
 * Получить варианты за кого можно проголосовать когда голосует город.
 * (список кандидатов)
 */
function mafia_getSheriffCheckVariants() {
    let variants = mafia_request("mafia_getCheckUserSheriffVariants").split(SEPORATOR)
    field_SheriffVariants.innerHTML = "<option></option>"
    for (let i = 0; i < variants.length; i = i + 1) {
        if (variants[i].length > 0)
            field_SheriffVariants.innerHTML =
            field_SheriffVariants.innerHTML + "<option>" + variants[i] + "</option>"
    }
}



function mafia_request(command) {
    console.log("GET TX: " + command)
    let xmlHttp = new XMLHttpRequest();

    xmlHttp.open("GET", "/games/" + command + "?userName=" + field_userName.value + "&sessionId=" + field_sessionId.value +
        "&sessionPas=" + field_SessionPas.value, false); // false for synchronous request
    xmlHttp.send(null);
    console.log("GET RX: " + xmlHttp.responseText)
    return xmlHttp.responseText

}