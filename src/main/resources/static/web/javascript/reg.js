
function reg() {
    //console.log("GET TX: " + command)
    var xmlHttp = new XMLHttpRequest();
    var login = document.getElementById("login").value
    var password = document.getElementById("password").value
    var password2 = document.getElementById("password2").value

    let checkUsername = checkUser(login);
    if (checkUsername==false) {
        //alert - имя существует
    } else if (password != password2){
        //alert - пароли не совпадают
    } else {
        // отправляем на сервер
        xmlHttp.open("GET", "/users/reg/addUserU?login="+login+"&password="+password+
        "&password2="+password2, false); // false for synchronous request
        xmlHttp.send(null);
        // alert(xmlHttp.responseText)
        document.getElementById("alertMessage").innerHTML = xmlHttp.responseText ;
        $('#alertModal').modal(show)
    }
    
    //console.log("GET RX: " + xmlHttp.responseText)

}