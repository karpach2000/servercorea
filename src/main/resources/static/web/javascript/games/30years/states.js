let GameState = {

    currentFrame: "",
    myUserName: "",
    countUsers: 0,

    /**
     * функция для переключения видимости фреймов, в зависимости от состояния игры. 
     * @param {string} frame LOGIN, LOBBY, START_GAME, ENTER_EXCUTE, VOTE, SHOW_RESULTS
     */
    switchFrame(frame) {
        this.currentFrame = frame;

        Frames.Loader.hidden = false;
        Frames.BeforeGame.hidden = true;
        Frames.Lobby.hidden = true;
        Frames.RealEx.hidden = true;
        Frames.FalseEx.hidden = true;
        Frames.Voter.hidden = true;
        Frames.Results.hidden = true;

        // document.getElementById('joinGameLoader').hidden = true
        // document.getElementById('createGameLoader').hidden = true

        switch (frame) {

            case ('LOGIN'):
                //страница только открыта, или завершена игра

                //left frame
                Frames.Start.hidden = false;
                Frames.UserList.hidden = true;
                //right frame
                Frames.BeforeGame.hidden = false;
                Frames.Loader.hidden = true;
                //another elements

                break

            case ('LOBBY'):
                //сессия создана, юзер добавился, но игра не стартовала

                //left frame
                Frames.Login.hidden = true;
                Frames.UserList.hidden = false;
                //right frame
                Frames.Lobby.hidden = false;
                Frames.Loader.hidden = true;
                //another elements

                break

            case ('ENTER_REAL_EXCUTE'):
                //right frame
                Frames.RealEx.hidden = false;
                Frames.Loader.hidden = true;
                //another elements
                break

            case ('ENTER_FALSH_EXCUTE'):
                //right frame
                Frames.FalseEx.hidden = false;
                Frames.Loader.hidden = true;
                //another elements
                break

            case ('VOTE'):
                //right frame
                Frames.Voter.hidden = false;
                Frames.Loader.hidden = true;
                //another elements
                break

            case ('SHOW_RESULTS'):
                //right frame
                Frames.Results.hidden = false;
                Frames.Loader.hidden = true;
                //another elements
                break

            case ('START_GAME'):

            default:

                break
        }

    },

    /** 
     * Здесь мы во время игры переключаем внутренние состояния игры, 
     * по итогам получения команд через вебсокет
     * 
     * Пока здесь в кучу и ответы сервера, и события
     * @param {{command:string,data:string,isAnserOnRequest:boolean,userName:string,sessionId:number, sessionPas:number,messageStatus:string}} incoming 
     * - содержимое JSON, пришедшего с сервера
     */
    eventListener(incoming) {
        //response
        if (incoming.isAnserOnRequest == false) //request
            webSocket.makeRequest(incoming.command, '', true)

        switch (incoming.command) {

            case "PONG": //response
                /**Соединение успешно установлено */
                logger('[info] PING PONG OK');
                break;

            case "ERROR":
                /** Сообщение об ошибке */
                logger('[error] сервер вернул ошибку: \n' + incoming.data)
                showAlert(incoming.data, 'red')
                break;

            case 'GET_GAME_STATUS': //response
                logger('[info] GET GAME STATUS');
                if (incoming.messageStatus == 'GOOD') {
                    switch (incoming.data) {
                        case "ADDING_USERS":
                            this.switchFrame('LOBBY')
                            break;

                        default:
                            showAlert(incoming.data)
                            this.switchFrame();
                            break;
                    }
                }

                break;

            case "CONNECT_TO_SESSION": //response
                /**
                 * Подсоединится к существующей.
                 *
                 *              Устанговление соединения для соединения с существующей скссией
                 *  1) Web страница при необходимости взаимодействия с сервером игры при помощи
                 *      веб сокетов устанавливает коннект.
                 *  2) Web страница отправляет реквест с командой CONNECT_TO_SESSION в котором соответсвующих полях
                 *      передается имя пользователя, id и пароль сессии.
                 */
                logger('[info] CONNECT action');
                if (incoming.messageStatus == 'GOOD') {
                    // showAlert('Есть контакт! ', 'green')
                    Frames.CheckID.hidden = true
                    Frames.Login.hidden = false
                    document.getElementById('checkGameLoader').hidden = true
                    Frames.Start.hidden = true //на случай автологина
                } else {
                    document.getElementById('checkGameLoader').hidden = true
                    showAlert(incoming.messageStatus + ' ' + incoming.data, 'orange')
                }
                break;

            case "CREATE_SESSION_IF_NOT_EXIST": //response
                /**
                 * Создать новую сессию.
                 *
                 *              Устанговление соединения для создания новой сессии
                 *  1) Web страница при необходимости взаимодействия с сервером игры при помощи
                 *      веб сокетов устанавливает коннект.
                 *  2) Web страница отправляет реквест с командой CREATE_SESSION_IF_NOT_EXIST в котором соответсвующих полях
                 *      передается имя пользователя, id и пароль сессии.
                 */
                logger('[info] CREATE_SESSION action');
                if (incoming.messageStatus == 'GOOD') {
                    logger('[info] сессия создана')
                    Frames.Start.hidden = true
                    Frames.Login.hidden = false
                    document.getElementById('createGameLoader').hidden = true
                } else {
                    document.getElementById('createGameLoader').hidden = true
                    showAlert(incoming.messageStatus + ' ' + incoming.data, 'orange')
                }
                break;

            case "ADD_USER": //response
                /** Добавить пользователя.
                 *  Веб страница сообщает серверу игры о том что она добавляет пользователя.
                 */
                logger('[action] ADD_USER action');
                if (incoming.messageStatus == 'GOOD') {
                    this.switchFrame('LOBBY')
                } else {
                    showAlert(incoming.messageStatus + ' ' + incoming.data, 'orange')
                }
                webSocket.makeRequest('GET_GAME_STATUS')
                document.getElementById('addGameLoader').hidden = true
                break;

            case "ADD_USER_EVENT": //request
                /** Событие добавить пользователя.
                 *  Сервер игры сообщает ВЕБ страницам от том, был добавлен пользователь
                 *  (в поле дата при этом передается список всех пользователей)
                 */
                let userArray = JSON.parse(incoming.data)
                let userList = [];
                for (let i = 0; i < userArray.length; i++) {
                    userList.push(userArray[i].name)
                }
                logger('[event] сервер прислал список игроков: ' + userList);

                updateUserList(userList);
                break;

            case "START_GAME": //response
                /** Команда начала игры (TX).
                 *  Страница сообщает серверу игры о том что пользователь нажал кнопку начала игры.
                 */
                if (incoming.messageStatus == 'GOOD') {
                    logger('[action] START_GAME action');
                    // this.switchFrame('START_GAME')
                } else {
                    showAlert(incoming.messageStatus + ' ' + incoming.data, 'orange')
                }
                break;

            case "START_GAME_EVENT": //request
                /** Событие начала игры.
                 *  Сервер игры сообщает ВЕБ страницам от том что игра началась
                 */
                logger('[event] START_GAME_EVENT');
                this.switchFrame('START_GAME')
                break;

            case "STOP_GAME": //response
                /** Команда остановки игры.
                 *  Веб страница сообщает серверу игры о том что пользователь нажал кнопку окончания игры.
                 */
                logger('[action] STOP_GAME action');
                break;

            case "STOP_GAME_EVENT": //request
                /** Команда остановки игры.
                 *  Сервер игры сообщает ВЕБ страницам от том что игра закончилась.
                 */
                logger('[event] STOP_GAME_EVENT');
                this.switchFrame('LOGIN')

                field_userName.value = ''
                field_sessionId.value = ''
                field_sessionPas.value = ''
                break;

            case "START_TIMER_EVENT": //request
                /** Запуск таймера.
                 *  Сервер игры сообщает ВЕБ страницам от том что игра закончилась.
                 *  В поле дата передается время оставшееся до истиечения срока работы таймера (мс)
                 */
                logger('[event] START_TIMER_EVENT');
                initProgressBar(incoming.data)
                break;

            case "ENTER_REAL_EXCUTE_EVENT": //request
                /** Введение реальной отмазки.
                 *  Сервер игры рассылает WEB страницам событие от которого необходимо отмазаться.
                 *  Страницы переходят в режим введения реальной отмазки.
                 */
                logger('[event] ENTER_REAL_EXCUTE_EVENT');
                document.getElementById("real-exec-data").innerHTML = incoming.data
                this.switchFrame('ENTER_REAL_EXCUTE')
                break;

            case "ENTER_FALSH_EXCUTE_EVENT":
                /** Введение фальшивой отмазки.
                 *  Сервер игры рассылает WEB страницам событие от которого необходимо отмазаться
                 *  так как это бы сделал игрок, чье событие сейчас отображается.
                 *  Страницы переходят в режим введения фальшивой отмазки.
                 */
                logger('[event] ENTER_FALSH_EXCUTE_EVENT');
                let evEx = JSON.parse(incoming.data)
                logger(`name: ${evEx.user}, event: ${evEx.event}`)
                if (evEx.user != field_userName.value) {
                    document.getElementById("false-exec-data").innerHTML = evEx.event
                    document.getElementById("false-exec-user").innerHTML = evEx.user
                    this.switchFrame('ENTER_FALSH_EXCUTE')
                } else {
                    //если это твой эвент
                    logger('[info] они врут про тебя, наслаждайся')
                    this.switchFrame('START_GAME')
                        // setTimeout(webSocket.makeRequest, 10000, 'SET_FALSH_EXCUTE', '') //костыль!!
                }

                break;

            case "VOTE_EVENT":
                /** Голосование.
                 *  Сервер игры рассылает WEB страницам варианты за кого можно проголосовать.
                 *  ВЕБ страница переходит в режим голосования.
                 */
                logger('[event] VOTE_EVENT');

                let evVt = JSON.parse(incoming.data)
                    // console.log(evVt)

                // вынести в отдельную функцию
                let btnColor = 'btn-primary'
                if (evVt.myQuestion != true) {
                    Frames.Voter.innerHTML = `<h3>Как думаешь, как ${evVt.eventHolder} будет отмазываться от ${evVt.event}?</h3>`
                } else {
                    Frames.Voter.innerHTML = `<h3>По мнению других, вот так бы ты отмазывался от ${evVt.event}:</h3>`
                    btnColor = 'btn-secondary'
                }
                for (let i = 0; i < evVt.rows.length; i++) {
                    let btn = document.createElement('button');
                    btn.innerHTML = evVt.rows[i].anser
                    btn.className = `btn ${btnColor} btn-block btn-lg`
                    if (evVt.rows[i].itsMe) //my variant
                        btn.setAttribute("disabled", "disabled")
                    else if (evVt.myQuestion != true) { //not my variant and not my question
                        btn.setAttribute("onclick", `webSocket.makeRequest('SET_VOTE','${evVt.rows[i].anser}')`)
                    } else { //not my variant , but my question
                        btn.setAttribute("onclick", `showAlert('Не тыкай, это ж они тебя раскрыть пытаются!')`)
                    }
                    Frames.Voter.append(btn)
                }
                // конец функции
                this.switchFrame('VOTE')
                break;

            case "ROUND": //response
                /** Закончить раунд голосования.
                 *  ВЕБ страница сообщает о том что пользователь нажал кнопку ROUND??
                 *  это значит что раунд окончен и все насмотрелись на результтаты голосования.
                 */
                logger('[action] ROUND action');
                if (this.currentFrame == 'SHOW_RESULTS') this.switchFrame('START_GAME')
                break;

            case "SHOW_RESULTS_EVENT":
                /** Демонстрирует результаты голосования пользователям.
                 *  Сервер игры рассылает WEB страницам результаты голосования.
                 *  Страница при этом переходит в режим просмотра результатов голосования.
                 */
            case "SHOW_FINAL_RESULTS_EVENT":
                /** Показывает пользователю результаты всей игры.
                 *  Сервер игры рассылает WEB страницам результаты финального голосования.
                 *  Страница при этом переходит в режим просмотра результатов финального голосования.
                 */
                logger('[event] SHOW_RESULTS_EVENT action');
                resetProgressBar()
                let evRes = JSON.parse(incoming.data)
                    // console.log(evRes)

                // вынести в отдельную функцию
                Frames.Results.innerHTML = `<h4>Вот как вы голосовали за ${evRes.event}</h4>`
                for (let i = 0; i < evRes.rows.length; i++) {
                    let card = document.createElement('div');
                    evRes.rows[i].trueTeller ? card.className = 'card border border-success' : card.className = 'card border border-secondary'
                    card.style.width = '100%'
                    card.innerHTML = '<div class = "card-header text-secondary"> Версия принадлежит ' + evRes.rows[i].name + '</div>' +
                        '<div class = "card-body">' +
                        '<h3 class="card-title">' + evRes.rows[i].excude + '</h3>' +
                        '</div>' +
                        '<div class = "card-footer">Заработано очков: ' + evRes.rows[i].pointsCount +
                        ' Всего очков: ' + evRes.rows[i].totalPointsCount + '</div>'
                    Frames.Results.append(card)
                }
                // конец функции

                setTimeout(this.switchFrame, 200, 'SHOW_RESULTS') //костыль!!

                let round = document.createElement('button');
                round.innerHTML = 'Завершить раунд'
                round.className = 'btn btn-primary btn-block btn-lg'
                round.setAttribute("onclick", "webSocket.makeRequest('ROUND')")
                Frames.Results.append(round)
                break;

            case "SET_REAL_EXCUTE":
                /** Ввести реальную отмазку.
                 *  ВЕБ страница сообщает серверу игры реальную отмазку которую ввел пользователь.
                 *  В поле data передается текс реальной отмазки.
                 */
                logger('[action] SET_REAL_EXCUTE, current frame = ', this.currentFrame)
                if (this.currentFrame == 'ENTER_REAL_EXCUTE') this.switchFrame('START_GAME')
                break;

            case "SET_FALSH_EXCUTE":
                /** Ввести фальшивую отмазку
                 *  ВЕБ страница сообщает серверу игры фальшивую отмазку которую ввел пользователь.
                 *  В поле data передается текс фальшивой отмазки.
                 */
                logger('[action] SET_FALSH_EXCUTE, current frame = ', this.currentFrame)
                if (this.currentFrame == 'ENTER_FALSH_EXCUTE') this.switchFrame('START_GAME')
                break

            case "SET_VOTE":
                /** Проголосовать за тот или иной вариант
                 *  ВЕБ страница сообщает серверу игры за какой вароиант проголосовал пользователь.
                 *  В поле data передается текс ответа, за который был отдан голос.
                 */

                //отправляем такое сообщение {"userName":"SashaGrey","sessionId":14,"sessionPas":88,"command":"SET_VOTE","data":"Жирная жопа","isAnserOnRequest":false,"messageStatus":"GOOD"}
                logger('[action] SET_VOTE')
                if (this.currentFrame == 'VOTE') this.switchFrame('START_GAME')
                break

            default:
                logger('[warning] Обработчик данной команды не задан:' + incoming.command);
                break;
        }
    }
}