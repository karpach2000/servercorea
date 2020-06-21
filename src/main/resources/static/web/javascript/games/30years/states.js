let GameState = {

    currentFrame: "",
    myUserName: "",
    countUsers: 0,

    /** 
     * Здесь мы во время игры переключаем внутренние состояния игры, 
     * по итогам получения команд через вебсокет
     */
    eventListener(incoming) {
        switch (incoming.command) {
            case "PONG":
                console.log('[info] PING PONG OK');
                break;

            case "CONNECT":
                console.log('[info] CONNECT action');
                webSocket.makeRequest('ADD_USER')
                break;

            case "ADD_USER":
                console.log('[info] ADD_USER action');
                if (incoming.isAnserOnRequest == false) {
                    console.log('[info] сервер прислал список игроков: \n' + incoming.data);
                    let userList = incoming.data.split('\n')
                    for (let i in userList) userList[i] = userList[i].trim()
                    console.log(userList);

                    updateUserList(userList);
                }


                break;

            case "START_GAME":
                console.log('[info] START_GAME action');
                break;

            case "STOP_GAME":
                console.log('[info] STOP_GAME action');
                break;

            case "SET_REAL_EXCUTE":
                console.log('[info] SET_REAL_EXCUTE action');
                break;

            case "SET_FALSH_EXCUTE":
                console.log('[info] SET_FALSH_EXCUTE action');
                break;

            case "SET_VOTE":
                console.log('[info] SET_VOTE action');
                break;

            case "ROUND":
                console.log('[info] ROUND action');
                break;

            case "SHOW_RESULTS_EVENT":
                console.log('[info] SHOW_RESULTS_EVENT action');
                break;

            case "SHOW_FINAL_RESULTS_EVENT":
                console.log('[info] SHOW_FINAL_RESULTS_EVENT action');
                break;

            default:
                console.log('[warning] Обработчик данной команды не задан');
                console.log(incoming);
                break;
        }
    }
}