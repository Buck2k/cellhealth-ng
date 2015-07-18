package cellhealth.utils.properties.xml;

import java.util.List;

/**
 * Created by alberto on 1/07/15.
 */
public class MetricGroup {

    private String statsType;
    private String prefix;
    private boolean uniqueInstance;
    private List<String> InstanceFilter;
    private List<Metric> metrics;

    public MetricGroup(){}

    public String getStatsType() {
        return statsType;
    }

    public void setStatsType(String statsType) {
        this.statsType = statsType;
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

    public List<String> getInstanceFilter() {
        return InstanceFilter;
    }

    public void setInstanceFilter(List<String> instanceFilter) {
        InstanceFilter = instanceFilter;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

}
