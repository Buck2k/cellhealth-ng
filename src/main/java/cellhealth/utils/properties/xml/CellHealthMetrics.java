package cellhealth.utils.properties.xml;

import java.util.List;

/**
 * Created by Alberto Pascual on 6/08/15.
 */
public class CellHealthMetrics {

    private PmiStatsType pmiStatsType;
    private List<MetricGroup> metricGroups;

    public List<MetricGroup> getMetricGroups() {
        return metricGroups;
    }

    public void setMetricGroups(List<MetricGroup> metricGroups) {
        this.metricGroups = metricGroups;
    }

    public PmiStatsType getPmiStatsType() {
        return pmiStatsType;
    }

    public void setPmiStatsType(PmiStatsType pmiStatsType) {
        this.pmiStatsType = pmiStatsType;
    }
}
