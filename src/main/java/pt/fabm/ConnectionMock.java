package pt.fabm;

import com.google.common.reflect.Reflection;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import groovy.lang.Closure;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ConnectionMock implements ConnectionProxy {

    private Map<String, ProxyWrapper<CallableStatement>> callableStatements = new HashMap<>();

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
    public ProxyWrapper<CallableStatement> getCallableStatement(String sql) {

        Supplier<ProxyWrapper<CallableStatement>> supplier = () -> {
            ProxyWrapper<CallableStatement> newCallableStatement = AppModule
                    .getInjector()
                    .getInstance(Key.get(Type.get()));

            callableStatements.put(sql, newCallableStatement);
            return newCallableStatement;
        };

        return Optional.ofNullable(callableStatements.get(sql)).orElseGet(supplier);
    }

    @Override
    public void prepareCall(String sql, Closure closure) {

        Supplier<CallableStatementProxy> supplier = () -> {
            CallableStatementProxy newCallableStatement = AppModule
                    .getInjector()
                    .getInstance(Key.get(new TypeLiteral<CallableStatementProxy>(){}));

            newCallableStatement.setProxy(Reflection.newProxy(CallableStatement.class,newCallableStatement));
            callableStatements.put(sql, newCallableStatement);
            return newCallableStatement;
        };


        closure.setDelegate(Optional.ofNullable(callableStatements.get(sql)).orElseGet(supplier));
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();

    }
}
