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
import cellhealth.utils.properties.xml.CellHealthMetrics;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.exception.ConnectorNotAvailableException;

import javax.management.ObjectName;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadManager implements Runnable {

    private WASConnection wasConnection;
    private Sender sender;
    private MBeansManager mbeansManager;
    private CellHealthMetrics cellHealthMetrics;

    public ThreadManager(CellHealthMetrics cellHealthMetrics) {
        this.cellHealthMetrics = cellHealthMetrics;
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
            Capturer capturer = new Capturer(this.mbeansManager, node, serverName, cellHealthMetrics);
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
                if(Settings.propertie().isSelfStats()) {
                    Runtime runtime = Runtime.getRuntime();
                    String retrieveTime = chStats.getPathChStats() + ".metrics.global.retrieve_time " + elapsed + " " + System.currentTimeMillis() / 1000L + "\n";
                    String numberMetrics = chStats.getPathChStats() + ".metrics.global.number_metrics " + chStats.getMetrics() + " " + System.currentTimeMillis() / 1000L + "\n";
                    String jvmFreeMemory = chStats.getPathChStats() + ".os.freememory " + runtime.freeMemory() + " " + System.currentTimeMillis() / 1000L + "\n";
                    String jvmMaxMemory = chStats.getPathChStats() + ".os.maxmemory " + runtime.maxMemory() + " " + System.currentTimeMillis() / 1000L + "\n";
                    String jvmTotalMemory = chStats.getPathChStats() + ".os.totalmemory " + runtime.totalMemory() + " " + System.currentTimeMillis() / 1000L + "\n";
                    String availableProcessors = chStats.getPathChStats() + ".os.availableprocessors " + runtime.availableProcessors() + " " + System.currentTimeMillis() / 1000L + "\n";
                    for(GarbageCollectorMXBean garbageCollectorMXBean:ManagementFactory.getGarbageCollectorMXBeans()){
                        String name = garbageCollectorMXBean.getName().replace(" ", "_");
                        chStats.add(chStats.getPathChStats() + ".jvm.gc." + name + ".gc_collection_count " + garbageCollectorMXBean.getCollectionCount() + " " + System.currentTimeMillis() / 1000L + "\n");
                        chStats.add(chStats.getPathChStats() + ".jvm.gc." + name + ".gc_collection_time " + garbageCollectorMXBean.getCollectionTime() + " " + System.currentTimeMillis() / 1000L + "\n");
                    }
                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    chStats.add(chStats.getPathChStats() + ".jvm.memory.heapUsage_init " + memoryMXBean.getHeapMemoryUsage().getInit() + " " + System.currentTimeMillis() / 1000L + "\n");
                    chStats.add(chStats.getPathChStats() + ".jvm.memory.heapUsage_used " + memoryMXBean.getHeapMemoryUsage().getUsed() + " " + System.currentTimeMillis() / 1000L + "\n");
                    chStats.add(chStats.getPathChStats() + ".jvm.memory.heapUsage_committed " + memoryMXBean.getHeapMemoryUsage().getCommitted() + " " + System.currentTimeMillis() / 1000L + "\n");
                    chStats.add(chStats.getPathChStats() + ".jvm.memory.heapUsage_max " + memoryMXBean.getHeapMemoryUsage().getMax() + " " + System.currentTimeMillis() / 1000L + "\n");
                    CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
                    chStats.add(chStats.getPathChStats() + ".jvm.compiler.total_compilation_time " + compilationMXBean.getTotalCompilationTime() + " " + System.currentTimeMillis() / 1000L + "\n");
                    ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
                    chStats.add(chStats.getPathChStats() + ".jvm.classloading.class_count " + classLoadingMXBean.getLoadedClassCount() + " " + System.currentTimeMillis() / 1000L + "\n");
                    for(MemoryPoolMXBean memoryPoolMXBean:ManagementFactory.getMemoryPoolMXBeans()){
                        String name = memoryPoolMXBean.getName().replace(" ", "_");
                        chStats.add(chStats.getPathChStats() + ".jvm.memoryPool." + name + ".init " + memoryPoolMXBean.getUsage().getInit() + " " + System.currentTimeMillis() / 1000L + "\n");
                        chStats.add(chStats.getPathChStats() + ".jvm.memoryPool." + name + ".used " + memoryPoolMXBean.getUsage().getUsed() + " " + System.currentTimeMillis() / 1000L + "\n");
                        chStats.add(chStats.getPathChStats() + ".jvm.memoryPool." + name + ".committed " + memoryPoolMXBean.getUsage().getCommitted() + " " + System.currentTimeMillis() / 1000L + "\n");
                        chStats.add(chStats.getPathChStats() + ".jvm.memoryPool." + name + ".max " + memoryPoolMXBean.getUsage().getMax() + " " + System.currentTimeMillis() / 1000L + "\n");
                    }
                    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                    chStats.add(chStats.getPathChStats() + ".jvm.threads.daemon " + threadMXBean.getDaemonThreadCount() + " " + System.currentTimeMillis() / 1000L + "\n");
                    chStats.add(chStats.getPathChStats() + ".jvm.threads.thread_count " + threadMXBean.getThreadCount() + " " + System.currentTimeMillis() / 1000L + "\n");
                    chStats.add(retrieveTime);
                    chStats.add(numberMetrics);
                    chStats.add(jvmFreeMemory);
                    chStats.add(jvmMaxMemory);
                    chStats.add(jvmTotalMemory);
                    chStats.add(availableProcessors);
                    if (chStats.getStats() != null) {
                        for (String metric : chStats.getStats()) {
                            sender.send(chStats.getHost(), metric);
                        }
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