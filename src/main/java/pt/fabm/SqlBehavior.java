package pt.fabm;

import java.sql.ResultSet;
import java.sql.SQLClientInfoException;

public class SqlBehavior {
    private int repetitions = 0;
    private String response;
    private ResultSet resultSet;

    public int getRepetitions() {
        return repetitions;
    }
    public void decrementRepetitions(){
        repetitions--;
    }
    public void setResultSet(ResultSet resultSet){
        this.resultSet = resultSet;
    }

    public SqlBehaviorQuery asQuery(){
        if(this instanceof SqlBehaviorQuery){
            return (SqlBehaviorQuery) this;
        }
        throw new MockSqlException("The sqlBehavior it's not query typed");
    }

}
