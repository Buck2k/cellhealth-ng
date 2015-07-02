package cellhealth.core;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import cellhealth.core.threads.Metrics.Capturer;
import cellhealth.core.threads.MetricsCollector;
import cellhealth.exception.CellhealthConnectionException;
import cellhealth.sender.graphite.sender.GraphiteSender;
import cellhealth.utils.constants.Constants;
import cellhealth.utils.constants.MBeansManagerConfig;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.MetricGroup;
import cellhealth.utils.properties.ReadMetricXml;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.ws.scripting.AdminConfigClient;
import commonj.work.WorkItem;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alberto Pascual on 19/06/15.
 */
public class StartCellhealth {

    private WASConnection wasConnection;
    private MBeansManager mbeansManager;

    public StartCellhealth(WASConnection wasConnection) throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, CellhealthConnectionException {
        this.wasConnection = wasConnection;
        this.startMBeansManager();
        this.initMetricsCollector();
    }

    private void startMBeansManager() throws ConnectorException, MalformedObjectNameException {
        this.mbeansManager = new MBeansManager(this.wasConnection);
    }

    private void initMetricsCollector() throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, CellhealthConnectionException {
        ReadMetricXml readMetricXml = new ReadMetricXml();
        List<MetricGroup> metricGroups = readMetricXml.getMetricGroup();
        Set<ObjectName> runtimes = mbeansManager.getAllServerRuntimes();
        int instances = runtimes.size();
        GraphiteSender sender = new GraphiteSender();
        sender.init();
        ExecutorService executor = Executors.newFixedThreadPool(instances);
        while(!sender.isConnected()){
            try {
                L4j.getL4j().warning("Wait to connect sender");
                Thread.sleep(6000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(ObjectName serverRuntime: runtimes){
            final String serverName = serverRuntime.getKeyProperty(Constants.NAME);
            final String node = serverRuntime.getKeyProperty(Constants.NODE);
            String showServerHost = (( node==null ) || ( node.length() == 0 ))?"<NOT SET IN CONFIG>":node;
            L4j.getL4j().info(new StringBuilder("SERVER :")
                    .append(serverName)
                    .append(" OVER MACHINE: ")
                    .append(showServerHost).toString());
            final Capturer capturer = new Capturer(mbeansManager, node, serverName, metricGroups);

            Runnable worker = new MetricsCollector(capturer, sender);
            executor.execute(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished all threads");
    }


}
