/**********WEB SOCKETS*******/
let SEPARATOR = "_"
let mafiaWsConnectionUrl = "ws://" + document.location.host + "/games/thirtyyears/ws";
let webSocket = new WebSocket(mafiaWsConnectionUrl);

webSocket.onerror = function(event) {
    console.log(`[error] ${event.message}`)
};

webSocket.onopen = function(event) {
    console.log(`[open] Соединение установлено, сообщение ${event.data}`)
    this.send('{"userName":"","sessionId":-1,"sessionPas":-1,"command":"PING","data":"","isAnserOnRequest":false}')
};

webSocket.onmessage = function(event) {
    console.log(`[message] Данные получены с сервера:\n ${event.data}`);
    // GameState.eventListener(command, data)
};

webSocket.onclose = function(event) {
    if (event.wasClean) {
        console.log(`[close] Соединение закрыто чисто, код=${event.code} причина=${event.reason}`);
    } else {
        // например, сервер убил процесс или сеть недоступна
        // обычно в этом случае event.code 1006
        console.log('[close] Соединение прервано');
    }
};

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