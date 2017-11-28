package pt.fabm;

import groovy.lang.Closure;
import groovy.lang.Script;

import java.sql.CallableStatement;
import java.time.LocalDateTime;

public abstract class DbGroovyScript extends Script {

    void prepareCall(String sql, Closure closure) {
        PrepareCallMock prepareCallMock = new PrepareCallMock();
        closure.setDelegate(prepareCallMock);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
        ProxyWrapper<CallableStatement> x = AppModule
                .getInjector()
                .getInstance(ConnectionProxy.class)
                .getCallableStatement(sql);

    }

    LocalDateTime date(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0, 0);
    }

    LocalDateTime date(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute, 0, 0);
    }

    LocalDateTime date(int year, int month, int day, int hour, int minute, int second, int nanoSeconds) {
        return LocalDateTime.of(year, month, day, hour, minute, second, nanoSeconds);
    }
}
