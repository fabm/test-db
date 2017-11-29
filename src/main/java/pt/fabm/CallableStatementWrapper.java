package pt.fabm;

import pt.fabm.script.CallableStatementScript;

import java.sql.CallableStatement;

public interface CallableStatementWrapper extends ProxyWrapper<CallableStatement>{
    void init(CallableStatementScript callableStatementScript);
}
