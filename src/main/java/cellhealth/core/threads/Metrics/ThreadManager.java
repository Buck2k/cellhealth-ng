package cellhealth.core.threads.Metrics;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import cellhealth.core.connection.WASConnectionSOAP;
import cellhealth.core.statistics.Capturer;
import cellhealth.sender.Sender;
import cellhealth.sender.graphite.sender.GraphiteSender;
import cellhealth.utils.constants.Constants;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.xml.MetricGroup;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.exception.ConnectorNotAvailableException;

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
    private Sender sender;
    private MBeansManager mbeansManager;
    private List<MetricGroup> metricGroups;
    private boolean throwThreads;
    private boolean isWaiting;

    public ThreadManager(List<MetricGroup> metricGroups) {
        this.metricGroups = metricGroups;
        this.connectToWebSphere();
        this.sender = this.getSender();
        this.throwThreads = true;
        this.isWaiting = !this.throwThreads;
    }

    private void startMBeansManager()  {
        this.mbeansManager = new MBeansManager(this.wasConnection);
    }

    public void run() {
        this.showInstances();
        while(throwThreads){
            try {
                this.launchThreads();
                Thread.sleep(60000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
                L4j.getL4j().error("", e);
            }
        }
    }

    private void launchThreads() {
        Set<ObjectName> runtimes = this.mbeansManager.getAllServerRuntimes();
        Date timeCountStart = new Date();
        ExecutorService executor = Executors.newFixedThreadPool(runtimes.size());
        checkConnections();
        for(ObjectName serverRuntime: runtimes){
            String serverName = serverRuntime.getKeyProperty(Constants.NAME);
            String node = serverRuntime.getKeyProperty(Constants.NODE);
            Capturer capturer = new Capturer(this.mbeansManager, node, serverName, this.metricGroups);
            Runnable worker = new MetricsCollector(capturer, this.sender);
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
                L4j.getL4j().info("It has been slow to catch all the metrics " + elapsed);
                waitToThreads = false;
            }
            if(elapsed == 60000l){
                L4j.getL4j().critical("The threads are taking too");
            }
        }

    }
    public void connectToWebSphere(){
        this.wasConnection = new WASConnectionSOAP();
        this.startMBeansManager();
    }
    public Sender getSender(){
        GraphiteSender sender = new GraphiteSender();
        sender.init();
        return sender;
    }

    public void setThrowThreads(boolean throwThreads) {
        this.throwThreads = throwThreads;
        this.isWaiting = !throwThreads;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void checkConnections(){
        try {
            mbeansManager.getClient().isAlive();
        } catch (ConnectorException e) {
            if(e instanceof ConnectorNotAvailableException){
                connectToWebSphere();
            }
        }
        while(!sender.isConnected()){
            try {
                L4j.getL4j().warning("The sender is not connected , waiting to connect");
                Thread.sleep(6000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void showInstances(){
        Set<ObjectName> runtimes = this.mbeansManager.getAllServerRuntimes();
        for(ObjectName serverRuntime: runtimes){
            String serverName = serverRuntime.getKeyProperty(Constants.NAME);
            String node = serverRuntime.getKeyProperty(Constants.NODE);
            String showServerHost = (( node==null ) || ( node.length() == 0 ))?"<NOT SET IN CONFIG>":node;
            L4j.getL4j().info("SERVER :" + serverName + " OVER MACHINE: " + showServerHost);
        }
    }

}