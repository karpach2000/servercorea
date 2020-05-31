// объекты HTML превращаем в константы, чтобы потом с ними работать как с объектами js
//поля (значиния меняются)
const field_userName = document.getElementById("userName");
const field_sessionId = document.getElementById("sessionId");
const field_sessionPas = document.getElementById("sessionPas");
// поля (значения и содержимое меняются)
const field_VoteVariants = document.getElementById("mafia_voteVariants");
const field_SheriffVariants = document.getElementById("mafia_checkUserSheriffVariants");

//текстовые области (содержимое меняется)
const textArea_mafiaUsers = document.getElementById("mafia_users");
const text_mafiaLeader = document.getElementById("mafia_leader");
const text_mafiaRole = document.getElementById("mafia_role");
const table_mafiaUserVoteTable = document.getElementById("mafia_userVoteTable");

//кнопки (меняется активность и видимость)
const button_voteCitizen = document.getElementById("mafia_voteСitizenButton");
const button_voteMafia = document.getElementById("mafia_voteMafiaButton");
const button_startGame = document.getElementById("mafia_startGame");
const button_becameLeader = document.getElementById("mafia_wanToBeaLeader");

//блоки (меняется видимость)
const div_controls = document.getElementById("mafia_controller");
const div_mainVoter = document.getElementById("mafia_voter");
const div_sherifVoter = document.getElementById("mafia_SheriffVote");

//фреймы состояний игры (меняется видимость)
const frame_user = document.getElementById("user");
const frame_beforGame = document.getElementById("beforGame");
const frame_game = document.getElementById("game");
const frame_gameInfo = document.getElementById("gameInfo");

//тестовые блоки (меняется содержимое)
const span_leftTip = document.getElementById("leftTip");
const span_inGameTip = document.getElementById("inGameTip");
const span_inLobbyTip = document.getElementById("inLobbyTip");


const dictionary = {

}