package pt.fabm;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import pt.fabm.mockito.DbDriverTest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class AnotherTest {
    private List<Object> currentRow;

    @Test
    public void mockingResultSet() throws InvocationTargetException, IllegalAccessException, SQLException, URISyntaxException, IOException {

        DriverManager.registerDriver(new DbDriverTest());

        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("pt.fabm.mockito.DbGroovyScript");
        GroovyShell shell = new GroovyShell(config);
        Script script = shell.parse(AnotherTest.class.getResource("/mockito/Script4DBmockito.groovy").toURI());
        script.run();


        Connection connection = DriverManager.getConnection("bla bla");
        CallableStatement cs = connection.prepareCall("call MY_TEST_SEARCH(?,?,?,?,?,?,?)");

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


    }
}
