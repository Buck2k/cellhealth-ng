package cellhealth.core.threads.Metrics;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import cellhealth.core.threads.MetricsCollector;
import cellhealth.exception.CellhealthConnectionException;
import cellhealth.sender.Sender;
import cellhealth.sender.graphite.sender.GraphiteSender;
import cellhealth.utils.constants.Constants;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.MetricGroup;
import cellhealth.utils.properties.ReadMetricXml;
import javax.management.ObjectName;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alberto Pascual on 7/07/15.
 */
public class ThreadManager implements Runnable {

    private WASConnection wasConnection;
    private MBeansManager mbeansManager;
    private boolean throwThreads;
    private boolean isWaiting;

    public ThreadManager(WASConnection wasConnection) {
        this.wasConnection = wasConnection;
        this.startMBeansManager();
        this.throwThreads = true;
        this.isWaiting = !this.throwThreads;

    }

    private void startMBeansManager()  {
        this.mbeansManager = new MBeansManager(this.wasConnection);
    }

    public void run() {
        ReadMetricXml readMetricXml = new ReadMetricXml();
        List<MetricGroup> metricGroups = readMetricXml.getMetricGroup();
        Set<ObjectName> runtimes = null;
        try {
            runtimes = mbeansManager.getAllServerRuntimes();
        } catch (CellhealthConnectionException e) {
            L4j.getL4j().error(", e");
        }
        int instances = runtimes.size();
        GraphiteSender sender = new GraphiteSender();
        sender.init();
        while(throwThreads){
            try {
                throwThreads(instances, sender, runtimes, metricGroups);
                Thread.sleep(60000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
                L4j.getL4j().error("", e);
            }
        }
    }

    private void throwThreads(int instances, Sender sender, Set<ObjectName> runtimes, List<MetricGroup> metricGroups) {
        Date timeCountStart = new Date();
        ExecutorService executor = Executors.newFixedThreadPool(instances);
        while(!sender.isConnected()){
            try {
                L4j.getL4j().warning("The sender is not connected , waiting to connect");
                Thread.sleep(6000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(ObjectName serverRuntime: runtimes){
            final String serverName = serverRuntime.getKeyProperty(Constants.NAME);
            final String node = serverRuntime.getKeyProperty(Constants.NODE);
            String showServerHost = (( node==null ) || ( node.length() == 0 ))?"<NOT SET IN CONFIG>":node;
            L4j.getL4j().info("SERVER :" + serverName + " OVER MACHINE: " + showServerHost);
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
        boolean waitToThreads = true;
        while(waitToThreads){
            Long elapsed = timeCountStart.getTime() - new Date().getTime();
            if(executor.isTerminated()){
                System.out.println("Ha " + elapsed);
                waitToThreads = false;
            }
            if(elapsed == 60000l){
                L4j.getL4j().critical("The threads are taking too");
            }
        }

    }

    public void setThrowThreads(boolean throwThreads) {
        this.throwThreads = throwThreads;
        this.isWaiting = !throwThreads;
    }

    public boolean isWaiting() {
        return isWaiting;
    }
}
