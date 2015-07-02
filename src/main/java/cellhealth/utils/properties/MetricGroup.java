package cellhealth.utils.properties;

import java.util.List;

/**
 * Created by alberto on 1/07/15.
 */
public class MetricGroup {

    private String type;
    private String prefix;
    private boolean uniqueInstance;
    private List<Metric> metrics;

    public MetricGroup(){}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isUniqueInstance() {
        return uniqueInstance;
    }

    public void setUniqueInstance(boolean uniqueInstance) {
        this.uniqueInstance = uniqueInstance;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }
}
