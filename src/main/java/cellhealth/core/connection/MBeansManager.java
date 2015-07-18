package cellhealth.core.connection;

import cellhealth.utils.constants.MBeansManagerConfig;
import cellhealth.utils.logs.L4j;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.exception.ConnectorNotAvailableException;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import java.util.Set;

public class MBeansManager {

    private AdminClient client;

    public MBeansManager(WASConnection connection) {
        this.client = connection.getClient();
    }

    public Set<ObjectName> getMBeans(String query) {
        if(this.client == null) {
            throw new NullPointerException();
        }
        Set<ObjectName> beans = null;
        try {
             beans = this.client.queryNames(new ObjectName(query), null);
        } catch (ConnectorNotAvailableException e) {
        } catch (ConnectorException e) {
            L4j.getL4j().error("MBeansManager, Error in the connector", e);
        } catch (MalformedObjectNameException e) {
            L4j.getL4j().error("MBeansManager, object malformet", e);
        }
        return beans;
    }

    public ObjectName getMBean(String query) {
        Set mbeans = this.getMBeans(query);
        ObjectName objectName = null;
        if(mbeans != null && mbeans.size() > 0) {
            objectName = (ObjectName) mbeans.iterator().next();
        }
        return objectName;
    }

    public AdminClient getClient(){
        return this.client;
    }

    public Set getAllServerRuntimes()  {
            return this.getMBeans(MBeansManagerConfig.QUERY_SERVER_RUNTIME);
    }

    public String getNodeServerMBean(){
        String node = "";
        try {
            node = this.client.getServerMBean().getKeyProperty("node");
        } catch (ConnectorException e) {}
        return node;
    }
}