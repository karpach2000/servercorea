let debug = true
    /**
     * Обертка для логгирования. Выводит сообщения если глобальная
     * переменная (debug) истинна, или передан флаг форсированного логгирования
     * @param {string} data  - передаваемое значение
     * @param {boolean} force - команда форсированного логгирования
     */
function logger(data, force = false) {
    let now = new Date()
    if (debug || force) console.log(`${("0" + now.getHours()).slice(-2)}:${("0" + now.getMinutes()).slice(-2)}:${("0" + now.getSeconds()).slice(-2)}.${("00" + now.getMilliseconds()).slice(-3)} ${data}`);
}

// объекты HTML превращаем в константы, чтобы потом с ними работать как с объектами js
//поля (значиния меняются)
const field_userName = document.getElementById("userName")
const field_sessionId = document.getElementById("sessionId")
const field_sessionPas = document.getElementById("sessionPas")

const prefix = 'Пресвятые боеголовки! '

const progressBar_left = document.getElementById('progress-blue')
const progressBar_done = document.getElementById('progress-red')
const progressBar = document.getElementById('progress-div')

//Buttons
const btn_startGame = document.getElementById('startGame')
const btn_stopGame = document.getElementById('stopGame')
const btn_invite = document.getElementById('invite')
const btn_joinGame = document.getElementById('joinGame')
const btn_createGame = document.getElementById('createGame')