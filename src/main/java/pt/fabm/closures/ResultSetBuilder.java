package pt.fabm.closures;

import com.google.common.reflect.Reflection;
import groovy.lang.Closure;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ResultSetBuilder implements InvocationHandler {
    private Iterator<List<Object>> rowsIterator;
    private List<Object> currentRow;
    private Closure callInterceptor;

    public ResultSetBuilder(List<List<Object>> rows) {
        this.rowsIterator = rows.iterator();

    }

    public ResultSet build() {
        return Reflection.newProxy(ResultSet.class, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "next":
                if (!rowsIterator.hasNext()) {
                    return false;
                } else {
                    currentRow = rowsIterator.next();
                    return true;
                }
            case "getInt":
                return currentRow.get(((Integer) args[0] - 1));
            case "getString":
                return currentRow.get(((Integer) args[0] - 1));
            case "getDate":
                LocalDateTime date = (LocalDateTime) currentRow.get(((Integer) args[0] - 1));
                final LocalDate localDate = Optional.ofNullable(date).map(LocalDateTime::toLocalDate).orElse(null);
                return Optional.ofNullable(localDate).map(java.sql.Date::valueOf).orElse(null);
        }

        return null;
    }
}
