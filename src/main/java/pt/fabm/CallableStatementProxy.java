package pt.fabm;

import pt.fabm.ProxyWrapper;

import java.sql.CallableStatement;
import java.util.List;
import java.util.Map;

public interface CallableStatementProxy extends ProxyWrapper<CallableStatement>{
    void setRows(List<List<Object>> rows);
    void setVars(List<Object> vars);
    void setResultSetMap(Map<String,Integer> outMap);
    void setInMap(Map<String,Integer> inMap);
}
