package com.microsoft.sqlserver.jdbc;

import pt.fabm.mockito.MockitoDbDriver;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class SQLServerDriver implements Driver {
    private Driver mock = new MockitoDbDriver();

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return mock.connect(url, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return mock.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return mock.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return mock.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return mock.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return mock.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return mock.getParentLogger();
    }
}
