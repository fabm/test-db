package pt.fabm;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class AnotherTest {
    private List<Object> currentRow;

    @Test
    public void mockingResultSet() throws InvocationTargetException, IllegalAccessException, SQLException {

        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, 2, "ola", new Date(2017, 1, 1)),
                Arrays.asList(0, -1, "", Date.class)
        );

        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        Function<Class<?>, Class<?>> permut = aClass -> {
            if (aClass == Integer.class) {
                return int.class;
            }
            return aClass;
        };


        Function<Integer, Object> value = (column) -> {
            Object current = currentRow.get(column);
            if (Class.class.isInstance(current)) {
                return null;
            }
            return current;
        };

        Function<Integer, Class<?>> type = (column) -> {
            Object current = currentRow.get(column);
            if (Class.class.isInstance(current)) {
                return permut.apply(Class.class.cast(current));
            }
            return permut.apply(current.getClass());
        };


        Iterator<List<Object>> rowsIterator = rows.iterator();

        while (rowsIterator.hasNext()) {
            currentRow = rowsIterator.next();

            for (int col = 0; col < currentRow.size(); col++) {
                int finalCol = col;
                Method methodRef = Stream.of(ResultSet.class.getMethods())
                        .filter(method ->
                                method.getName().startsWith("get") &&
                                        type.apply(finalCol) == method.getReturnType() &&
                                        method.getParameters()[0].getType() == int.class
                        )
                        .findAny().get();

                Mockito.when(methodRef.invoke(resultSetMock, finalCol + 1)).then(invocation -> value.apply(finalCol));
            }
        }

        rowsIterator = rows.iterator();

        Iterator<List<Object>> finalRowsIterator = rowsIterator;
        Mockito.when(resultSetMock.next()).then(invocation -> {
            boolean result = finalRowsIterator.hasNext();
            if(result){
                currentRow = finalRowsIterator.next();
            }
            return result;
        });

        Assert.assertTrue(resultSetMock.next());
        Assert.assertEquals(1,resultSetMock.getInt(1));
        Assert.assertEquals(2,resultSetMock.getInt(2));
        Assert.assertEquals("ola",resultSetMock.getString(3));
        Assert.assertEquals(new Date(2017,1,1),resultSetMock.getDate(4));


        Assert.assertTrue(resultSetMock.next());
        Assert.assertEquals(0,resultSetMock.getInt(1));
        Assert.assertEquals(-1,resultSetMock.getInt(2));
        Assert.assertEquals("",resultSetMock.getString(3));
        Assert.assertEquals(null,resultSetMock.getDate(4));

        Assert.assertFalse(resultSetMock.next());
    }
}
