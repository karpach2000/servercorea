/**********WEB SOCKETS*******/
let SEPARATOR = "_"
let mafiaWsConnectionUrl = "ws://" + document.location.host + "/games/thirtyyears/ws";
let webSocket = new WebSocket(mafiaWsConnectionUrl);

webSocket.onerror = function(event) {
    logger(`[error] ${event.message}`, true)
};

webSocket.onopen = function(event) {
    logger(`[open] Соединение установлено, сообщение ${event.data}`)
    this.makeRequest('PING')
};

webSocket.onmessage = function(event) {
    logger(`[incoming] Данные получены с сервера:\n ${event.data}`);
    let incoming = JSON.parse(event.data)
        // console.log(incoming);
    GameState.eventListener(incoming)
};

webSocket.onclose = function(event) {
    if (event.wasClean) {
        logger(`[close] Соединение закрыто чисто, код=${event.code} причина=${event.reason}`);
    } else {
        // например, сервер убил процесс или сеть недоступна
        // обычно в этом случае event.code 1006
        logger('[close] Соединение прервано', true);
    }
};

webSocket.makeRequest = function(command, data = '', isAnswer = 'false', status = 'GOOD') {
    let message = `{'userName':'${field_userName.value}','sessionId':'${field_sessionId.value==""?-1:field_sessionId.value}','sessionPas':'${field_sessionPas.value==""?-1:field_sessionPas.value}','command':'${command}','data':'${data}','isAnserOnRequest':'${isAnswer}','messageStatus':'${status}'}`
    logger(`[outcoming] Данные отправлены на сервер:\n ${message}`);
    this.send(message)
}


// /** 
//  * Обертка для GET-запросов.
//  */
// function mafia_request(command) {
//     console.log(`[request] ${command}`)
//     let xmlHttp = new XMLHttpRequest();

//     let message = "/games/" + command +
//         "?userName=" + field_userName.value +
//         "&sessionId=" + field_sessionId.value +
//         "&sessionPas=" + field_sessionPas.value;

//     console.log(`[GET] ${message}`);

//     xmlHttp.open("GET", message, false); // false for synchronous request
//     xmlHttp.send(null);
//     console.log(`[response] ${xmlHttp.responseText}`)
//     return xmlHttp.responseText
// }