package pt.fabm;

import java.sql.ResultSet;

public class SqlBehaviorQuery extends SqlBehavior{
    private int repetitions = 0;
    private String response;
    private ResultSet resultSet;

    @Override
    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }
}
