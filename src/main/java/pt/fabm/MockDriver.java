package pt.fabm;


import com.google.inject.Guice;
import com.google.inject.Injector;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class MockDriver implements Driver {

    static final Injector INJECTOR;
    static {
        try {
            MockDriver mockDriver = new MockDriver();
            DriverManager.registerDriver(mockDriver);
            INJECTOR = Guice.createInjector(new AppModule());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return AppModule.getInjector().getInstance(MockConnection.class);
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
