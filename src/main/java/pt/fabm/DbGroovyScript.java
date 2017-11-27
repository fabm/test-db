package pt.fabm;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import groovy.lang.Closure;
import groovy.lang.Script;
import pt.fabm.closures.ResultSetBuilder;
import pt.fabm.closures.ResultSetPSClosure;
import pt.fabm.closures.TypeResultSetClosure;

import java.time.LocalDateTime;
import java.util.Map;

public abstract class DbGroovyScript extends Script {

    void resultSet(String type, String sql, Closure closure) {
        final ResultSetPSClosure delegate = new ResultSetPSClosure();
        closure.setDelegate(delegate);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
        if (TypeResultSetClosure.PREPARED_STATEMENT.toString().equals(type)) {
            Map<String, SqlBehavior> sqlmap = AppModule
                    .getInjector()
                    .getInstance(Key.get(new TypeLiteral<Map<String, SqlBehavior>>() {
                    }, Names.named("sql-map")));
            final SqlBehaviorQuery sqlBehaviorQuery = new SqlBehaviorQuery();

            ResultSetBuilder builder = new ResultSetBuilder(delegate.getRows());


            sqlBehaviorQuery.setResultSet(builder.build());
            sqlmap.put(sql, sqlBehaviorQuery);

        }
    }

    void resultSet(String sql, Closure closure) {
        resultSet(TypeResultSetClosure.PREPARED_STATEMENT.toString(), sql, closure);
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
