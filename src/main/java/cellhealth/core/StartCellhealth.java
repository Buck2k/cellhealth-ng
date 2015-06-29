package cellhealth.core;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import cellhealth.sender.graphite.sender.GraphiteSender;
import cellhealth.utils.constants.MBeansManagerConfig;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.ws.scripting.AdminConfigClient;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * Created by Alberto Pascual on 19/06/15.
 */
public class StartCellhealth {

    private WASConnection wasConnection;
    private MBeansManager mbeansManager;

    public StartCellhealth(WASConnection wasConnection) throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        this.wasConnection = wasConnection;
        this.startMBeansManager();
        this.initMetricsCollector();
    }

    private void startMBeansManager() throws ConnectorException, MalformedObjectNameException {
        this.mbeansManager = new MBeansManager(this.wasConnection);
    }

    private void initMetricsCollector() throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
//        for(ObjectName objectName: mbeansManager.getMBeans("WebSphere:*")){
//                System.out.println(objectName + "\n");
//                String serverName = (String)mbeansManager.getAttribute(objectName, "name");
//                String serverHost = (String)mbeansManager.getAttribute(objectName, "nodeName");
//                if (serverHost == null || serverHost.length() == 0) {
//                    L4j.getL4j().info("SERVER: " + serverName + " OVER MACHINE: <NOT SET IN CONFIG>");
//                } else {
//                    L4j.getL4j().info("SERVER: " + serverName + " OVER MACHINE: " + serverHost);
//                }
//        }
//        System.out.println("\n\n\n\n");
//        for(ObjectName objectName: mbeansManager.getMBeans(MBeansManagerConfig.QUERY_SERVER)){
//            System.out.println(objectName + "\n");
//        }
//        System.out.println("\n\n\n\n");
//        for(ObjectName objectName: mbeansManager.getMBeans(MBeansManagerConfig.QUERY_PERF)){
//            System.out.println(objectName + "\n");
//        }

    }


}
