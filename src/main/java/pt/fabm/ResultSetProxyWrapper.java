package pt.fabm;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

public interface ResultSetProxyWrapper extends ProxyWrapper<ResultSet>{
    void setIterator(Iterator<List<Object>> iterator);
}
