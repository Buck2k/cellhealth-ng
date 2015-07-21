package cellhealth.core.statistics;

import cellhealth.core.connection.MBeansManager;
import cellhealth.utils.Utils;
import cellhealth.utils.UtilsWSStatistic;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.xml.Metric;
import cellhealth.utils.properties.xml.MetricGroup;
import com.ibm.websphere.pmi.stat.WSStatistic;
import com.ibm.websphere.pmi.stat.WSStats;

import javax.management.ObjectName;
import java.util.LinkedList;
import java.util.List;

public class Capturer {

    private MBeansManager mbeansManager;
    private String serverName;
    private String node;
    private List<MetricGroup> metricGroups;
    private ObjectName serverPerfMBean;
    private String host;

    public Capturer(MBeansManager mbeansManager, String node, String serverName, List<MetricGroup> metricGroups) {
        this.mbeansManager = mbeansManager;
        this.serverName = serverName;
        this.node = node;
        this.metricGroups = metricGroups;
        this.host = Utils.getHostByNode(mbeansManager.getNodeServerMBean());
    }

    public List<String> getMetrics() {
        List<String> stats = null;
        if (!"dmgr".equals(this.serverName)) {
            stats = getStats(getWSStats());
        }
        return stats;
    }

    private WSStats getWSStats() {
        WSStats wsStats = null;
        this.serverPerfMBean = mbeansManager.getMBean("WebSphere:type=Perf,node=" + this.node + ",process=" + this.serverName + ",*");
        ObjectName objectName = mbeansManager.getMBean("WebSphere:name=" + this.serverName + ",node=" + this.node + ",process=" + this.serverName + ",*");
        if (objectName != null) {
            String[] signature = new String[]{"javax.management.ObjectName", "java.lang.Boolean"};
            Object[] params = new Object[]{objectName, true};
            try {
                wsStats = (WSStats) mbeansManager.getClient().invoke(serverPerfMBean, "getStatsObject", params, signature);
            } catch (Exception e) {
                L4j.getL4j().error("Capturer ", e);
            }
        }
        return wsStats;
    }

    private List<String> getStats(WSStats wsStats) {
        List<String> stats = new LinkedList<String>();
        if (wsStats != null) {
            for (MetricGroup metricGroup : this.metricGroups) {
                WSStats auxStats = null;
                if(!metricGroup.isUniqueInstance()){
                    if(metricGroup.getInstanceFilter() == null || metricGroup.getInstanceFilter().size() > 0) {
                        for (String instance : metricGroup.getInstanceFilter()) {
                            auxStats = findStatsByMetricGroupType(instance, wsStats);
                            if (auxStats != null) {
                                stats.addAll(getStatsType(metricGroup, auxStats));
                            } else {
                                L4j.getL4j().warning("Node: " + this.node + " Server: " + this.serverName + " Not fount statstype " + metricGroup.getStatsType());
                            }
                        }
                    } else {
                        for(WSStats allstats: getAllInstances(metricGroup.getStatsType(), wsStats)){
                            stats.addAll(getStatsType(metricGroup, allstats));
                        }
                    }
                } else {
                    auxStats = findStatsByMetricGroupType(metricGroup.getStatsType(), wsStats);
                    if (auxStats != null) {
                        stats.addAll(getStatsType(metricGroup, auxStats));
                    } else {
                        L4j.getL4j().warning("Node: " + this.node + " Server: " + this.serverName + " Not fount statstype " + metricGroup.getStatsType());
                    }
                }

            }
        } else {
            L4j.getL4j().error("WSStats not found", new NullPointerException());
        }
        return stats;
    }

    public List<String> getStatsType(MetricGroup metricGroup, WSStats wsStats) {
        List<String> result = new LinkedList<String>();

        if (metricGroup == null || metricGroup.getMetrics() == null || metricGroup.getStatsType() == null || metricGroup.getMetrics().size() == 0) {
            throw new IllegalArgumentException();
        }
        String prefix = this.serverPerfMBean.getKeyProperty("cell") + "." + this.serverPerfMBean.getKeyProperty("process") + "." + metricGroup.getPrefix();
        for (Metric metric : metricGroup.getMetrics()) {
            WSStatistic wsStatistic = wsStats.getStatistic(metric.getId());
            result.addAll(UtilsWSStatistic.parseStatistics(prefix, wsStatistic));
        }
        return result;
    }

    public WSStats findStatsByMetricGroupType(String type, WSStats wsStats){
        WSStats result = null;
        WSStats aux = wsStats.getStats(type);
        if(aux == null && wsStats.getSubStats().length > 0) {
            for(WSStats substat: wsStats.getSubStats()){
                findStatsByMetricGroupType(type, substat);
            }
        } else if(aux != null){
            result = aux;
        }
        return result;
    }
    public List<WSStats> getAllInstances(String type, WSStats wsStats){
        List<WSStats> result = new LinkedList<WSStats>();
        for(WSStats allstats: wsStats.getSubStats()){
            result.add(allstats);
        }
        return  result;
    }

    public String getHost() {
        return this.host;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getNode() {
        return this.node;
    }
}