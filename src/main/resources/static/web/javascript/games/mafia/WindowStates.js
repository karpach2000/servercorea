/**
 * Изменяет состояние окна.
 * (приводит его в состояние соответсвующее текущей таблицы состояния)
 */
class WindowStates
{
    /*****ROLE_POSITIONS*****/
    leaderPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = false
        document.getElementById("mafia_voteMafiaButton").hidden = false
        document.getElementById("mafia_voteVariants").hidden = true
    }
    mafiaPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = true
        document.getElementById("mafia_voteMafiaButton").hidden = true
    }
    citizenPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = true
        document.getElementById("mafia_voteMafiaButton").hidden = true
    }
    sheriffPosition() {
        document.getElementById("mafia_voteСitizenButton").hidden = true
        document.getElementById("mafia_voteMafiaButton").hidden = true
        document.getElementById("mafia_checkUserSheriffVariants").hidden = false
    }

    /*****GAME_POSITIONS*****/

    stopGamePosition() {
        document.getElementById("mafia_user").hidden = false
        document.getElementById("mafia_beforGame").hidden = true
        document.getElementById("mafia_game").hidden = true
    }
    beforGamePosition() {
        document.getElementById("mafia_user").hidden = true
        document.getElementById("mafia_beforGame").hidden = false
        document.getElementById("mafia_game").hidden = true
    }
    gamePositionCitizenVote() {
        document.getElementById("mafia_user").hidden = true
        document.getElementById("mafia_beforGame").hidden = true
        document.getElementById("mafia_game").hidden = false
        document.getElementById("mafia_citizenVote").hidden = false
        document.getElementById("mafia_mafiaVote").hidden = true
    }
    gamePositionMafiaVote() {
        document.getElementById("mafia_user").hidden = true
        document.getElementById("mafia_beforGame").hidden = true
        document.getElementById("mafia_game").hidden = false
        document.getElementById("mafia_citizenVote").hidden = true
        document.getElementById("mafia_mafiaVote").hidden = false
    }

}