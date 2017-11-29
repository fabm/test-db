package pt.fabm;

import com.google.common.reflect.Reflection;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import groovy.lang.Closure;
import groovy.lang.Script;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

public abstract class DbGroovyScript extends Script {

    ConnectionProxy connection(String url) {
        Map<String, ConnectionProxy> connectionProxyMap = AppModule.getInjector()
                .getInstance(Key.get(new TypeLiteral<Map<String, ConnectionProxy>>() {
                }, Names.named("sql-connections")));
        return Optional.ofNullable(connectionProxyMap.get(url)).orElseGet(() -> {

            //TODO construir no AppModule
            ConnectionProxy connectionProxy = AppModule
                    .getInjector()
                    .getInstance(ConnectionProxy.class);

            connectionProxy.setProxy(Reflection.newProxy(Connection.class,connectionProxy));
            connectionProxyMap.put(url, connectionProxy);
            return connectionProxy;
        });
    }

    void prepareCall(String sql, Closure closure) {

        CallableStatementProxy callableStatementProxy = AppModule
                .getInjector()
                .getInstance(CallableStatementProxy.class);

        closure.setDelegate(callableStatementProxy);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();

    }

    Date date(int year, int month, int day) {
        long miliseconds = LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Date(miliseconds);
    }

    Date date(int year, int month, int day, int hour, int minute) {
        long miliseconds = LocalDateTime.of(year, month, day,hour,minute)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return new Date(miliseconds);
    }

    Date date(int year, int month, int day, int hour, int minute, int second, int nanoSeconds) {
        long miliseconds = LocalDateTime.of(year, month, day,hour,minute,second,nanoSeconds)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return new Date(miliseconds);
    }
}
