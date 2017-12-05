package pt.fabm.mockito;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.List;

import static org.mockito.Mockito.mock;

public class DbConnectionMock {
    protected Date date(int year, int month, int day) {
        long miliseconds = LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Date(miliseconds);
    }

    protected Date date(int year, int month, int day, int hour, int minute) {
        long miliseconds = LocalDateTime.of(year, month, day, hour, minute)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return new Date(miliseconds);
    }

    protected Date date(int year, int month, int day, int hour, int minute, int second, int nanoSeconds) {
        long miliseconds = LocalDateTime.of(year, month, day, hour, minute, second, nanoSeconds)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return new Date(miliseconds);
    }

    protected Connection connection() {
        return DbDriverTest.getConnection();
    }

    protected CallableStatement prepareCall() {
        return mock(CallableStatement.class);
    }

    protected ResultSetAdapter resultSet(List<List<Object>> rows) throws SQLException {
        ResultSet mock = mock(ResultSet.class);
        return new ResultSetAdapter(mock,rows);
    }

    protected <T> OngoingStubbing<T> when(T methodCall){
        return Mockito.when(methodCall);
    }
}
