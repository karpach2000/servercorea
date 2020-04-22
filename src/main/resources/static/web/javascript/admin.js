var currentMenuCountingItem="createUser";
function menuClick(id) {
    showMenuContent(id);
}

function showMenuContent(id) {

    document.getElementById(currentMenuCountingItem).hidden=true;
    document.getElementById(id).hidden=false;
    currentMenuCountingItem=id;
}