let LocationsTable = {

    /**
     * Список основных локаций.
     */
    tableMain: "<table>",
    /**
     * Список пользовательских локаций
     */
    tableUser: "<table>",
    parsedJSON: {},


    /**
     * Команда на генерацию таблицы.
     * @param data
     */
    generateTables(data) {
        this.generatePublicTable(data);
        this.generateUserTable(data);
    },


    generatePublicTable(data)
    {
        this.parseTable(data);
        var html =
            'Общественные локации'+
            '            <table class="table table-bordered table-hover">\n' +
            '                <thead class="thead-light">\n' +
            '                <tr>\n' +
            '                    <th class="tableLogin" >Локация</th>\n' +
            '                </tr>\n' +
            '                </thead>\n' +
            '                <tbody class="table table-striped">';

        for (let i in Locations.publicLocations) {
            var line =
                '                    <tr   class="table-data">\n' +
                '                        <td class="tableLogin" >' + Locations.publicLocations[i] + '</td>\n' +
                '                    </tr>\n'
            html += line
        }

        html += '                </tbody>\n' +
            '            </table>\n';

        this.tableMain = html;
    },
    generateUserTable(data)
    {
        this.parseTable(data);
        var html =
            'Пользовательские локации'+
            '            <table class="table table-bordered table-hover">\n' +
            '                <thead class="thead-light">\n' +
            '                <tr>\n' +
            '                    <th class="tableLogin" >Локация</th>\n' +
            '                </tr>\n' +
            '                </thead>\n' +
            '                <tbody class="table table-striped">';

        for (let i in Locations.userLocations) {
            var line =
                '                    <tr   class="table-data">\n' +
                '                        <td class="tableLogin" >' + Locations.userLocations[i] + '</td>\n' +
                '                    </tr>\n'
            html += line
        }

        html += '                </tbody>\n' +
            '            </table>\n';

        this.tableUser = html;
    },

    /**
     * Обновляет список локаций внутри JS/
     * @param data
     */
    parseTable(data) {
        Locations = JSON.parse(data);
    },


    show() {
        document.getElementById("spy_mainLocations").innerHTML = this.tableMain;
        document.getElementById("spy_userLocations").innerHTML = this.tableUser;
    }



}