
function reg() {
    //console.log("GET TX: " + command)
    var xmlHttp = new XMLHttpRequest();
    var login = document.getElementById("login").value
    var password = document.getElementById("password").value
    var password2 = document.getElementById("password2").value
    xmlHttp.open("GET", "/users/reg/addUserU?login="+login+"&password="+password+
        "&password2="+password2, false); // false for synchronous request
    xmlHttp.send(null);
    //console.log("GET RX: " + xmlHttp.responseText)
    alert(xmlHttp.responseText)

}