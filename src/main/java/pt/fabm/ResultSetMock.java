package pt.fabm;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ResultSetMock implements ResultSetProxyWrapper{
    private ResultSet proxy;
    private Iterator<List<Object>> iterator;
    private List<Object> row;

    @Override
    public void setProxy(ResultSet proxy) {
        this.proxy = proxy;
    }

    @Override
    public ResultSet getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (name.equals("next")){
            Boolean result = Optional.ofNullable(iterator).map(Iterator::hasNext).orElse(false);
            if (result){
                row = iterator.next();
            }
            return result;
        }

        List<String> types = Arrays.asList(
                "Int",
                "String",
                "Date"
        );

        Predicate<String> isMethodType = (str) -> types.stream().anyMatch(str::startsWith);
        if (name.startsWith("get") && args.length == 1 && isMethodType.test(name.substring(3))) {
            return row.get(((Integer) args[0])-1);
        }
        return null;
    }

    @Override
    public void setIterator(Iterator<List<Object>> iterator) {
        this.iterator = iterator;
    }
}
