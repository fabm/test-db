package pt.fabm;

import com.google.common.io.Files;
import com.google.inject.*;
import com.google.inject.util.Modules;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.transport.vm.VMTransportFactory;

import javax.jms.JMSException;
import javax.jms.Session;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class TestModule {

    private BrokerService brokerService;
    private javax.jms.Connection connection;

    void setup() throws Exception {
        createBrokerService();
        createConnection();
        createSession();
    }

    public void tearDown() throws Exception {
        connection.close();
        brokerService.stop();
    }

    private void createConnection() throws JMSException, IOException {
        Properties properties = new Properties();
        properties.load(TestModule.class.getResourceAsStream("/application.properties"));
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(properties.getProperty("jms"));
        connection = factory.createConnection();
        connection.start();
    }


    private void createBrokerService() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("localhost");
        brokerService.setStartAsync(false);
        File tempDir = Files.createTempDir();
        brokerService.setDataDirectoryFile(tempDir);
        URI vmUri = new URI(String.format("vm://%s?marshal=%s", "mocks", true));
        TransportServer transportServer = new VMTransportFactory().doBind(vmUri);
        brokerService.addConnector(transportServer);
        this.brokerService = brokerService;
    }

    Session createSession() throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

}