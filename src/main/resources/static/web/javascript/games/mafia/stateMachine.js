let GameState = {

    currentFrame: "",
    currentLeader: "",
    currentTime: "",

    myUserName: "",
    myRole: "",
    meAlive: true,
    countUsers: 0,

    /** 
     * запрашиваем у сервера, кто является ведущим в данный момент.
     */
    getLeader() {
        let leader = mafia_request("mafia_getLeader");
        if (this.currentLeader != leader) {
            this.currentLeader = leader;
            this.update("leader");
        }
    },

    /** 
     * присваиваем переменной имя из поля. Пока не уверен, что метод нужен.
     */
    getUserName() {
        this.myUserName = field_userName.value;
    },

    /** 
     * запрашиваем у сервера, какую роль мы занимаем в данный момент.
     */
    getRole() {
        if (this.meAlive == true)
            this.myRole = mafia_request("mafia_getRole");
        else this.myRole = "DEAD";
        this.update("role");
    },

    /**
     * запрашиваем у сервера текущее количество игроков в лобби
     */
    getCountUsers() {
        this.countUsers = mafia_request("mafia_count_users");
    },

    /** 
     * запрашиваем у сервера, каков статус игры в данный момент.
     */
    getGameState() {
        let ans = mafia_request("mafia_getGameState");
        if (ans == "ADD_USERS") {
            GameState.currentFrame = "LOBBY";
            GameState.update("frame");
        } else {
            GameState.currentFrame = "GAME";
            if (ans == "CITIZEN_VOTE") {
                GameState.currentTime == "DAY";
            } else if (ans == "MAFIA_VOTE") {
                GameState.currentTime == "NIGHT";
            }
            GameState.update("frame");
            GameState.update("dayOrNight");
        }

    },

    /** 
     * Здесь мы во время игры переключаем видимые состояния окна игры.
     * @param event leader; role; frame; dayOrNight; table
     */
    update(event = '', data = '') {
        switch (event) {

            case "leader":
                text_mafiaLeader.textContent = `Ведущий: ${this.currentLeader}`;

                if (GameState.currentLeader == field_userName.value) {
                    button_startGame.disabled = false;
                    button_becameLeader.disabled = true;
                } else {
                    button_startGame.disabled = true;
                    button_becameLeader.disabled = false;
                }
                break;

                /* Приводит окно к виду, соответсвующему роли данного игрока в игре.*/
            case "role":
                switch (GameState.myRole) {
                    case "LEADING":
                        div_controls.hidden = false;
                        div_mainVoter.hidden = true;
                        div_sherifVoter.hidden = true;
                        break;

                    case "CITIZEN":
                        div_controls.hidden = true
                        div_sherifVoter.hidden = true
                            // div_mainVoter.hidden = false
                        break;

                    case "SHERIFF":
                        div_controls.hidden = true
                            // div_mainVoter.hidden = false
                        break;

                    case "MAFIA":
                        div_controls.hidden = true
                        div_sherifVoter.hidden = true
                            // div_mainVoter.hidden = false
                        break;

                    case "DEAD":
                        div_controls.hidden = true;
                        div_mainVoter.hidden = true;
                        div_sherifVoter.hidden = true;
                        break;

                    default: //роль не входит в вышеперечисленные
                        div_controls.hidden = true;
                        div_mainVoter.hidden = true;
                        div_sherifVoter.hidden = true;
                        break;
                }
                //прописываем роль пользователю
                // text_mafiaRole.textContent = `Роль: ${GameState.myRole}`;
                this.showIngameHints();
                break;

            case "frame":
                switch (GameState.currentFrame) {
                    case "STOP_GAME":
                        frame_user.hidden = false;
                        frame_beforGame.hidden = true;
                        frame_game.hidden = true;
                        frame_gameInfo.hidden = true;

                        span_leftTip.innerHTML = '<p>Для создания новой игры:</p>' +
                            '<ol>' +
                            '    <li>Придумайте свое "Имя пользователя", "ID сессии", "Пароль сессии"</li>' +
                            '    <li>Передайте "Пароль сессии" и "ID сессии" остальным игрокам</li>' +
                            '    <li>Нажмите кнопку "Добавить себя"</li> ' +
                            '</ol> ' +
                            '<p>Для присоединения к существующей игре:</p>' +
                            '<ol> ' +
                            '    <li>Придумайте свое "Имя пользователя"</li> ' +
                            '    <li>Заполните поля "Пароль сессии" и "ID сессии" значениями, получеными от создателя игры</li>' +
                            '    <li>Нажмите кнопку "Добавить себя"</li>' +
                            '</ol>';
                        break;

                    case "LOBBY":
                        frame_user.hidden = true;
                        frame_beforGame.hidden = false;
                        frame_game.hidden = true;
                        frame_gameInfo.hidden = true;

                        this.getLeader();

                        span_inLobbyTip.innerHTML = '<p>Вы представились как <strong>' + field_userName.value + ' </strong></p>';

                        span_leftTip.innerHTML = '<p>Убедитесь, что все желающие знают ID и пароль сессии! ' +
                            'дождитесь, пока все присоединятся к игре.</p>' +
                            '<p>После нажатия на кнопку "Начать игру" присоединиться до конца раунда будет невозможно!</p>' +
                            '<p>Для начала игры вас должно быть не меньше 5 человек</p>';
                        break;

                    case "GAME":
                        frame_user.hidden = true;
                        frame_beforGame.hidden = true;
                        frame_game.hidden = false;
                        frame_gameInfo.hidden = false;

                        GameState.getRole();
                        this.update("dayOrNight");
                        break;
                }
                break;

            case "dayOrNight":
                this.getRole();
                if (GameState.currentTime == "DAY") {
                    button_voteCitizen.disabled = false
                    button_voteMafia.disabled = true

                    mafia_getСitizenVoteVariants();
                    if (this.myRole != 'DEAD')
                        div_mainVoter.hidden = false;
                    div_sherifVoter.hidden = true;
                }
                if (GameState.currentTime == "NIGHT") {
                    button_voteCitizen.disabled = true
                    button_voteMafia.disabled = false

                    mafia_getMafiaVoteVariants();
                    mafia_getSheriffCheckVariants();
                    // mafia_startGame()
                    if (this.myRole == 'CITIZEN') {
                        div_mainVoter.hidden = true;
                    }
                    if (this.myRole == 'SHERIFF') {
                        div_mainVoter.hidden = true;
                        div_sherifVoter.hidden = false;
                    }
                }
                this.showIngameHints();
                break;

            case "table":
                this.update("role");
                voteTable.show();
                break;

            default:
                //При неправильных или отсутствующих аргументах - берем и обновляем все сразу
                this.update("frame");
                this.update("table");
                break;
        }

    },

    /** 
     * показываем подсказки в зависимости от времени суток, роли и жив ли игрок.
     */
    showIngameHints() {
        if (GameState.currentTime == "NIGHT") {
            switch (GameState.myRole) {
                case 'LEADING':
                    span_inGameTip.innerHTML = '<p>Господин <strong> Ведущий ' + field_userName.value + nightBecome;
                    span_leftTip.innerHTML = '<p>Убедитесь, что:</p>' +
                        '<ul>\n' +
                        '<li>все мирные жители мирно спят, или по крайней мере правдоподобно притворяются,</li>\n' +
                        '<li>мафия не забыла, что она мафия, и отправилась убивать,</li>\n' +
                        '<li>шериф не забыл что он шериф, и пытается посмотреть, кто есть кто в этом городишке.</li>\n' +
                        '</ul>\n' +
                        '<p>Когда все сделали свой выбор, нажмите кнопку "Засчитать голоса мафии". Голоса будут' +
                        ' обработаны движком игры, засчитаны, и в игре наступит новый день.</p>\n';
                    break;

                case 'SHERIFF':
                    span_inGameTip.innerHTML = '<p>Господин <strong> Шериф ' + field_userName.value + nightBecome;
                    span_leftTip.innerHTML = '<p>Вы - единственный из мирных жителей, кто не спит этой ночью. ' +
                        'Это Ваш шанс - можно тихонько заглянуть к кому-то в окно, и узнать - спит ли он в своей кровати. ' +
                        'Если этот кто-то не спит - поздравляем, Вы обнаружили мафиози! </p>' +
                        '<p>Это все, что Вы можете сделать для своего города этой ночью. Но поверьте, это уже немало.</p>';
                    break;

                case 'CITIZEN':
                    span_inGameTip.innerHTML = '<p>Гражданин <strong>' + field_userName.value + nightBecome;
                    span_leftTip.innerHTML = '<p>Ночь - время мафии, не время для мирных жителей. ' +
                        'Все что Вам остается - тревожно спать и надеяться утром проснуться.</p>';
                    break;

                case 'MAFIA':
                    span_inGameTip.innerHTML = '<p>Господин <strong> Мафиози ' + field_userName.value + nightBecome;
                    span_leftTip.innerHTML = '<p>Ночь - это Ваше время, здесь вы почти всесильны. Выберете, кто из ' +
                        'мирных граждан не проснется утром.</p>' +
                        '<p>Будте внимательны! Иногда стоит проголосовать против другого Мафиози или даже против себя, ' +
                        'чтобы подставить Шерифа. Дело Мафии должно жить любой ценой, даже ценой жизни.</p>';
                    break;

                case 'DEAD':
                    span_inGameTip.innerHTML = '<p>В этом городе все знали Вас как <strong>' + field_userName.value +
                        ' </strong>. Но теперь Вы труп, и этой ночью можете спать спокойно.</p>';
                    span_leftTip.innerHTML = '<p>В городе наступает ночь, город засыпает, просыпается мафия... ' +
                        'А Вам остается только наблюдать за всем этим происходящим, как бестелесный призрак.</p>';
                    break;

                default:
                    span_inGameTip.innerHTML = '<p>Хер <strong> ' + field_userName.value + nightBecome;
                    span_leftTip.innerHTML = '<p>Ночная подсказка для неведомого хера</p>';
                    break;
            }
        } else if (GameState.currentTime == "DAY") {
            switch (GameState.myRole) {
                case 'LEADING':
                    span_inGameTip.innerHTML = '<p>Господин <strong> Ведущий ' + field_userName.value + dayBecome;
                    span_leftTip.innerHTML = '<p>Убедитесь, что:</p>' + '<ol>\n' +
                        '<li>Все жители проснулись. Если кто-то не проснулся, расскажите об этом. Смерть надо уважать.</li>\n' +
                        '<li>Все жители получили свою минутку на высказаться. Демократия и свобода слова - наше все.</li>\n' +
                        '<li>Жители сделали демократический выбор, кого сегодя они хотят убить. Если вдруг никого не хотят ' +
                        '- ничего страшного, еще успеют.</li>\n' + '</ol>\n' +
                        '<p>Когда все сделали свой выбор, приговоренный сказал свое последнее ' +
                        'слово, и никто больше не собирается менять своего мнения,  нажмите кнопку ' +
                        '"Засчитать голоса мафии". Голоса будут обработаны ' +
                        'движком игры, засчитаны, и в игре наступит новая ночь.</p>\n';
                    break;

                case 'SHERIFF':
                    span_inGameTip.innerHTML = '<p>Господин <strong> Шериф ' + field_userName.value + dayBecome;
                    span_leftTip.innerHTML = '<p>Сейчас Вы голосуете за того, кого сегодня днем казнят. ' +
                        'Подумайте хорошенько, стоит ли раскрыть себя городу, ведь Вас могут после этого убить. ' +
                        'Но иногда стоит пожертвовать собой ради искоренения Мафии, если данная Вами информация ' +
                        'гарантирует победу города.</p>';
                    break;

                case 'CITIZEN':
                    span_inGameTip.innerHTML = '<p>Гражданин <strong>' + field_userName.value + dayBecome;
                    span_leftTip.innerHTML = '<p>Сейчас Вы голосуете за того, кого сегодня днем казнят. ' +
                        'Подумайте хорошенько - стоит ли рисковать жизнью невинных ради призрачного ' +
                        'шанса убить виновного?</p>';
                    break;

                case 'MAFIA':
                    span_inGameTip.innerHTML = '<p>Господин <strong> Мафиози ' + field_userName.value + dayBecome;
                    span_leftTip.innerHTML = '<p>Сейчас Вы голосуете за того, кого сегодня днем казнят. ' +
                        'Подумайте хорошенько - никто (или почти никто) не знает, что Вы - Мафиози, это ' +
                        'Ваш шанс избавиться законным путем от кого-то неугодного Мафии. Но будьте ' +
                        'внимательны - не делайте это слишком явно, не компрометируйте себя!</p>';
                    break;

                case 'DEAD':
                    span_inGameTip.innerHTML = '<p>В этом городе все знали Вас как <strong>' + field_userName.value +
                        ' </strong>. Но теперь Вы труп, город просыпается, а Вы - нет.</p>';
                    span_leftTip.innerHTML = '<p>В городе день, и все горожане занимаются своими ' +
                        'обычными делами - ищут, кто из окружающих не выспался, потому что ночью ходил ' +
                        'убивать. Но Вас такие мелочи уже не волнуют. Мертвецов вообще мало что волнует.</p>';
                    break;
                default:
                    span_inGameTip.innerHTML = '<p>Хер <strong> ' + field_userName.value + dayBecome;
                    span_leftTip.innerHTML = '<p>Дневная подсказка для неведомого хера</p>';
                    break;
            }
        }
    },

    /** 
     * Здесь мы во время игры переключаем внутренние состояния игры, 
     * по итогам получения команд через вебсокет
     */
    eventListener(command, data) {

        switch (command) {
            case "addUserEvent":
                webSocket.send(`ok_${field_userName.value}`);
                textArea_mafiaUsers.textContent = data;
                break;

            case "leaderChandged":
                webSocket.send("ok");
                this.currentLeader = data;
                this.update("leader");
                break;

            case "updateVoteTable":
                webSocket.send("ok")
                voteTable.generateTable(data);
                this.update("table");
                break;

            case "startGameEvent":
                webSocket.send("ok");
                this.currentTime = "DAY";
                this.currentFrame = "GAME";
                this.update("dayOrNight");
                this.update("role");
                this.update("frame");
                break;

            case "stopGameEvent":
                webSocket.send("ok");
                this.currentFrame = "STOP_GAME";
                this.update("frame");
                alert("Игра закончена!");
                break;

            case "openMafiaVote":
                webSocket.send("ok");
                this.currentTime = "NIGHT";
                if (data != "") {
                    alert(`Игрок:${data} мертв!`);
                    this.update("role");
                } else
                    alert("К сожалению все живы.");
                this.update("dayOrNight");
                break;

            case "openСitizensVote":
                webSocket.send("ok");
                this.currentTime = "DAY";
                if (data != "") {
                    alert(`Игрок:${data} мертв!`);
                    this.update("role");
                } else
                    alert("К сожалению все живы.")
                this.update("dayOrNight");
                break;

            case "sheriffCheckedUser":
                alert(`Шериф выбрал для проверки  ${data}`);
                break;

            default:
                if ((field_userName.value.length > 0) &&
                    (field_sessionId.value.length > 0) &&
                    (field_sessionPas.value.length > 0)) {
                    mafia_login();
                }
                break;
        }
    }
}