package cellhealth.core.threads.Metrics;

import cellhealth.core.statistics.Capturer;
import cellhealth.sender.Sender;
import cellhealth.utils.properties.Settings;

import java.util.List;


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
        if(this.capturer.getPrefix() != null) {
            String[] aux = this.capturer.getPrefix().split("\\.");
            String pathChStats = aux[0] + ".ch_stats";
            String retrieveTime = pathChStats + ".metrics." + aux[1] + ".retrieve_time " + serverIn + " " + System.currentTimeMillis() / 1000L + "\n";
            String numberMetrics = pathChStats + ".metrics." + aux[1] + ".number_metrics " + metrics.size() + " " + System.currentTimeMillis() / 1000L + "\n";
            if(Settings.propertie().isSelfStats()) {
                if (this.chStats.getPathChStats() == null) {
                    this.chStats.setPathChStats(pathChStats);
                }
                if (this.chStats.getHost() == null) {
                    this.chStats.setHost(capturer.getHost());
                }
                this.chStats.add(retrieveTime);
                this.chStats.add(numberMetrics);
                this.chStats.count(metrics.size());
            }
        }
    }

    private void sendAllMetricRange(List<String> metrics) {
        if(metrics != null) {
            for (String metric : metrics) {
                sender.send(capturer.getHost(), metric);
            }
        }
    }

}
