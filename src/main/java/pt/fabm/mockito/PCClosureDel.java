package pt.fabm.mockito;

import org.mockito.Mockito;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PCClosureDel {
    private Connection connection = MockitoDbDriver.getConnection();
    private CallableStatement callableStatement = Mockito.mock(CallableStatement.class);
    private ResultSet resultSet = mock(ResultSet.class);
    private ResultSetAdapter resultSetAdapter;

    PCClosureDel() {
    }

    public void sql(String sql) throws SQLException {
        Mockito.when(connection.prepareCall(sql)).thenReturn(callableStatement);
    }

    public void rows(List<Object>... rows) throws SQLException {
        resultSetAdapter = new ResultSetAdapter(resultSet, Arrays.asList(rows));
        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.getResultSet()).thenReturn(resultSet);
    }

    public void types(String...types){
        resultSetAdapter.withTypes(types);
    }
}
