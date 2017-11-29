package pt.fabm;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.activemq.broker.BrokerService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestApp {

    private static Injector INJECTOR;

    @BeforeClass
    public static void setUp() throws Exception {
        INJECTOR = TestModule.getInjector();
        AppModule.setInjector(INJECTOR);
    }

    @Test
    public void testDataSource() throws SQLException, ClassNotFoundException, URISyntaxException, IOException {

        DataSource ds = new DataSource();
        ds.setDriverClassName(SQLServerDriver.class.getCanonicalName());
        ds.setUrl("bla bla");
        ds.setUsername("user");
        ds.setPassword("pass");
        ds.setInitialSize(5);
        ds.setMaxActive(10);
        ds.setMaxIdle(5);
        ds.setMinIdle(2);

        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("pt.fabm.DbGroovyScript");
        GroovyShell shell = new GroovyShell(config);
        Script script = shell.parse(TestApp.class.getResource("/Script4DB.groovy").toURI());
        script.run();

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


    @Test
    public void testApp() throws ClassNotFoundException, SQLException {
        Connection connection = DriverManager.getConnection("xpto");
        Map<String, Connection> map = INJECTOR.getInstance(Key.get(new TypeLiteral<Map<String, Connection>>() {
        }, Names.named("sql-map")));
    }


    @Test
    public void testGroovyScript() throws URISyntaxException, IOException, ClassNotFoundException, SQLException {

        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("pt.fabm.DbGroovyScript");
        GroovyShell shell = new GroovyShell(config);
        Script script = shell.parse(TestApp.class.getResource("/Script4DB.groovy").toURI());
        script.run();

        Class.forName("pt.fabm.MockDriver");
        Connection connection = DriverManager.getConnection("xpto");
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

    @Test
    public void testQueue() throws Exception {


        BrokerService brokerService = INJECTOR.getInstance(BrokerService.class);

        Session session = INJECTOR.getInstance(Session.class);
        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a messages
        final InputStream stream = (InputStream) TestApp.class.getResource("/Script4DB.groovy").getContent();

        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return stream;
            }
        };


        String text = byteSource.asCharSource(Charsets.UTF_8).read();
        TextMessage message = session.createTextMessage(text);

        // Tell the producer to send the message
        System.out.println("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());


        producer.send(message);

        final javax.jms.Connection connection = INJECTOR.getInstance(javax.jms.Connection.class);

        Assert.assertNotNull(receiveMessage());

        session.close();
        connection.close();
        brokerService.stop();
    }

    private String receiveMessage() throws JMSException, ExecutionException, InterruptedException {

        AsyncResponse<String> asyncResponse = new AsyncResponse<>();
        final javax.jms.Connection connection = INJECTOR.getInstance(javax.jms.Connection.class);
        // Create a Session
        Session session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination2 = session2.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session2.createConsumer(destination2);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<String> future = executorService.submit(asyncResponse);

        consumer.setMessageListener(m -> {
            try {
                System.out.println("trying:" + System.nanoTime());
                Thread.sleep(5000);
                System.out.println("done:" + System.nanoTime());
                String t = Optional.ofNullable(m).map(e -> (TextMessage) e).orElse(null).getText();
                asyncResponse.setResponse(t);
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Clean up
        String response = future.get();
        session2.close();
        return response;
    }
}
