package cellhealth.core.threads;

import cellhealth.core.threads.Metrics.Capturer;
import cellhealth.sender.Sender;
import cellhealth.sender.graphite.sender.GraphiteSender;
import com.ibm.websphere.management.exception.ConnectorException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

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
            capturer.getStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
