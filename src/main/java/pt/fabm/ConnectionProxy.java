package pt.fabm;

import groovy.lang.Closure;

import java.sql.CallableStatement;
import java.sql.Connection;

public interface ConnectionProxy extends ProxyWrapper<Connection>{
    ProxyWrapper<CallableStatement> getCallableStatement(String sql);
    void prepareCall(String sql, Closure closure);
}
