package pt.fabm.mockito;

import org.mockito.Mockito;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DbDriverTest implements Driver{
    private static Connection connection = Mockito.mock(Connection.class);

    public static Connection getConnection() {
        return connection;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return connection;
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
