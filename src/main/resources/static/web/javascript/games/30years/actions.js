$(function() {
    $('[data-toggle="tooltip"]').tooltip()
})

function updateUserList(array) {
    document.getElementById("userTable").innerHTML = ''
    for (let i = 0; i < array.length - 1; i++) {
        let row = document.createElement('tr');
        row.innerHTML =
            '<td class="av p-0">' +
            '   <img' +
            '       class="img-fluid rounded m-0"' +
            '       width="50px"' +
            '       src="/web/images/avatar/Roundicons-0' + (i + 1) + '.png" ' +
            '       alt="">' +
            '</td>' +
            '<td ' +
            '   data-toggle="tooltip"' +
            '   data-placement="left" ' +
            '   title=' +
            (array[i] == field_userName.value ? '"Это типа ты"' : '"Это еще какой-то хер"') +
            '>' +
            array[i] +
            '</td>' +
            '<td>?</td>'
        document.getElementById("userTable").append(row)
    }
}
/**
 * Функция выводит алерт в виде модуля на странице, без блокирующего алерта
 * @param {string} message - выводимое сообщение, к которому добавится префикс
 * @param {string} color - orange, green,red, blue - возможные цвета алерта
 */
function showAlert(message, color = 'orange') {
    let div = document.createElement('div');
    let alertStyle;

    if (color == 'green') alertStyle = 'success';
    else if (color == 'red') alertStyle = 'danger';
    else if (color == 'blue') alertStyle = 'primary';
    else alertStyle = 'warning';

    div.className = "alert alert-dismissible fade show alert-" + alertStyle;
    div.setAttribute('role', "alert")
    div.innerHTML = '<strong>' + prefix + message + '</strong>' +
        '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
        '<span aria-hidden="true">&times;</span></button>';
    document.getElementById("alertContainer").append(div)
}

$("#createGame").click(function() {
    logger("[action] нажата кнопка 'Создать игру'");
    document.getElementById('createGameLoader').hidden = true

    if (field_userName.value == "") {
        logger('[warning] Пустое поле "Имя пользователя"');
        showAlert('Заполните поле "Имя пользователя"')
        return
    } else if (field_sessionId.value == "") {
        logger('[warning] Пустое поле "ID сессии"');
        showAlert('Заполните поле "ID сессии"')
        return
    } else if (field_sessionPas.value == "") {
        logger('[warning] Пустое поле "Пароль сессии"');
        showAlert('Заполните поле "Пароль сессии"')
        return
    }

    document.getElementById('createGameLoader').hidden = false
        //request to server
    webSocket.makeRequest('CREATE_SESSION_IF_NOT_EXIST')
});

$("#joinGame").click(function() {
    logger("[action] нажата кнопка 'Присоединиться к игре'");
    document.getElementById('joinGameLoader').hidden = true

    if (field_userName.value == "") {
        logger('[warning] Пустое поле "Имя пользователя"');
        showAlert('Заполните поле "Имя пользователя"')
        return
    } else if (field_sessionId.value == "") {
        logger('[warning] Пустое поле "ID сессии"');
        showAlert('Заполните поле "ID сессии"')
        return
    } else if (field_sessionPas.value == "") {
        logger('[warning] Пустое поле "Пароль сессии"');
        showAlert('Заполните поле "Пароль сессии"')
        return
    }

    document.getElementById('joinGameLoader').hidden = false
        //request to server
    webSocket.makeRequest('CONNECT_TO_SESSION')
});

$('#startGame').click(function() {
    webSocket.makeRequest('START_GAME')
})

$('#stopGame').click(function() {
    webSocket.makeRequest('STOP_GAME')
})

function initProgressBar(ms = 30000) {
    setProgressBarAttr(100, ms)
    progressBar.hidden = false //потом уйдет в стейты
    countDownProgressBar(100, ms, ~~(ms / 200))

}

function resetProgressBar() {
    progressBar.hidden = true
}

/**
 * рекурсивный таймер обратного отсчета для прогресс бара
 * @param {number} persent - оставшийся процент времени
 * @param {number} ms - отображаемое значение, оставшееся время
 * @param {number} step - шаг изменения в полпроцента
 */
function countDownProgressBar(persent, ms, step) {
    setProgressBarAttr(persent, ms)
    if (persent < 0) {
        setProgressBarAttr(-1, ms)
        return logger('[info] Время вышло!')
    } else {
        logger('[info] Осталось миллисекунд:' + ms)
        setTimeout(countDownProgressBar, step, persent - 0.5, ms - step, step);
    }
}


/**
 * Управляем атрибутами прогрессбара
 * @param {number} persent - проценты от общего
 * @param {number} ms - отображаемое время
 */
function setProgressBarAttr(persent, ms) { //#fixme: if ms not defined, ms = 0. WTF?!

    if (persent > 30) {
        progressBar_left.childNodes[1].innerText = ~~(ms / 1000)
        progressBar_done.childNodes[1].innerText = ''
    } else if (persent < 0) {
        progressBar_left.childNodes[1].innerText = ''
        progressBar_done.childNodes[1].innerText = 'Время вышло!'
    } else {
        progressBar_left.childNodes[1].innerText = ''
        progressBar_done.childNodes[1].innerText = ~~(ms / 1000)
    }

    progressBar_left.setAttribute('aria-valuenow', persent)
    progressBar_left.setAttribute('style', 'width: ' + persent + '%')
    progressBar_done.setAttribute('aria-valuenow', (100 - persent))
    progressBar_done.setAttribute('style', 'width: ' + (100 - persent) + '%')
}