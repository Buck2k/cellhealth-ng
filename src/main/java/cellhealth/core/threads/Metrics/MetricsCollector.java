package cellhealth.core.threads.Metrics;

import cellhealth.core.statistics.Capturer;
import cellhealth.sender.Sender;
import cellhealth.utils.logs.L4j;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Alberto Pascual on 19/06/15.
 */
public class MetricsCollector implements Runnable {

    final Capturer capturer;
    final Sender sender;

    public MetricsCollector(Capturer capturer, Sender sender) {
        this.capturer = capturer;
        this.sender = sender;
    }

    public void run() {
        long start_time=System.currentTimeMillis();
        this.sendAllMetricRange(this.capturer.getMetrics());
        L4j.getL4j().info("Server: " + capturer.getServerName() + ", Node: " + capturer.getNode() + ", to slow " + (System.currentTimeMillis() - start_time) + "ms");
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
