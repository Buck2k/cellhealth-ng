package cellhealth.core.connection;

import cellhealth.utils.constants.Constants;
import cellhealth.utils.constants.message.Info;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.Settings;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;

import java.security.Security;
import java.util.Properties;

public class WASConnectionSOAP implements WASConnection {

    private Properties props;
    private AdminClient client;

    public WASConnectionSOAP() {
        do {
            this.connect();
            if(this.client == null) {
                try {
                    Thread.sleep(60000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while(this.client == null);
    }

    public void connect() {
        Security.setProperty(Constants.SSL_SOCKETFACTORY_PROVIDER, Constants.JSSE2_SSLSOCKETFACTORYIMPL);
        this.props = new Properties();
        this.props.setProperty(AdminClient.CONNECTOR_HOST, Settings.propertie().HOST_WEBSPHERE);
        this.props.setProperty(AdminClient.CONNECTOR_PORT, Settings.propertie().PORT_WEBSPHERE);
        this.props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
        try {
            this.client = AdminClientFactory.createAdminClient(props);
        } catch (ConnectorException e) {
            L4j.getL4j().error("The system can not create a SOAP connector to connect a host " +  Settings.propertie().HOST_WEBSPHERE + " on port " + Settings.propertie().PORT_WEBSPHERE);
        }
        if(this.client != null) {
            StringBuilder conectando = new StringBuilder();
            conectando.append(Info.CONECTANDO_DMG)
                    .append(" ").append(Info.CONECTANDO_MEDIANTE_CONECTOR)
                    .append(" ").append(this.props.getProperty(AdminClient.CONNECTOR_TYPE))
                    .append(" ").append(Info.CONECTANDO_HOST)
                    .append(" ").append(this.props.getProperty(AdminClient.CONNECTOR_HOST));
            L4j.getL4j().info(conectando.toString());
        }
    }

    public AdminClient getClient() {
        return this.client;
    }
}

