let GameState = {

    currentFrame: "",
    myUserName: "",
    countUsers: 0,

    /**
     * функция для переключения видимости фреймов, в зависимости от состояния игры. 
     * @param {string} frame LOGIN, LOBBY, START_GAME, ENTER_EXCUTE, VOTE, SHOW_RESULTS
     */
    switchFrame(frame) {
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
                Frames.Login.hidden = false;
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
                //игра стартовала, но еще никаких данных не пришло
                //делаем ничего - остальное сделано до нас
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
                    showAlert('Есть контакт! ', 'green')
                    webSocket.makeRequest('ADD_USER')
                } else {
                    document.getElementById('joinGameLoader').hidden = true
                    document.getElementById('createGameLoader').hidden = true
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
                    showAlert('Есть контакт! ', 'green')
                    webSocket.makeRequest('ADD_USER')
                } else {
                    document.getElementById('joinGameLoader').hidden = true
                    document.getElementById('createGameLoader').hidden = true
                    showAlert(incoming.messageStatus + ' ' + incoming.data, 'orange')
                }
                break;

            case "ADD_USER": //response
                /** Добавить пользователя.
                 *  Веб страница сообщает серверу игры о том что она добавляет пользователя.
                 */
                logger('[action] ADD_USER action');
                //это должно будет уехать в стейты
                if (incoming.messageStatus == 'GOOD') {
                    this.switchFrame('LOBBY')
                } else {
                    showAlert(incoming.messageStatus + ' ' + incoming.data, 'orange')
                }
                document.getElementById('joinGameLoader').hidden = true
                document.getElementById('createGameLoader').hidden = true
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
                let ev = JSON.parse(incoming.data)
                logger(`name: ${ev.user}, event: ${ev.event}`)
                if (ev.user != field_userName.value) {
                    document.getElementById("false-exec-data").innerHTML = ev.event
                    document.getElementById("false-exec-user").innerHTML = ev.user
                    this.switchFrame('ENTER_FALSH_EXCUTE')
                } else {
                    //если это твой эвент
                    logger('[info] они врут про тебя, наслаждайся')
                }

                break;

            case "VOTE_EVENT":
                /** Голосование.
                 *  Сервер игры рассылает WEB страницам варианты за кого можно проголосовать.
                 *  ВЕБ страница переходит в режим голосования.
                 */

                // {
                //     "event":"Кальянная на яблочково",
                //     "myQuestion":false,
                //     "rows":[
                //         {
                //             "anser":"Жирная жопа",
                //             "itsMe":false
                //         },{
                //             "anser":"_Худые ляшки",
                //             "itsMe":true
                //         },{
                //             "anser":"_Я унылое говно",
                //             "itsMe":false
                //         },{
                //             "anser":"_Понос",
                //             "itsMe":false
                //         }
                //     ]
                // }

                logger('[event] VOTE_EVENT');

                let ev = JSON.parse(incoming.data)
                logger(`event: ${ev.event}, myQuestion: ${ev.myQuestion},answers: ${ev.rows}`)

                this.switchFrame('VOTE')
                break;

            case "ROUND":
                /** Закончить раунд голосования.
                 *  ВЕБ страница сообщает о том что пользователь нажал кнопку ROUND??
                 *  это значит что раунд окончен и все насмотрелись на результтаты голосования.
                 */
                logger('[action] ROUND action');
                break;

            case "SHOW_RESULTS_EVENT":
                /** Демонстрирует результаты голосования пользователям.
                 *  Сервер игры рассылает WEB страницам результаты голосования.
                 *  Страница при этом переходит в режим просмотра результатов голосования.
                 */

                //{
                //    "event":"Кальянная на яблочково",
                //    "rows":[
                //      {
                //        "name":"Petr",
                //        "trueTeller":true,
                //        "excude":"Жирная жопа",
                //        "selectedAuthor":"",
                //        "itsMe":true,
                //        "pointsCount":2,
                //        "totalPointsCount":2
                //      },{
                //        "name":"SashaGrey",
                //        "trueTeller":false,
                //        "excude":"_Худые ляшки",
                //        "selectedAuthor":"Petr",
                //        "itsMe":false,
                //        "pointsCount":11,
                //        "totalPointsCount":11
                //      },{
                //        "name":"Gena",
                //        "trueTeller":false,
                //        "excude":"_Я унылое говно",
                //        "selectedAuthor":"SashaGrey",
                //        "itsMe":false,
                //        "pointsCount":0,
                //        "totalPointsCount":0
                //      },{
                //        "name":"Vasa",
                //        "trueTeller":false,
                //        "excude":"_Понос",
                //        "selectedAuthor":"Petr",
                //        "itsMe":false,
                //        "pointsCount":10,
                //        "totalPointsCount":10
                //      }
                //    ]
                //  }

                let ev = JSON.parse(incoming.data)
                logger(`event: ${ev.event},variants: ${ev.rows}`)

                logger('[event] SHOW_RESULTS_EVENT action');
                this.switchFrame('SHOW_RESULTS')
                break;

            case "SHOW_FINAL_RESULTS_EVENT":
                /** Показывает пользователю результаты всей игры.
                 *  Сервер игры рассылает WEB страницам результаты финального голосования.
                 *  Страница при этом переходит в режим просмотра результатов финального голосования.
                 */
                logger('[event] SHOW_FINAL_RESULTS_EVENT action');
                this.switchFrame('SHOW_RESULTS')
                break;

            case "SET_REAL_EXCUTE":
                /** Ввести реальную отмазку.
                 *  ВЕБ страница сообщает серверу игры реальную отмазку которую ввел пользователь.
                 *  В поле data передается текс реальной отмазки.
                 */
                logger('[action] SET_REAL_EXCUTE ')
                this.switchFrame('START_GAME')
                break;

            case "SET_FALSH_EXCUTE":
                /** Ввести фальшивую отмазку
                 *  ВЕБ страница сообщает серверу игры фальшивую отмазку которую ввел пользователь.
                 *  В поле data передается текс фальшивой отмазки.
                 */
                logger('[action] SET_FALSH_EXCUTE')
                this.switchFrame('START_GAME')
                break

            case "SET_VOTE":
                /** Проголосовать за тот или иной вариант
                 *  ВЕБ страница сообщает серверу игры за какой вароиант проголосовал пользователь.
                 *  В поле data передается текс ответа, за который был отдан голос.
                 */

                //отправляем такое сообщение {"userName":"SashaGrey","sessionId":14,"sessionPas":88,"command":"SET_VOTE","data":"Жирная жопа","isAnserOnRequest":false,"messageStatus":"GOOD"}
                logger('[action] SET_VOTE')
                this.switchFrame('START_GAME')
                break

            default:
                logger('[warning] Обработчик данной команды не задан:' + incoming.command);
                break;
        }
    }
}