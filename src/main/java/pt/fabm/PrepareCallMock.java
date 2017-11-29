package pt.fabm;

import pt.fabm.script.CallableStatementScript;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Predicate;

public class PrepareCallMock implements CallableStatementWrapper, CallableStatementScript {

    private List<Object> vars = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();
    private Map<String, Integer> outMap;
    private Map<String, Integer> inMap;
    private CallableStatement proxy;

    @Override
    public int hashCode() {
        return Arrays.hashCode(vars.toArray());
    }

    @Override
    public boolean equals(Object obj) {
        return Optional.ofNullable(obj)
                .filter(PrepareCallMock.class::isInstance)
                .map(PrepareCallMock.class::cast)
                .filter(vars::equals)
                .isPresent();
    }

    @Override
    public List<List<Object>> getRows() {
        return rows;
    }

    @Override
    public void setRows(List<List<Object>> rows) {
        this.rows = rows;
    }

    @Override
    public void setVars(List<Object> vars) {
        this.vars = vars;
    }

    @Override
    public void setResultSetMap(Map<String, Integer> outMap) {
        this.outMap = outMap;
    }

    @Override
    public void setInMap(Map<String, Integer> inMap) {
        this.inMap = inMap;
    }

    @Override
    public List<Object> getVars() {
        return vars;
    }

    @Override
    public Map<String, Integer> getInMap() {
        return inMap;
    }

    @Override
    public Map<String, Integer> getOutMap() {
        return outMap;
    }

    @Override
    public void setProxy(CallableStatement proxy) {
        this.proxy = proxy;
    }

    @Override
    public CallableStatement getProxy() {
        return proxy;
    }

    private ResultSet createResultSet() {
        if (rows == null) {
            return null;
        }
        ResultSetProxyWrapper resultSetProxyWrapper = AppModule.getInjector().getInstance(ResultSetProxyWrapper.class);
        resultSetProxyWrapper.setIterator(rows.iterator());
        return resultSetProxyWrapper.getProxy();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();

        List<String> types = Arrays.asList(
                "Int",
                "String",
                "Date"
        );

        Predicate<String> isMethodType = str -> types.stream().anyMatch(str::startsWith);

        if (name.startsWith("set") && args.length == 2 && isMethodType.test(name.substring(3))) {
            vars.add((Integer) args[0] - 1, args[1]);
            return null;
        }

        if (name.equals("execute")) {
            return true;
        }

        if (name.equals("getResultSet")) {
            return createResultSet();
        }

        return null;
    }

    @Override
    public void init(CallableStatementScript callableStatementScript) {
        rows = callableStatementScript.getRows();
        vars = callableStatementScript.getVars();
        inMap = callableStatementScript.getInMap();
        outMap = callableStatementScript.getOutMap();
    }
}
