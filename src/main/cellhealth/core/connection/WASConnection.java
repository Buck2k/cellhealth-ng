package cellhealth.core.connection;

import cellhealth.exception.CellhealthConnectionException;
import cellhealth.utils.constants.message.Info;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.Base;
import lombok.Getter;
import lombok.Setter;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by alberto on 10/06/15.
 */
public class WASConnection {

    @Getter
    @Setter
    private MBeanServerConnection  mbeanServerConnection;
    private ObjectName serverMBean;
    private ObjectName perfMBean;
    private ObjectName jvmMBean;
    private ObjectName node;

    public WASConnection() throws CellhealthConnectionException {
        try {
            System.setProperty(Base.propertie().TRUSTSTORE, Base.propertie().TRUSTSTORE_VALUE);
            System.setProperty(Base.propertie().TRUSTSTOREPASSWORD, Base.propertie().TRUSTSTOREPASSWORD_VALUE);

            Map<String, Object> h = new HashMap<String, Object>();
            h.put(Base.propertie().MONITORROLE, new String[]{Base.propertie().MONITORROLE, Base.propertie().MONITORROLE_PASSWORD});
            h.put("jmx.remote.protocol.provider.pkgs", "com.ibm.websphere.management.remote");

            //JMXServiceURL It is the JMX service URL
            JMXConnector jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(Base.propertie().JMXSERVICEURL), h);

            this.mbeanServerConnection = jmxConnector.getMBeanServerConnection();
            this.serverMBean = this.getMBean(Base.propertie().SERVERMBEAN);
            this.perfMBean = this.getMBean(Base.propertie().PERFMBEAN);
            this.jvmMBean = this.getMBean(Base.propertie().JVMMBEAN);
            L4j.getL4j().info(Info.JMX_CONNECT);
        }
        catch (Exception e) {
            throw new CellhealthConnectionException(e.toString(), e);
        }
    }
    public ObjectName getMBean(String query) throws Exception {
        Set<ObjectName> mbeans = this.getMBeans(query);
        return mbeans.iterator().next();
    }

    public Set<ObjectName> getMBeans(String query) throws Exception {
        return this.mbeanServerConnection.queryNames(new ObjectName(query), null);
    }


}
