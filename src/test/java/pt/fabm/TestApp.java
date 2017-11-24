package pt.fabm;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.Test;
import pt.fabm.closures.ResultSetClosure;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class TestApp {

    @Test
    public void testApp() throws ClassNotFoundException, SQLException {
        Class.forName("pt.fabm.MockDriver");
        Connection connection = DriverManager.getConnection("xpto");
        Map<String, SqlBehavior> map = AppModule.getInjector().getInstance(Key.get(new TypeLiteral<Map<String, SqlBehavior>>() {
        }, Names.named("sql-map")));
        final SqlBehaviorQuery sqlBehaviorQuery = new SqlBehaviorQuery();
    }

    @Test
    public void testGroovyScript() throws URISyntaxException, IOException {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("pt.fabm.Xpto");
        GroovyShell shell = new GroovyShell(config);
        Script script = shell.parse(TestApp.class.getResource("/Script4DB.groovy").toURI());
        script.run();
    }
}
