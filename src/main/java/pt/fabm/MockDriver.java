package pt.fabm;


import com.google.common.reflect.Reflection;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class MockDriver implements Driver {

    @Inject
    @Named("sql-connections")
    private Map<String,Connection> connections;

    @Inject
    @Named("sql-callable-statements")
    private Map<String,Connection> callableStatements;


    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Connection proxy = connections.get(url);
        if(proxy == null){
            connections.put(url, Reflection.newProxy(Connection.class, (proxy1, method, args) -> {
                switch (method.getName()){

                }
                return null;
            }));
        }
        return connections.get(url);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
