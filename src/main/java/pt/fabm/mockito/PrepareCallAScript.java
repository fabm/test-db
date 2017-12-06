package pt.fabm.mockito;

import groovy.lang.Closure;
import groovy.lang.Script;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public abstract class PrepareCallAScript extends Script {
    private DbConnectionMock dbConnectionMock = new DbConnectionMock();

    protected Date date(int year, int month, int day) {
        return dbConnectionMock.date(year, month, day);
    }

    protected Date date(int year, int month, int day, int hour, int minute) {
        return dbConnectionMock.date(year, month, day, hour, minute);
    }

    protected Date date(int year, int month, int day, int hour, int minute, int second, int nanoSeconds) {
        return dbConnectionMock.date(year, month, day, hour, minute, second, nanoSeconds);
    }

    protected void prepareCall(Closure closure) {
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.setDelegate(new PCClosureDel());
        closure.call();
    }

    protected  <T> OngoingStubbing<T> when(T methodCall) {
        return dbConnectionMock.when(methodCall);
    }


}
