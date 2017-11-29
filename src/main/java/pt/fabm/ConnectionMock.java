package pt.fabm;

import com.google.common.reflect.Reflection;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import groovy.lang.Closure;
import pt.fabm.script.CallableStatementScript;
import pt.fabm.script.ConnectionScript;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ConnectionMock implements ProxyWrapper<Connection>,ConnectionScript {

    private Map<String, CallableStatementWrapper> callableStatements = new HashMap<>();

    private Connection proxy;

    public void setProxy(Connection proxy) {
        this.proxy = proxy;
    }

    public Connection getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();

        if (name.equals("prepareCall")) {
            return Optional
                    .ofNullable(callableStatements.get(args[0]))
                    .orElseThrow(() -> new IllegalStateException("no callableStatement with sql:" + args[0]))
                    .getProxy();
        }
        return null;
    }


    @Override
    public void prepareCall(String sql, Closure closure) {

        Supplier<CallableStatementWrapper> supplier = () -> {
            CallableStatementWrapper newCallableStatement = AppModule
                    .getInjector()
                    .getInstance(CallableStatementWrapper.class);

            callableStatements.put(sql, newCallableStatement);
            return newCallableStatement;
        };

        CallableStatementScript callableStatementScript = AppModule
                .getInjector()
                .getInstance(CallableStatementScript.class);


        final CallableStatementWrapper callableStatementWrapper = Optional.ofNullable(callableStatements.get(sql))
                .orElseGet(supplier);

        callableStatementScript.setRows(new ArrayList<>());

        closure.setDelegate(callableStatementScript);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();

        callableStatementWrapper.init(callableStatementScript);
    }
}
