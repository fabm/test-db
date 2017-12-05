package pt.fabm.mockito;

import groovy.lang.Script;
import org.mockito.stubbing.OngoingStubbing;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public abstract class DbGroovyScript extends Script {
    protected DbConnectionMock dbConnectionMock = new DbConnectionMock();

    protected Date date(int year, int month, int day) {
        return dbConnectionMock.date(year, month, day);
    }

    protected Date date(int year, int month, int day, int hour, int minute) {
        return dbConnectionMock.date(year, month, day, hour, minute);
    }

    protected Date date(int year, int month, int day, int hour, int minute, int second, int nanoSeconds) {
        return dbConnectionMock.date(year, month, day, hour, minute, second, nanoSeconds);
    }

    protected Connection connection() {
        return dbConnectionMock.connection();
    }

    protected CallableStatement prepareCall() {
        return dbConnectionMock.prepareCall();
    }

    protected ResultSetAdapter resultSet(List<List<Object>> rows) throws SQLException {
        return dbConnectionMock.resultSet(rows);
    }

    protected  <T> OngoingStubbing<T> when(T methodCall) {
        return dbConnectionMock.when(methodCall);
    }


}
