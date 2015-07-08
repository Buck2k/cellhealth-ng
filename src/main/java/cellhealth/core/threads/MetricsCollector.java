package cellhealth.core.threads;

import cellhealth.core.threads.Metrics.Capturer;
import cellhealth.sender.Sender;
import cellhealth.sender.graphite.sender.GraphiteSender;
import com.ibm.websphere.management.exception.ConnectorException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import java.util.List;

/**
 * Created by Alberto Pascual on 19/06/15.
 */
public class MetricsCollector implements Runnable {

    final Capturer capturer;
    final Sender sender;
    private int test = 0;
    public MetricsCollector(Capturer capturer, Sender sender) {
        this.capturer = capturer;
        this.sender = sender;
    }

    public void run() {
        try {
            long start_time=System.currentTimeMillis();
            List<String> metrics = capturer.getStats();
            long end_time=System.currentTimeMillis();
            String elapsed=Long.toString(end_time-start_time);
            System.out.println("Test whit system milis " + elapsed);
            for(String metric: metrics){
                sender.send(metric);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
