/**
 * Изменяет состояние окна.
 * (приводит его в состояние соответсвующее текущей таблицы состояния)
 */
class WindowStates {
    /*****ROLE_POSITIONS*****/
    leaderPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = false
        document.getElementById("mafia_voteMafiaButton").hidden = false
        document.getElementById("mafia_voter").hidden = true

    }
    mafiaPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = true
        document.getElementById("mafia_voteMafiaButton").hidden = true
        document.querySelectorAll(".tableCheck").forEach(element => element.hidden = true);
    }
    citizenPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = true
        document.getElementById("mafia_voteMafiaButton").hidden = true
        document.querySelectorAll(".tableCheck").forEach(element => element.hidden = true);
    }
    sheriffPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = true
        document.getElementById("mafia_voteMafiaButton").hidden = true
        document.getElementById("mafia_checkUserSheriffVariants").hidden = false
    }

    /*****GAME_POSITIONS*****/

    stopGamePosition() {
        document.getElementById("user").hidden = false
        document.getElementById("beforGame").hidden = true
        document.getElementById("game").hidden = true
        document.getElementById("gameInfo").hidden = true

        document.getElementById("leftTip").innerHTML = '<p>Для создания новой игры:</p>' +
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
        document.getElementById("user").hidden = true
        document.getElementById("beforGame").hidden = false
        document.getElementById("game").hidden = true
        document.getElementById("gameInfo").hidden = true

        document.getElementById("leftTip").innerHTML = '<p>Убедитесь, что все желающие знают ID и пароль сессии! ' +
            'дождитесь, пока все присоединятся к игре.</p>' +
            '<p>После нажатия на кнопку "Начать игру" присоединиться до конца раунда будет невозможно!</p>' +
            '<p>Для начала игры вас должно быть не меньше 5 человек</p>';
    }
    gamePositionCitizenVote() {
        document.getElementById("user").hidden = true
        document.getElementById("beforGame").hidden = true
        document.getElementById("game").hidden = false
        document.getElementById("gameInfo").hidden = false
        document.getElementById("mafia_voteСitizenButton").disabled = false
        document.getElementById("mafia_voteMafiaButton").disabled = true

        document.getElementById("inGameTip").innerHTML = '<p>В этом городе все знают Вас как <strong>' + myUserName + ' </strong></p><p>Сейчас в городе день</p>';
        document.getElementById("leftTip").innerHTML = '<p>В городе день, и все горожане занимаются своими ' +
            'обычными делами - ищут, кто из окружающих не выспался, потому что ночью ходил убивать</p>';

        document.getElementById("mafia_SheriffVote").hidden = true;
    }
    gamePositionMafiaVote() {
        document.getElementById("user").hidden = true
        document.getElementById("beforGame").hidden = true
        document.getElementById("game").hidden = false
        document.getElementById("gameInfo").hidden = false
        document.getElementById("mafia_voteСitizenButton").disabled = true
        document.getElementById("mafia_voteMafiaButton").disabled = false

        document.getElementById("inGameTip").innerHTML = '<p>В этом городе все знают Вас как <strong>' + myUserName + ' </strong></p><p>Сейчас в городе ночь</p>';
        document.getElementById("leftTip").innerHTML = '<p>В городе наступает ночь, город засыпает, просыпается мафия...</p>';

        if (role == 'SHERIFF') document.getElementById("mafia_SheriffVote").hidden = false
    }

}