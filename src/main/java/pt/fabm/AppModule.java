package pt.fabm;

import com.google.common.reflect.Reflection;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppModule extends AbstractModule {

    private static Injector injector;
    private static boolean init = false;

    public static Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new AppModule());
        }
        if(init){
            init(injector);
            init = true;
        }
        return injector;
    }
    private static void init(Injector injector){
        try {
            DriverManager.registerDriver(injector.getInstance(SQLServerDriver.class));
            DriverManager.registerDriver(injector.getInstance(MockDriver.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void init(){
        init(getInjector());
    }

    public static void setInjector(Injector injector) {
        AppModule.injector = injector;
    }

    private Map<String, ConnectionProxy> sqlMap = new HashMap<>();


    @Provides
    private CallableStatementProxy getCallableStatementProxy(Injector injector){
        return injector.getInstance(PrepareCallMock.class);
    }

    @Provides
    private ResultSetProxyWrapper getResultSetProxyWrapper(Injector injector){
        final ResultSetMock instance = injector.getInstance(ResultSetMock.class);
        instance.setProxy(Reflection.newProxy(ResultSet.class,instance));
        return instance;
    }

    @Provides
    private ConnectionProxy getConnectionMock(Injector injector) {
        return injector.getInstance(ConnectionMock.class);
    }

    @Provides
    @Singleton
    private Connection getJMSConnection() throws JMSException, IOException {
        Properties properties = new Properties();
        properties.load(AppModule.class.getResourceAsStream("/application.properties"));
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(properties.getProperty("jms"));
        return factory.createConnection();
    }

    @Provides
    private ProxyWrapper<java.sql.Connection> getProxyWrapper(Injector injector){
        return injector.getInstance(ConnectionMock.class);
    }

    @Provides
    @Named("sql-connections")
    private Map<String, ConnectionProxy> getConnectionsMap() {
        return sqlMap;
    }

    @Provides
    @Singleton
    private Session getJMSSession(Connection connection) throws JMSException {
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }


    @Override
    protected void configure() {
    }
}
