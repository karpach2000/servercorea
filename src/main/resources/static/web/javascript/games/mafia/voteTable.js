let voteTable = {

    table: "<table>",
    parsedJSON: {},

    generateTable(data) {
        this.parseTable(data);

        var html =
            '            <table class="table table-bordered table-hover">\n' +
            '                <thead class="thead-light">\n' +
            '                <tr>\n' +
            '                    <th class="tableLogin" >Игрок</th>\n' +
            '                    <th class="tableRole"  >Роль</th>\n' +
            '                    <th class="tableStatus">Статус</th>\n' +
            '                    <th class="tableVotes" >Количество голосов</th>\n' +
            '                    <th class="tableCheck" >Проверка шерифа</th>\n' +
            '                </tr>\n' +
            '                </thead>\n' +
            '                <tbody class="table table-striped">';

        for (let i in this.parsedJSON) {
            var line =
                '                    <tr   class="table-data">\n' +
                '                        <td class="tableLogin" >' + this.parsedJSON[i].name + '</td>\n' +
                '                        <td class="tableRole"  >' + this.parsedJSON[i].role + '</td>\n' +
                '                        <td class="tableStatus">' + this.parsedJSON[i].isAlife + '</td>\n' +
                '                        <td class="tableVotes" >' + this.parsedJSON[i].voteCount + '</td>\n' +
                '                        <td class="tableCheck" >' + this.parsedJSON[i].sheriffChecked + '</td>\n' +
                '                    </tr>\n'
            html += line
        }

        html += '                </tbody>\n' +
            '            </table>\n';

        this.table = html;
    },

    parseTable(data) {
        parsedArray = JSON.parse(data).rows;
        for (let i in parsedArray) {
            if (parsedArray[i].isItMe == 'true') { // эта строчка относится к данному игроку
                if (parsedArray[i].isAlife == "true")
                    GameState.meAlive = true;
                else {
                    GameState.meAlive = false;
                    GameState.myRole = "DEAD";
                    GameState.update("role");
                }
                //Здесь можно проверить что-то еще о себе
                // break;
            }
        }
        this.parsedJSON = this.formatTable(parsedArray);
    },

    formatTable(parsedArray) {
        for (let i in parsedArray) {
            // меняем содержимое массива на удобочитаемые
            if (parsedArray[i].isAlife == 'true')
                parsedArray[i].isAlife = "Живой";
            else
                parsedArray[i].isAlife = "Мертв";
            // меняем форматирование строки, относящейся к данному игроку
            if (parsedArray[i].isItMe == 'true') { // эта строчка относится к данному игроку
                for (let key in parsedArray[i]) {
                    parsedArray[i][key] = `<b><i>${parsedArray[i][key]}</b></i>`;
                }
            }
        }
        return parsedArray;
    },

    show() {
        table_mafiaUserVoteTable.innerHTML = this.table;
    }
}