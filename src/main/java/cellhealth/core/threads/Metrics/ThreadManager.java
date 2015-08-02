package cellhealth.core.threads.Metrics;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import cellhealth.core.connection.WASConnectionSOAP;
import cellhealth.core.statistics.Capturer;
import cellhealth.sender.Sender;
import cellhealth.sender.graphite.sender.GraphiteSender;
import cellhealth.utils.Utils;
import cellhealth.utils.constants.Constants;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.Settings;
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

public class ThreadManager implements Runnable {

    private WASConnection wasConnection;
    private Sender sender;
    private MBeansManager mbeansManager;
    private List<MetricGroup> metricGroups;

    public ThreadManager(List<MetricGroup> metricGroups) {
        this.metricGroups = metricGroups;
        this.connectToWebSphere();
        this.sender = this.getSender();
    }

    private void startMBeansManager()  {
        this.mbeansManager = new MBeansManager(this.wasConnection);
    }

    public void run() {
        Utils.showInstances(this.mbeansManager);
        boolean start = true;
        while(start){
            try {
                this.launchThreads();
                Thread.sleep(Settings.propertie().getThreadInterval());
            } catch (InterruptedException e) {
                start = false;
                L4j.getL4j().error("TreadManager sleep error: ", e);
            }
        }
    }

    private void launchThreads() {
        Set<ObjectName> runtimes = this.mbeansManager.getAllServerRuntimes();
        Date timeCountStart = new Date();
        ExecutorService executor = Executors.newFixedThreadPool(runtimes.size());
        checkConnections();
        CHStats chStats = new CHStats();
        for(ObjectName serverRuntime: runtimes){
            String serverName = serverRuntime.getKeyProperty(Constants.NAME);
            String node = serverRuntime.getKeyProperty(Constants.NODE);
            Capturer capturer = new Capturer(this.mbeansManager, node, serverName, this.metricGroups);
            Runnable worker = new MetricsCollector(capturer, this.sender, chStats);
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
            Long elapsed =  new Date().getTime() - timeCountStart.getTime();
            if(executor.isTerminated()){
                Runtime runtime = Runtime.getRuntime();
                String retrieveTime = chStats.getPathChStats() + ".global.retrieve_time " + elapsed + " " + System.currentTimeMillis() / 1000L + "\n";
                String numberMetrics = chStats.getPathChStats() + ".global.number_metrics " + chStats.getMetrics() + " " + System.currentTimeMillis() / 1000L + "\n";
                String jvmFreeMemory = chStats.getPathChStats() + ".global.jvm.freememory " + runtime.freeMemory() + " " + System.currentTimeMillis() / 1000L + "\n";
                String jvmMaxMemory = chStats.getPathChStats() + ".global.jvm.maxmemory " + runtime.maxMemory() + " " + System.currentTimeMillis() / 1000L + "\n";
                String jvmTotalMemory = chStats.getPathChStats() + ".global.jvm.totalmemory " + runtime.totalMemory() + " " + System.currentTimeMillis() / 1000L + "\n";
                String availableProcessors = chStats.getPathChStats() + ".global.jvm.availableprocessors " + runtime.availableProcessors() + " " + System.currentTimeMillis() / 1000L + "\n";
                chStats.add(retrieveTime);
                chStats.add(numberMetrics);
                chStats.add(jvmFreeMemory);
                chStats.add(jvmMaxMemory);
                chStats.add(jvmTotalMemory);
                chStats.add(availableProcessors);
                if(chStats.getStats() != null) {
                    for (String metric : chStats.getStats()) {
                        sender.send(chStats.getHost(), metric);
                    }
                }
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
                Thread.sleep(Settings.propertie().getSenderInterval() / 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}