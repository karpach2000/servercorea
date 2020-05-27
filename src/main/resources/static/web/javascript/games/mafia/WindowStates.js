/**
 * Изменяет состояние окна.
 * (приводит его в состояние соответсвующее текущей таблицы состояния)
 */
class WindowStates {
    /*****ROLE_POSITIONS*****/
    leaderPosition() {
        div_controls.hidden = false
        div_mainVoter.hidden = true
        div_sherifVoter.hidden = true
    }
    mafiaPosition() {
        div_controls.hidden = true
        div_sherifVoter.hidden = true
        div_mainVoter.hidden = false
    }
    citizenPosition() {
        div_controls.hidden = true
        div_sherifVoter.hidden = true
        div_mainVoter.hidden = false
    }
    sheriffPosition() {
        div_controls.hidden = true
            // div_sherifVoter.hidden = false
        div_mainVoter.hidden = false
    }

    /*****GAME_POSITIONS*****/

    stopGamePosition() {
        frame_user.hidden = false
        frame_beforGame.hidden = true
        frame_game.hidden = true
        frame_gameInfo.hidden = true

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
    }

    beforGamePosition() {
        frame_user.hidden = true
        frame_beforGame.hidden = false
        frame_game.hidden = true
        frame_gameInfo.hidden = true

        this.whoIsLeader();

        span_inLobbyTip.innerHTML = '<p>Вы представились как <strong>' + field_userName.value + ' </strong></p>';

        span_leftTip.innerHTML = '<p>Убедитесь, что все желающие знают ID и пароль сессии! ' +
            'дождитесь, пока все присоединятся к игре.</p>' +
            '<p>После нажатия на кнопку "Начать игру" присоединиться до конца раунда будет невозможно!</p>' +
            '<p>Для начала игры вас должно быть не меньше 5 человек</p>';
    }

    whoIsLeader() {

        mafia_getLeader();
        if (currentLeader == field_userName.value) {
            button_startGame.disabled = false;
            button_becameLeader.disabled = true;
        } else {
            button_startGame.disabled = true;
            button_becameLeader.disabled = false;
        }
    }

    gamePositionCitizenVote() {
        frame_user.hidden = true
        frame_beforGame.hidden = true
        frame_game.hidden = false
        frame_gameInfo.hidden = false

        button_voteCitizen.disabled = false
        button_voteMafia.disabled = true

        span_inGameTip.innerHTML = '<p>В этом городе все знают Вас как <strong>' + field_userName.value + ' </strong></p><p>Сейчас в городе день</p>';
        span_leftTip.innerHTML = '<p>В городе день, и все горожане занимаются своими ' +
            'обычными делами - ищут, кто из окружающих не выспался, потому что ночью ходил убивать</p>';

        div_sherifVoter.hidden = true;
    }
    gamePositionMafiaVote() {
        frame_user.hidden = true
        frame_beforGame.hidden = true
        frame_game.hidden = false
        frame_gameInfo.hidden = false

        button_voteCitizen.disabled = true
        button_voteMafia.disabled = false

        span_inGameTip.innerHTML = '<p>В этом городе все знают Вас как <strong>' + field_userName.value + ' </strong></p><p>Сейчас в городе ночь</p>';
        span_leftTip.innerHTML = '<p>В городе наступает ночь, город засыпает, просыпается мафия...</p>';

        if (role == 'SHERIFF') div_sherifVoter.hidden = false
    }

}