package pt.fabm;

import java.sql.CallableStatement;
import java.sql.Connection;

public interface ConnectionProxy extends ProxyWrapper<Connection>{
    ProxyWrapper<CallableStatement> getCallableStatement(String sql);
}
