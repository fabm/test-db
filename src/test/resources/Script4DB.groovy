prepareCall {
    sql 'call MY_TEST_SEARCH(?,?,?,?,?,?,?)'
    rows(
            [1, 'a', 3, null],
            [2, 'b', 5, date(2017, 1, 1)]
    )
    types 'int', 'string', 'int', 'date'
}