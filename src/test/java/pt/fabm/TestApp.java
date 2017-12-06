package pt.fabm;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.fabm.mockito.MockitoDbDriver;

import javax.jms.*;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestApp {

    private static TestModule testModule;

    @BeforeClass
    public static void setUp() throws Exception {
        testModule = new TestModule();
        testModule.setup();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        testModule.tearDown();
    }

    @Test
    public void testDataSource() throws SQLException, ClassNotFoundException, URISyntaxException, IOException {
        DataSource ds = new DataSource();
        ds.setDriverClassName(MockitoDbDriver.class.getCanonicalName());
        ds.setUrl("bla bla");
        ds.setUsername("user");
        ds.setPassword("pass");
        ds.setInitialSize(5);
        ds.setMaxActive(10);
        ds.setMaxIdle(5);
        ds.setMinIdle(2);

        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass("pt.fabm.mockito.PrepareCallAScript");
        GroovyShell shell = new GroovyShell(config);
        Script script = shell.parse(TestApp.class.getResource("/Script4DB.groovy").toURI());
        script.run();


        Connection connection = ds.getConnection();
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


        Session session = testModule.createSession();

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


        Assert.assertNotNull(receiveMessage());

        session.close();
    }


    @Test
    public void testApacheCxf() throws Exception {

        int port = 9090;

        Server httpServer = new Server(port);
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        httpServer.setHandler(contexts);
        ServletContextHandler root = new ServletContextHandler(contexts, "/",
                ServletContextHandler.SESSIONS);

        Bus bus = BusFactory.getDefaultBus(true);

        CXFNonSpringServlet cxf = new CXFNonSpringServlet();
        cxf.setBus(bus);
        ServletHolder servlet = new ServletHolder(cxf);
        servlet.setName("soap");
        servlet.setForcedPath("soap");
        root.addServlet(servlet, "/soap/*");

        httpServer.start();
        BusFactory.setDefaultBus(bus);

        Hello hello = name -> "hello, "+ name;

        Endpoint.publish("/Hello", hello);

        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient("http://localhost:9090/soap/Hello?wsdl");

        Object[] res = client.invoke("sayHi", "it's me");
        Assert.assertEquals("hello, it's me",res[0]);

    }

    private String receiveMessage() throws JMSException, ExecutionException, InterruptedException {

        AsyncResponse<String> asyncResponse = new AsyncResponse<>();
        // Create a Session
        Session session2 = testModule.createSession();

        // Create the destination (Topic or Queue)
        Destination destination2 = session2.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session2.createConsumer(destination2);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<String> future = executorService.submit(asyncResponse);

        consumer.setMessageListener(m -> {
            try {
                String t = Optional.ofNullable(m).map(TextMessage.class::cast).orElse(null).getText();
                asyncResponse.setResponse(t);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        // Clean up
        String response = future.get();
        session2.close();
        return response;
    }


}


