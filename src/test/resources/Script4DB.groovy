import java.time.LocalDateTime

resultSet('call MY_TEST_SEARCH(?,?,?,?,?,?,?)') {
    rows << [1,'a',3,null]
    rows << [2, 'b', 5, date(2017,1,1)]

    vars = [
            RETURN: 'OK',
            LABEL:'COOL'
    ]
}