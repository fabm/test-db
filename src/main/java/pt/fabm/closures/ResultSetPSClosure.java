package pt.fabm.closures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ResultSetPSClosure {
    List<List<Object>> rows = new ArrayList<>();
    Map<String,Object> vars;

    public List<List<Object>> getRows() {
        return rows;
    }

    public Map<String, Object> getVars() {
        return vars;
    }
}
