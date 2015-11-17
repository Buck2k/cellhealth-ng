package cellhealth.utils.properties.xml;

import java.util.List;

/**
 * Created by alberto on 1/07/15.
 */
public class MetricGroup {

    private String statsType;
    private String prefix;
    private boolean uniqueInstance;
    private boolean allowGlobal;
    private List<String> instanceInclude;
    private List<Metric> metrics;
    private List<String> instanceExclude;


    public void setAllowGlobal(boolean allowGlobal) {
        this.allowGlobal = allowGlobal;
    }

    public boolean getAllowGlobal(){
        return this.allowGlobal;
    }

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

    public List<String> getInstanceInclude() {
        return instanceInclude;
    }

    public void setInstanceInclude(List<String> instanceInclude) {
        this.instanceInclude = instanceInclude;
    }


    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public List<String> getInstanceExclude() {
        return instanceExclude;
    }

    public void setInstanceExclude(List<String> instanceExclude) {
        this.instanceExclude = instanceExclude;
    }
}
