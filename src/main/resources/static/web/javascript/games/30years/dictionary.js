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
const field_realExcute = document.getElementById('real-excute')
const field_falseExcute = document.getElementById('false-excute')

const prefix = 'Пресвятые боеголовки! '

//Buttons
const btn_startGame = document.getElementById('startGame')
const btn_stopGame = document.getElementById('stopGame')
const btn_invite = document.getElementById('invite')
const btn_joinGame = document.getElementById('joinGame')
const btn_createGame = document.getElementById('createGame')
const btn_realExcute = document.getElementById('real-exec-btn')
const btn_falseExcute = document.getElementById('false-exec-btn')


const UserTable = document.getElementById("userTable")
const AlertContainer = document.getElementById("alertContainer")
    /**
     * frames in main gamefield 
     */
const Frames = {
    /** форма входа*/
    Login: document.getElementById('userlogin'),
    /** таблица пользователей */
    UserList: document.getElementById('userList'),

    Loader: document.getElementById('loader'),
    ProgressBar: {
        main: document.getElementById('progress-div'),
        left: document.getElementById('progress-blue'),
        done: document.getElementById('progress-red'),
    },
    BeforeGame: document.getElementById('beforeGame'),
    Lobby: document.getElementById('inLobby'),
    RealEx: document.getElementById('realExec'),
    FalseEx: document.getElementById('falseExec'),
    Voter: document.getElementById('voterFrame'),
    Results: document.getElementById('resultsFrame'),
}