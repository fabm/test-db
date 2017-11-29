package pt.fabm.script;

import java.util.List;
import java.util.Map;

public interface CallableStatementScript {
    List<List<Object>> getRows();
    void setRows(List<List<Object>> rows);
    void setVars(List<Object> vars);
    void setResultSetMap(Map<String,Integer> outMap);
    void setInMap(Map<String,Integer> inMap);
    List<Object> getVars();
    Map<String,Integer> getInMap();
    Map<String,Integer> getOutMap();
}
