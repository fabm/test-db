package pt.fabm;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class AppModule extends AbstractModule {

    private static Injector INJECTOR;

    public static Injector getInjector() {
        if (INJECTOR == null) {
            INJECTOR = Guice.createInjector(new AppModule());
        }
        return INJECTOR;
    }

    public static void setInjector(Injector injector) {
        AppModule.INJECTOR = injector;
    }

    private Map<String, java.sql.Connection> sqlMap = new HashMap<>();

    @Provides
    @Singleton
    private Connection getJMSConnection() throws JMSException, IOException {
        Properties properties = new Properties();
        properties.load(AppModule.class.getResourceAsStream("/application.properties"));
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(properties.getProperty("jms"));
        return factory.createConnection();
    }

    @Provides
    @Singleton
    private Session getJMSSession(Connection connection) throws JMSException {
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    protected void configure() {
        try {
            DriverManager.registerDriver(new SQLServerDriver());
            DriverManager.registerDriver(new MockDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bind(new TypeLiteral<Map<String, java.sql.Connection>>() {
        }).annotatedWith(Names.named("sql-map")).toInstance(sqlMap);
    }
}