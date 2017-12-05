package pt.fabm.mockito;

import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

public class ResultSetAdapter {

    private ResultSet mock;
    private List<List<Object>> rows;
    private Iterator<List<Object>> iterator;
    private List<Object> currentRow;
    private String[] types;

    private static Class<?> permut(Class<?> sourceClass) {
        if (sourceClass == Integer.class) {
            return int.class;
        }
        return sourceClass;
    }

    private Object getValue(int column) {
        Object current = currentRow.get(column);
        if (Class.class.isInstance(current)) {
            return null;
        }
        return current;
    }

    public ResultSetAdapter(ResultSet mock, List<List<Object>> rows) throws SQLException {
        this.mock = mock;
        this.rows = rows;
        this.iterator = rows.iterator();
        when(mock.next()).then(invocation -> next());
    }

    public void withTypes(String... types) {
        this.types = types;
        addGetters();
    }

    private Class<?> getType(int column) {
        Object current = currentRow.get(column);
        if (Class.class.isInstance(current)) {
            return permut(Class.class.cast(current));
        }
        return permut(current.getClass());
    }

    private void addGetters() {

        iterator = rows.iterator();

        while (iterator.hasNext()) {
            currentRow = iterator.next();

            for (int col = 0; col < currentRow.size(); col++) {
                int finalCol = col;
                Method methodRef = Stream.of(ResultSet.class.getMethods())
                        .filter(method ->
                                method.getName().equalsIgnoreCase("get" + types[finalCol]) &&
                                        method.getParameterCount() == 1 &&
                                        getType(finalCol) == method.getReturnType() &&
                                        method.getParameters()[0].getType() == int.class
                        )
                        .findAny().get();

                try {
                    Mockito.when(methodRef.invoke(mock, finalCol + 1)).then(invocation -> getValue(finalCol));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        iterator = rows.iterator();

    }

    private boolean next() {
        boolean result = iterator.hasNext();
        if (result) {
            this.currentRow = iterator.next();
        }
        return result;
    }

    public ResultSet getMock() {
        return mock;
    }
}
