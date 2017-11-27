package pt.fabm;

import com.google.common.io.Files;
import com.google.inject.*;
import com.google.inject.util.Modules;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.transport.vm.VMTransportFactory;

import javax.jms.JMSException;
import javax.jms.Session;
import java.io.File;
import java.net.URI;

public class TestModule extends AbstractModule {

    private static Injector INJECTOR;

    public static Injector getInjector() {
        if (INJECTOR == null) {
            INJECTOR = Guice.createInjector(Modules.override(new AppModule()).with(new TestModule()));
        }
        return INJECTOR;
    }

    private BrokerService brokerService;

    @Provides
    @Singleton
    private BrokerService getBrokerService() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("localhost");
        brokerService.setStartAsync(false);
        File tempDir = Files.createTempDir();
        brokerService.setDataDirectoryFile(tempDir);
        URI vmUri = new URI(String.format("vm://%s?marshal=%s", "mocks", true));
        TransportServer transportServer = new VMTransportFactory().doBind(vmUri);
        brokerService.addConnector(transportServer);
        return brokerService;
    }

    @Provides
    private Session getSession(javax.jms.Connection connection) throws JMSException {
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    ;


    @Override
    protected void configure() {
    }


}