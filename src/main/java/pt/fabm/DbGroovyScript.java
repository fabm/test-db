package pt.fabm;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import groovy.lang.Script;
import pt.fabm.script.ConnectionScript;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public abstract class DbGroovyScript extends Script {

    ConnectionScript connection(String url) {
        ConnectionScript connectionScript = AppModule
                .getInjector()
                .getInstance(ConnectionScript.class);

        Map<String, ProxyWrapper<Connection>> connectionProxyMap = AppModule.getInjector()
                .getInstance(Key.get(new TypeLiteral<Map<String, ProxyWrapper<Connection>>>() {
                }, Names.named("sql-connections")));


        if(!connectionProxyMap.containsKey(url)){
            ProxyWrapper<Connection> connectionProxy = AppModule
                    .getInjector()
                    .getInstance(Key.get(new TypeLiteral<ProxyWrapper<Connection>>() {
                    }));

            connectionProxyMap.put(url,connectionProxy);
        }

        return connectionScript;
    }

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
}
