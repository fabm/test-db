def connection = connection()
def prepareCall = prepareCall()
def resultSet = resultSet([
        [1, 'a', 3, null],
        [2, 'b', 5, date(2017,1,1)]
])
resultSet.withTypes('int','string','int','date')

when(connection.prepareCall('call MY_TEST_SEARCH(?,?,?,?,?,?,?)')).thenReturn(prepareCall)
when(prepareCall.execute()).thenReturn(true)
when(prepareCall.resultSet).thenReturn(resultSet.mock)
