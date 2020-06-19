/**********WEB SOCKETS*******/
let SEPARATOR = "_"
let mafiaWsConnectionUrl = "ws://" + document.location.host + "/games/thirtyyears/ws";
let webSocket = new WebSocket(mafiaWsConnectionUrl);

webSocket.onerror = function(event) {
    console.log(`[error] ${event.message}`)
};

webSocket.onopen = function(event) {
    console.log(`[open] Соединение установлено, сообщение ${event.data}`)
    this.makeRequest('PING')
};

webSocket.onmessage = function(event) {
    console.log(`[incoming] Данные получены с сервера:\n ${event.data}`);
    // console.log('--- try to parse data');
    let incoming = JSON.parse(event.data)
        // console.log(incoming);


    // GameState.eventListener(incoming)
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

webSocket.makeRequest = function(command, data = '', isAnswer = 'false') {
    let message = `{'userName':'${field_userName.value}','sessionId':'${field_sessionId.value==""?-1:field_sessionId.value}','sessionPas':'${field_sessionPas.value==""?-1:field_sessionPas.value}','command':'${command}','data':'${data}','isAnserOnRequest':'${isAnswer}'}`
    console.log(`[outcoming] Данные отправлены на сервер:\n ${message}`);
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