package cellhealth.core.connection;

import cellhealth.exception.CellhealthConnectionException;
import cellhealth.utils.constants.MBeansManagerConfig;
import cellhealth.utils.constants.message.Info;
import cellhealth.utils.logs.L4j;
import com.ibm.websphere.pmi.stat.MBeanStatDescriptor;
import com.ibm.websphere.pmi.stat.StatDescriptor;
import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.util.Set;

/**
 * Created by Alberto Pascual on 18/06/15.
 */

public class MBeansManager {

    private AdminClient client;
    private ObjectName serverMBean;
    private ObjectName perfMBean;
    private ObjectName nodeAgent;

    public MBeansManager(WASConnection connection) throws ConnectorException, MalformedObjectNameException {
        this.client = connection.getClient();
        this.serverMBean = this.getMBean(MBeansManagerConfig.QUERY_SERVER);
        this.perfMBean = this.getMBean(MBeansManagerConfig.QUERY_PERF);
    }

    public Set<ObjectName> getMBeans(String query) throws MalformedObjectNameException, ConnectorException {
        if(this.client == null) {
            throw new NullPointerException();
        }
        return this.client.queryNames(new ObjectName(query), null);
    }

    public ObjectName getMBean(String query) throws ConnectorException, MalformedObjectNameException {
        Set<ObjectName> mbeans = this.getMBeans(query);
        return mbeans.iterator().next();
    }

    public ObjectName getMBean(String query, String server) throws ConnectorException, MalformedObjectNameException {
        StringBuilder queryServer = new StringBuilder();
        queryServer.append(query)
                .append(MBeansManagerConfig.QUERY_ADD_PROCCES)
                .append(server);
        return getMBean(queryServer.toString());
    }

    public Object getAttribute(ObjectName mbean, String attribute) throws ConnectorException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        return this.client.getAttribute(mbean, attribute);
    }

    public double getNumberAttr(ObjectName mBean, String attr) throws CellhealthConnectionException {
        try {
            Object number = this.client.getAttribute(mBean, attr);
            if (number instanceof Double) {
                return (Double)number;
            }
            if (number instanceof Long) {
                return ((Long)number).longValue();
            }
            return ((Integer)number).intValue();
        }
        catch (Exception e) {
            throw new CellhealthConnectionException(e.toString(), e);
        }
    }

    public String getTextAttr(ObjectName mBean, String attr) throws CellhealthConnectionException {
        try {
            return this.client.getAttribute(mBean, attr).toString();
        }
        catch (Exception e) {
            throw new CellhealthConnectionException(e.toString(), e);
        }
    }

    public WSStats getStats(String name) throws Exception {
        return getStats(name, null);
    }

    public WSStats getStats(String name, String server) throws Exception {
        ObjectName auxPerfMBean;
        ObjectName auxServerMBean;
        if(server == null) {
            auxPerfMBean = this.perfMBean;
            auxServerMBean = this.serverMBean;
        } else {
            this.nodeAgent = this.getNodeAgentMBean(MBeansManagerConfig.NODEAGENT);
            auxPerfMBean = this.getMBean(MBeansManagerConfig.QUERY_PERF, server);
            auxServerMBean = this.getMBean(MBeansManagerConfig.QUERY_SERVER, server);
        }
        MBeanStatDescriptor mbeanStatDescriptor = new MBeanStatDescriptor(auxServerMBean, new StatDescriptor(new String[]{name}));
        Object[] parameters = new Object[]{mbeanStatDescriptor, new Boolean(true)};
        String[] signature = new String[]{MBeansManagerConfig.MBEANSTATDESCRIPTOR, MBeansManagerConfig.JAVA_BOOLEAN};
        return (WSStats)this.client.invoke(auxPerfMBean, MBeansManagerConfig.STATS_OBJECT, parameters, signature);
    }

    public ObjectName getNodeAgentMBean(String nodeName) throws MalformedObjectNameException, ConnectorException {
        StringBuilder query = new StringBuilder();
        query.append(MBeansManagerConfig.QUERY_NODEAGENT)
                .append(nodeName)
                .append(",*");
        Set<ObjectName> mbeans = this.client.queryNames(new ObjectName(query.toString()), null);
        if (mbeans != null && !mbeans.isEmpty()) {
            L4j.getL4j().info(new StringBuilder(Info.MBEAN_FOUND_NODO).append(nodeName).toString());
            return this.nodeAgent = mbeans.iterator().next();
        } else {
            L4j.getL4j().info(Info.MBEAN_NOTFOUND_NODO);
            return this.nodeAgent;
        }
    }
}
