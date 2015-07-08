package cellhealth.core.connection;

import cellhealth.exception.CellhealthConnectionException;
import cellhealth.utils.constants.MBeansManagerConfig;

import cellhealth.utils.logs.L4j;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

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
        } catch (ConnectorException e) {
            L4j.getL4j().error("MBeansManager, Error in the connector", e);
        } catch (MalformedObjectNameException e) {
            L4j.getL4j().error("MBeansManager, object malformet", e);
        }
        return beans;
    }

    public ObjectName getMBean(String query) {
        Set mbeans = this.getMBeans(query);
        return (ObjectName)mbeans.iterator().next();
    }

    public AdminClient getClient(){
        return this.client;
    }

    public Set getAllServerRuntimes() throws CellhealthConnectionException {
        try {
            return this.getMBeans(MBeansManagerConfig.QUERY_SERVER_RUNTIME);
        }
        catch (Exception e) {
            throw new CellhealthConnectionException(e.toString(), e);
        }
    }
}