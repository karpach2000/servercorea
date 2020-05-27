class VoteTableGenerator {

    role = 'NC'



    generate(data) {
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

            const rows = JSON.parse(data);
            for (let i = 0; i < rows.rows.length; i++) {
                let status = "Мертв";
                let strongify = '';
                if (rows.rows[i].isAlife == 'true') status = "Живой";
                if (rows.rows[i].isItMe == 'true') strongify = '<b><i>';
                var line =
                    '                    <tr   class="table-data">\n' +
                    '                        <td class="tableLogin" >' + strongify + rows.rows[i].name + '</td>\n' +
                    '                        <td class="tableRole"  >' + strongify + rows.rows[i].role + '</td>\n' +
                    '                        <td class="tableStatus">' + strongify + status + '</td>\n' +
                    '                        <td class="tableVotes" >' + strongify + rows.rows[i].voteCount + '</td>\n' +
                    '                        <td class="tableCheck" >' + strongify + rows.rows[i].sheriffChecked + '</td>\n' +
                    '                    </tr>\n'
                html = html + line
            }

            html = html + '                </tbody>\n' +
                '            </table>\n';
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