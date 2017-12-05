package pt.fabm.mockito;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.mock;

class DbConnectionMock {
    Date date(int year, int month, int day) {
        long miliseconds = LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Date(miliseconds);
    }

    Date date(int year, int month, int day, int hour, int minute) {
        long miliseconds = LocalDateTime.of(year, month, day, hour, minute)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return new Date(miliseconds);
    }

    Date date(int year, int month, int day, int hour, int minute, int second, int nanoSeconds) {
        long miliseconds = LocalDateTime.of(year, month, day, hour, minute, second, nanoSeconds)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return new Date(miliseconds);
    }

    Connection connection() {
        return MockitoDbDriver.getConnection();
    }

    CallableStatement prepareCall() {
        return mock(CallableStatement.class);
    }

    ResultSetAdapter resultSet(List<List<Object>> rows) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        return new ResultSetAdapter(mock,rows);
    }

    <T> OngoingStubbing<T> when(T methodCall){
        return Mockito.when(methodCall);
    }
}
