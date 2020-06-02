/* Действия, вызываемые на странице (инициализированы пользователем) */

/**
 * Нажатие на кнопку "начать игру". 
 * Выполняется проверка условий начала, и отправляется команда начать игру для сервера
 */
function mafia_startGame() {
    console.log("[action] нажата кнопка 'Начать игру'");
    GameState.getCountUsers();
    if (GameState.countUsers < 5) {
        console.log(`[warning] недостаточно людей (${GameState.countUsers}) в лобби для начала игры`);
        alert("Минимальное количество игроков 5!");
    } else {
        console.log("[action] отправлена команда начала игры на сервер")
        mafia_request("mafia_startGame");
    }
}

/**
 * Нажатие на кнопку "Закончить игру". 
 * Выполняется проверка (запрос подтверждения), и отправляется команда закончить игру для сервера
 */
function mafia_stopGame() {
    console.log("[action] нажата кнопка 'Закончить игру'");
    if (confirm("Вы действительно хотите завершить игру для всех? Возможно, не все с этим согласны, а кнопка только одна")) {
        console.log("[action] отправлена команда завершения игры на сервер")
        mafia_request("mafia_stopGame")
    }
}

/**
 * Нажатие на кнопку "Добавить себя" или реконнект.
 * Создает сессию (если таковой нет) и/или добавляет пользователя. 
 */
function mafia_login() {
    console.log("[action] нажата кнопка 'Добавить себя'");

    if (field_userName.value == "") {
        console.log('[warning] Пустое поле "Имя пользователя"');
        alert('Заполните поле "Имя пользователя"')
        return
    } else if (field_sessionId.value == "") {
        console.log('[warning] Пустое поле "ID сессии"');
        alert('Заполните поле "ID сессии"')
        return
    } else if (field_sessionPas.value == "") {
        console.log('[warning] Пустое поле "Пароль сессии"');
        alert('Заполните поле "Пароль сессии"')
        return
    }

    //create session
    createSessionIfNotExists()
        //Add user
    ans = addUserIfNotExist()

    if (ans == "true") {
        GameState.currentFrame = "LOBBY";
        GameState.update("frame");
    } else {
        alert(ans)
    }
}

/**
 * Нажатие на кнопку "Хочу быть ведущим".
 * Делает данного игрока ведущим игры. 
 */
function mafia_wanToBeaLeader() {
    console.log("[action] нажата кнопка 'Хочу быть ведущим'");
    mafia_request("mafia_becomeALeader");
}

/**
 * Нажатие на кнопку "Засчитать голоса горожан".
 * Завершить шолосование горожан, завершить день.
 */
function mafia_citizenVote() {
    console.log("[action] нажата кнопка 'Засчитать голоса горожан'");
    mafia_request("mafia_getCitizenVoteResult");
}

/**
 * Нажатие на кнопку "Засчитать голоса мафии".
 * Завершить действие ролей - мафии, шерифа и т.д., завершить ночь.
 */
function mafia_mafiaVote() {
    console.log("[action] нажата кнопка 'Засчитать голоса мафии'");
    mafia_request("mafia_getMafiaVoteResult");
}

/**
 * Переключение выпадающего списка голосовалки "Выберете против кого голосовать".
 * Отдать голос за одного из игроков.
 */
function mafia_voteVote() {
    console.log("[action] выбран элемент из списка 'Выберете против кого голосовать'");
    let xmlHttp = new XMLHttpRequest();

    xmlHttp.open("GET", "/games/mafia_voteVote?userName=" + field_userName.value +
        "&sessionId=" + field_sessionId.value +
        "&sessionPas=" + field_sessionPas.value +
        "&vote=" + field_VoteVariants.value,
        false); // false for synchronous request
    xmlHttp.send(null);
}



/**
 * Переключение выпадающего списка голосовалки "Проверка Шерифа".
 * Шериф проверяет игрока.
 */
function mafia_selectCheckUserSheriff() {
    console.log("[action] выбран элемент из списка 'Проверка Шерифа'");
    let xmlHttp = new XMLHttpRequest();

    if (field_SheriffVariants.value.length > 0) {
        xmlHttp.open("GET", "/games/mafia_selectCheckUserSheriff?userName=" + field_userName.value +
            "&sessionId=" + field_sessionId.value +
            "&sessionPas=" + field_sessionPas.value +
            "&checkedUserName=" + field_SheriffVariants.value,
            false); // false for synchronous request
        xmlHttp.send(null);
        console.log("GET RX: " + xmlHttp.responseText)
        return xmlHttp.responseText
    }
}

/******PRIVATE******/

/**
 * Запрашиваем у сервера существование пользователя
 */
function addUserIfNotExist() {
    return mafia_request("mafia_addUser");
}

/**
 * Запрашиваем у сервера существование сессии.
 * если сессии не существует, создаем ее
 */
function createSessionIfNotExists() {

    var ans = mafia_request("mafia_add_session");
    if (ans == "true") {
        webSocket.send("init" + SEPARATOR +
                field_sessionId.value + " " +
                field_sessionPas.value + " " +
                field_userName.value)
            //alert("Игра создана.")
    } else if (ans == "false") {
        webSocket.send("init" + SEPARATOR +
                field_sessionId.value + " " +
                field_sessionPas.value + " " +
                field_userName.value)
            //alert("Ничего не делаем.")
    } else {

        alert(ans)
        return
    }
}

/**
 * Получить варианты за кого можно проголосовать.
 * (список кандидатов)
 */
function mafia_getMafiaVoteVariants() {
    let voteVariants = mafia_request("mafia_getUsersForVoteMafia").split(SEPARATOR)
    field_VoteVariants.innerHTML = "<option></option>"
    for (let i = 0; i < voteVariants.length; i = i + 1) {
        if (voteVariants[i].length > 0)
            field_VoteVariants.innerHTML = `${field_VoteVariants.innerHTML}<option>${voteVariants[i]}</option>`
    }
}

/**
 * Получить варианты за кого можно проголосовать когда голосует город.
 * (список кандидатов)
 */
function mafia_getСitizenVoteVariants() {
    let voteVariants = mafia_request("mafia_getUsersForVoteСitizen").split(SEPARATOR)
    field_VoteVariants.innerHTML = "<option></option>"
    for (let i = 0; i < voteVariants.length; i = i + 1) {
        if (voteVariants[i].length > 0)
            field_VoteVariants.innerHTML = `${field_VoteVariants.innerHTML}<option>${voteVariants[i]}</option>`
    }
}

/**
 * Получить варианты за кого можно проголосовать когда голосует город.
 * (список кандидатов)
 */
function mafia_getSheriffCheckVariants() {
    let variants = mafia_request("mafia_getCheckUserSheriffVariants").split(SEPARATOR)
    field_SheriffVariants.innerHTML = "<option></option>"
    for (let i = 0; i < variants.length; i = i + 1) {
        if (variants[i].length > 0)
            field_SheriffVariants.innerHTML = `${field_SheriffVariants.innerHTML}<option>${variants[i]}</option>`
    }
}