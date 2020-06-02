
function account() {

    var xmlHttp = new XMLHttpRequest();
    var login = document.getElementById("login").value
    var password = document.getElementById("password").value

    xmlHttp.open("GET", "/settings/account/update?login="+login+"&password="+password, false); // false for synchronous request
    xmlHttp.send(null);
}

function checkUser(username){
    return true
}