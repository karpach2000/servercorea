function toggleMenu()
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", "/session/getCurrentLogin", false);
    xmlHttp.send(null);
    let currentUser = xmlHttp.responseText;

    if (currentUser == "anonymousUser" || currentUser == undefined){
        toggleForAnon();
    } else {
        toggleForUser(currentUser);
    }
}

function toggleForAnon () {
    document.getElementById("buttonUser").hidden = true;
    document.getElementById("buttonLogin").hidden = false;
    document.getElementById("buttonLogout").hidden = true;
    document.getElementById("navLinkSettings").classList.add('disabled');
}

function toggleForUser (username) {
    document.getElementById("buttonUser").textContent = username;
    document.getElementById("buttonUser").hidden = false;
    document.getElementById("buttonLogin").hidden = true;
    document.getElementById("buttonLogout").hidden = false;
    document.getElementById("navLinkSettings").classList.remove('disabled');    
}