package pt.fabm.mockito;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDateTime;

public class TesteDbScript {

    private void withScript() throws URISyntaxException, IOException {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("pt.fabm.mockito.DbGroovyScript");
        GroovyShell shell = new GroovyShell(config);
        Script script = shell.parse(TesteDbScript.class.getResource("/mockito/Script4DBmockito.groovy").toURI());
        script.run();
    }
    private void withNoScript() throws URISyntaxException, IOException, SQLException {
        DbConnectionMock dbConnectionMock = new DbConnectionMock(){
            {
                Connection connection = connection();
                CallableStatement prepareCall = prepareCall();
                when(connection.getAutoCommit()).thenReturn(true);
                when(connection.prepareCall("call MY_TEST_SEARCH(?,?,?,?,?,?,?)")).thenReturn(prepareCall);

            }
        };
    }

    @Test
    public void test() throws SQLException, URISyntaxException, IOException {

        DataSource ds = new DataSource();
        ds.setDriverClassName(DbDriverTest.class.getCanonicalName());
        ds.setUrl("bla bla");
        ds.setUsername("user");
        ds.setPassword("pass");
        ds.setInitialSize(5);
        ds.setMaxActive(10);
        ds.setMaxIdle(5);
        ds.setMinIdle(2);

        withScript();
        Connection connection = ds.getConnection();
        CallableStatement cs = connection.prepareCall("call MY_TEST_SEARCH(?,?,?,?,?,?,?)");

        cs.setString(1, "a");
        cs.setString(2, "b");
        cs.setString(3, "c");
        cs.setInt(4, 1);
        cs.setInt(5, 2);
        cs.registerOutParameter(6, Types.VARCHAR);
        cs.registerOutParameter(7, Types.INTEGER);

        Assert.assertTrue(cs.execute());
        ResultSet rs = cs.getResultSet();

        Assert.assertTrue(rs.next());

        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertEquals("a", rs.getString(2));
        Assert.assertEquals(3, rs.getInt(3));
        Assert.assertNull(rs.getDate(4));

        Assert.assertTrue(rs.next());

        LocalDateTime date = LocalDateTime.of(2017, 1, 1, 0, 0, 0, 0);

        Assert.assertEquals(2, rs.getInt(1));
        Assert.assertEquals("b", rs.getString(2));
        Assert.assertEquals(5, rs.getInt(3));
        Assert.assertEquals(java.sql.Date.valueOf(date.toLocalDate()), rs.getDate(4));

        Assert.assertFalse(rs.next());
        cs.close();
        connection.close();


    }
}
