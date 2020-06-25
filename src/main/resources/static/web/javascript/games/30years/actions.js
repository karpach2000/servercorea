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

$("#addUser").click(function() {
    console.log("[action] нажата кнопка 'Добавить себя'");

    if (field_userName.value == "") {
        console.log('[warning] Пустое поле "Имя пользователя"');
        showAlert('Заполните поле "Имя пользователя"')
        return
    } else if (field_sessionId.value == "") {
        console.log('[warning] Пустое поле "ID сессии"');
        showAlert('Заполните поле "ID сессии"')
        return
    } else if (field_sessionPas.value == "") {
        console.log('[warning] Пустое поле "Пароль сессии"');
        showAlert('Заполните поле "Пароль сессии"')
        return
    }
    showAlert('Вроде все правильно', 'green')
        //это должно будет уехать в стейты
    document.getElementById('userlogin').hidden = true;
    document.getElementById('userList').hidden = false;
    document.getElementById('beforeGame').hidden = true;
    document.getElementById('inGame').hidden = false;

    //request to server
    webSocket.makeRequest('CONNECT')
});

function countDownProgressBar(ms = 30000) {
    let msPerPersent = ms / 100;

}


/**
 * Управляем атрибутами прогрессбара
 * @param {*} persent - проценты от общего времени
 * @param {*} ms - отображаемое время
 */
function setProgressBarAttr(persent, ms) { //#fixme: if ms not defined, ms = 0. WTF?!

    if (persent > 30) {
        progressBar_left.innerHTML = ~~(ms / 1000)
        progressBar_done.innerHTML = ''
    } else if (persent < 0) {
        progressBar_left.innerHTML = ''
        progressBar_done.innerHTML = 'Время вышло!'
    } else {
        progressBar_left.innerHTML = ''
        progressBar_done.innerHTML = ~~(ms / 1000)
    }

    progressBar_left.setAttribute('aria-valuenow', persent)
    progressBar_left.setAttribute('style', 'width: ' + persent + '%')
    progressBar_done.setAttribute('aria-valuenow', (100 - persent))
    progressBar_done.setAttribute('style', 'width: ' + (100 - persent) + '%')
}