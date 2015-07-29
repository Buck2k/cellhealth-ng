package cellhealth.core.threads.Metrics;

import cellhealth.core.statistics.Capturer;
import cellhealth.sender.Sender;
import cellhealth.utils.properties.Settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Alberto Pascual on 19/06/15.
 */
public class MetricsCollector implements Runnable {

    private final Capturer capturer;
    private final Sender sender;
    private final CHStats chStats;


    public MetricsCollector(Capturer capturer, Sender sender, CHStats chStats) {
        this.capturer = capturer;
        this.sender = sender;
        this.chStats = chStats;
    }

    public void run() {
        long start_time=System.currentTimeMillis();
        List<String> metrics = this.capturer.getMetrics();
        long serverIn = (System.currentTimeMillis() - start_time);
        this.sendAllMetricRange(metrics);
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(Settings.propertie().getPathSenderConf()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(this.capturer != null && this.capturer.getPrefix() != null) {
            List<String> csStats = new LinkedList<String>();
            String[] aux = this.capturer.getPrefix().split("\\.");
            String pathChStats = aux[0] + ".ch_stats";
            String retrieveTime = pathChStats + ".servers." + aux[1] + ".retrieve_time " + serverIn + " " + System.currentTimeMillis() / 1000L + "\n";
            String numberMetrics = pathChStats + ".servers." + aux[1] + ".number_metrics " + metrics.size() + " " + System.currentTimeMillis() / 1000L + "\n";
            if(this.chStats.getPathChStats() == null) {
                this.chStats.setPathChStats(pathChStats);
            }
            if(this.chStats.getHost() == null) {
                this.chStats.setHost(capturer.getHost());
            }
            this.chStats.add(retrieveTime);
            this.chStats.add(numberMetrics);
            this.chStats.count(metrics.size());
        }
    }

    private void sendAllMetricRange(List<String> metrics) {
        if(metrics != null) {
            for (String metric : metrics) {
                sender.send(capturer.getHost(), metric);
            }
        }
    }

    private String orderTreeByProperties(String metric){
        //pro.bbdd.wastest.wls.wastestCell.server2.conpool.WaitingThreadCount.integral 0.0 1437248069
        String[] splitOne = metric.split(" ");
        List<String> splitNodes = Arrays.asList(splitOne[0].split("."));
        return null;
    }
}
