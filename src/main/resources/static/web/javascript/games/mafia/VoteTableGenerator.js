class VoteTableGenerator {

    role = 'NC'



    generate(data) {
        var html =
            "<ul  >\n" +
            "            <table border=\"1\">\n" +
            "                <thead>\n" +
            "                <tr>\n" +
            "                    <th>Игрок</th>\n" +
            "                    <th>Роль</th>\n" +
            "                    <th>Статус (жив/мертв)</th>\n" +
            "                    <th>Количество голосов [шт.]</th>\n" +
            "                    <th>Шериф просмотрел этого пользователя</th>\n" +
            "                </tr>\n" +
            "                </thead>\n" +
            "                <tbody>"
        const rows = JSON.parse(data);
        for (var i = 0; i < rows.rows.length; i++) {
            var line =
                "                    <tr   class='table-data'>\n" +
                "                        <td>" + rows.rows[i].name + "</td>\n" +
                "                        <td>" + rows.rows[i].role + "</td>\n" +
                "                        <td>" + rows.rows[i].isAlife + "</td>\n" +
                "                        <td>" + rows.rows[i].voteCount + "</td>\n" +
                "                        <td>" + rows.rows[i].sheriffChecked + "</td>\n" +
                "                    </tr>\n"
            html = html + line
        }

        html = html + "                </tbody>\n" +
            "            </table>\n" +
            "        </ul>"
        return html
    }
/*
    generateNc(data) {
        var html =
            "<ul  >\n" +
            "            <table border=\"1\">\n" +
            "                <thead>\n" +
            "                <tr>\n" +
            "                    <th>Игрок</th>\n" +
            "                    <th>Роль</th>\n" +
            "                    <th>Статус (жив/мертв)</th>\n" +
            "                    <th>Количество голосов [шт.]</th>\n" +
            "                </tr>\n" +
            "                </thead>\n" +
            "                <tbody>"
        const rows = JSON.parse(data);
        for (var i = 0; i < rows.rows.length; i++) {
            var line =
                "                    <tr   class='table-data'>\n" +
                "                        <td>" + rows.rows[i].name + "</td>\n" +
                "                        <td>" + rows.rows[i].role + "</td>\n" +
                "                        <td>" + rows.rows[i].isAlife + "</td>\n" +
                "                        <td>" + rows.rows[i].voteCount + "</td>\n" +
                "                    </tr>\n"
            html = html + line
        }

        html = html + "                </tbody>\n" +
            "            </table>\n" +
            "        </ul>"
        return html
    }

 */
    /*
    generate(data)
    {

        if(this.role == 'LEADER')
        {
            this.generateLeader(data)
        }
        else
        {
            this.generateNc(data)
        }
    }
     */
}
