package pt.fabm;


import com.google.common.reflect.Reflection;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Named;

import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class MockDriver implements Driver {

    @Inject
    @Named("sql-connections")
    private Map<String, ProxyWrapper<Connection>> connections;

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        ProxyWrapper<Connection> connectionMock = connections.get(url);
        if (Objects.isNull(connectionMock)) {
            connectionMock = AppModule.getInjector().getInstance(Key.get(Type.get()));
            connectionMock.setProxy(Reflection.newProxy(Connection.class, connectionMock));
            connections.put(url, connectionMock);
        }
        return connectionMock.getProxy();
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
