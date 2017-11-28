package pt.fabm;

import com.google.inject.Key;

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
}
