let GameState = {

    currentFrame: "",
    myUserName: "",
    countUsers: 0,

    /** 
     * Здесь мы во время игры переключаем внутренние состояния игры, 
     * по итогам получения команд через вебсокет
     * 
     * Пока здесь в кучу и ответы сервера, и события
     * @param {{command:string,data:string,isAnswer:boolean,userName:string,sessionId:number, sessionPas:number}} incoming 
     * - содержимое JSON, пришедшего с сервера
     */
    eventListener(incoming) {
        switch (incoming.command) {

            case "PONG":
                /**Соединение успешно установлено */
                logger('[info] PING PONG OK');
                break;

            case "ERROR":
                /** Сообщение об ошибке */
                logger('[error] сервер вернул ошибку: \n' + incoming.data)
                break;

            case "CONNECT":
                /** Соединится с сервером.
                 *  Установление соединения
                 *  1) Web страница при необходимости взаимодействия с сервером игры при помощи
                 *      веб сокетов устанавливает коннект.
                 *  2) Web страница отправляет реквест с командой CONNECT в котором соответсвующих полях
                 *      передается имя пользователя, id и пароль сессии.
                 */
                logger('[info] CONNECT action');
                webSocket.makeRequest('ADD_USER')
                break;


            case "ADD_USER":
                /** Добавить пользователя.
                 *  Веб страница сообщает серверу игры о том что она добавляет пользователя.
                 */
                logger('[action] ADD_USER action');
                //это должно будет уехать в стейты
                document.getElementById('userlogin').hidden = true;
                document.getElementById('userList').hidden = false;
                document.getElementById('beforeGame').hidden = true;
                document.getElementById('inGame').hidden = false;
                break;

            case "ADD_USER_EVENT":
                /** Событие добавить пользователя.
                 *  Сервер игры сообщает ВЕБ страницам от том, был добавлен пользователь
                 *  (в поле дата при этом передается список всех пользователей)
                 */
                logger('[event] сервер прислал список игроков: \n' + incoming.data);
                let userList = incoming.data.split('\n')
                for (let i in userList) userList[i] = userList[i].trim()
                logger(userList);

                updateUserList(userList);
                break;

            case "START_GAME":
                /** Команда начала игры (TX).
                 *  Страница сообщает серверу игры о том что пользователь нажал кнопку начала игры.
                 */
                logger('[action] START_GAME action');
                break;

            case "START_GAME_EVENT":
                /** Событие начала игры.
                 *  Сервер игры сообщает ВЕБ страницам от том что игра началась
                 */
                logger('[event] START_GAME_EVENT');
                break;

            case "STOP_GAME":
                /** Команда остановки игры.
                 *  Веб страница сообщает серверу игры о том что пользователь нажал кнопку окончания игры.
                 */
                logger('[action] STOP_GAME action');
                break;

            case "STOP_GAME_EVENT":
                /** Команда остановки игры.
                 *  Сервер игры сообщает ВЕБ страницам от том что игра закончилась.
                 */
                logger('[event] STOP_GAME_EVENT');
                break;

            case "START_TIMER_EVENT":
                /** Запуск таймера.
                 *  Сервер игры сообщает ВЕБ страницам от том что игра закончилась.
                 *  В поле дата передается время оставшееся до истиечения срока работы таймера (мс)
                 */
                logger('[event] START_TIMER_EVENT');
                initProgressBar(incoming.data)
                break;

            case "ENTER_REAL_EXCUTE_EVENT":
                /** Введение реальной отмазки.
                 *  Сервер игры рассылает WEB страницам событие от которого необходимо отмазаться.
                 *  Страницы переходят в режим введения реальной отмазки.
                 */
                logger('[event] ENTER_REAL_EXCUTE_EVENT');
                document.getElementById("awful-event").innerHTML = incoming.data
                break;

            case "SENTER_FALSH_EXCUTE_EVENT":
                /** Введение фальшивой отмазки.
                 *  Сервер игры рассылает WEB страницам событие от которого необходимо отмазаться
                 *  так как это бы сделал игрок, чье событие сейчас отображается.
                 *  Страницы переходят в режим введения фальшивой отмазки.
                 */
                logger('[event] ENTER_FALSH_EXCUTE_EVENT');
                document.getElementById("awful-event").innerHTML = incoming.data
                break;

            case "VOTE_EVENT":
                /** Голосование.
                 *  Сервер игры рассылает WEB страницам варианты за кого можно проголосовать.
                 *  ВЕБ страница переходит в режим голосования.
                 */
                logger('[event] VOTE_EVENT');
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
                logger('[event] SHOW_RESULTS_EVENT action');
                break;

            case "SHOW_FINAL_RESULTS_EVENT":
                /** Показывает пользователю результаты всей игры.
                 *  Сервер игры рассылает WEB страницам результаты финального голосования.
                 *  Страница при этом переходит в режим просмотра результатов финального голосования.
                 */
                logger('[event] SHOW_FINAL_RESULTS_EVENT action');
                break;

            case "SET_REAL_EXCUTE":
                /** Ввести реальную отмазку.
                 *  ВЕБ страница сообщает серверу игры реальную отмазку которую ввел пользователь.
                 *  В поле data передается текс реальной отмазки.
                 */
                logger('[action] SET_REAL_EXCUTE ')
                break;

            case "SET_FALSH_EXCUTE":
                /** Ввести фальшивую отмазку
                 *  ВЕБ страница сообщает серверу игры фальшивую отмазку которую ввел пользователь.
                 *  В поле data передается текс фальшивой отмазки.
                 */
                logger('[action] SET_FALSH_EXCUTE')
                break

            case "SET_VOTE":
                /** Проголосовать за тот или иной вариант
                 *  ВЕБ страница сообщает серверу игры за какой вароиант проголосовал пользователь.
                 *  В поле data передается текс ответа, за который был отдан голос.
                 */
                logger('[action] SET_VOTE')
                break

            default:
                logger('[warning] Обработчик данной команды не задан:' + incoming.command);
                break;
        }
    }
}