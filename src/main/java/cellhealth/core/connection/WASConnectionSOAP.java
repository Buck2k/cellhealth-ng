package cellhealth.core.connection;

import cellhealth.utils.constants.Constants;
import cellhealth.utils.constants.message.Info;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.Conf;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;

import java.security.Security;
import java.util.Properties;

public class WASConnectionSOAP implements WASConnection {

    private Properties props;          // Connection properties
    private AdminClient client;        // WebSphere JMX client

    public WASConnectionSOAP() {
        this.connect();
        StringBuilder conectando = new StringBuilder();
        conectando.append(Info.CONECTANDO_DMG)
                .append(" ").append(Info.CONECTANDO_MEDIANTE_CONECTOR)
                .append(" ").append(this.props.getProperty(AdminClient.CONNECTOR_TYPE))
                .append(" ").append(Info.CONECTANDO_HOST)
                .append(" ").append(this.props.getProperty(AdminClient.CONNECTOR_HOST));
        L4j.getL4j().info(conectando.toString());
    }

    public void connect() {
        Security.setProperty(Constants.SSL_SOCKETFACTORY_PROVIDER, Constants.JSSE2_SSLSOCKETFACTORYIMPL);
        this.props = new Properties();
        this.props.setProperty(AdminClient.CONNECTOR_HOST, Conf.propertie().HOST_WEBSPHERE);
        this.props.setProperty(AdminClient.CONNECTOR_PORT, Conf.propertie().PORT_WEBSPHERE);
        this.props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
        try {
            this.client = AdminClientFactory.createAdminClient(props);
        } catch (ConnectorException e) {
            L4j.getL4j().error(e.toString(), e);
        }
    }

    public AdminClient getClient() {
        return this.client;
    }
}

