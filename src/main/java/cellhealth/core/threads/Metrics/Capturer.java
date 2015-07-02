package cellhealth.core.threads.Metrics;

import cellhealth.core.connection.MBeansManager;
import cellhealth.utils.Utils;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.Metric;
import cellhealth.utils.properties.MetricGroup;
import com.ibm.websphere.pmi.stat.WSAverageStatistic;
import com.ibm.websphere.pmi.stat.WSBoundaryStatistic;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSCountStatistic;
import com.ibm.websphere.pmi.stat.WSDoubleStatistic;
import com.ibm.websphere.pmi.stat.WSRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.websphere.pmi.stat.WSTimeStatistic;

import javax.management.ObjectName;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Capturer {

    private MBeansManager mbeansManager;
    private String serverName;
    private String node;
    private List<MetricGroup> metricGroups;


    public Capturer(MBeansManager mbeansManager, String node, String serverName, List<MetricGroup> metricGroups) {
        this.mbeansManager = mbeansManager;
        this.serverName = serverName;
        this.node = node;
        this.metricGroups = metricGroups;
    }

    public String getStats() throws Exception {
        if(!"dmgr".equals(serverName)) {
            ObjectName serverPerfMBean = mbeansManager.getMBean("WebSphere:type=Perf,node=" + this.node + ",process=" + this.serverName + ",*");
            Map<String, WSStats> statsMap = new TreeMap<String, WSStats>();
            try {
                for(ObjectName objectName: mbeansManager.getMBeans("WebSphere:*")){
                    String[] signature = new String[] {"javax.management.ObjectName","java.lang.Boolean"};
                    Object[] params = new Object[] {objectName, true};
                    WSStats wsStats = (WSStats) mbeansManager.getClient().invoke(serverPerfMBean, "getStatsObject", params, signature);
                    if(wsStats != null) {
                        statsMap.put(wsStats.getName(), wsStats);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(MetricGroup metricGroup: this.metricGroups){
                for(String line:getStatsType(metricGroup, statsMap)){
                    System.out.println(line);
                }
            }
        }
        return "";
    }

    public List<String> getStatsType(MetricGroup metricGroup, Map<String, WSStats> statsMap) throws Exception {
        List<String> result = new LinkedList<String>();

        if(metricGroup == null || metricGroup.getMetrics() == null || metricGroup.getType() == null || metricGroup.getMetrics().size() == 0) {
            throw new IllegalArgumentException();
        }
        String prefix = metricGroup.getPrefix();
        WSStats stats = statsMap.get(metricGroup.getType());
        if(stats == null){
            L4j.getL4j().info("Unable to get " + metricGroup.getType() + " Stats for server: " + this.serverName);
        } else {
            for (Metric metric : metricGroup.getMetrics()) {
                WSStatistic wsStatistic = stats.getStatistic(metric.getId());
                String type = null;
                if (wsStatistic != null) {
                    type = Utils.getWSStatisticType(wsStatistic);
                } else {
                    L4j.getL4j().info("Unable to get " + metric.getName() + " Stats for server: " + this.serverName);
                }
                if ("CountStatistic".equals(type)) {
                    WSCountStatistic countStatistic = (WSCountStatistic) wsStatistic;
                    result.add(prefix + "." + wsStatistic.getName() + ".long." +countStatistic.getCount());
                } else if ("DoubleStatistic".equals(type)) {
                    WSDoubleStatistic doubleStatistic = (WSDoubleStatistic) wsStatistic;
                    result.add(prefix + "." + wsStatistic.getName() + ".double."+ doubleStatistic.getDouble());
                } else if ("AverageStatistic".equals(type)) {
                    WSAverageStatistic averageStatistic = (WSAverageStatistic) wsStatistic;
                    result.add(prefix + "." + wsStatistic.getName() + ".count." + averageStatistic.getCount());
                    result.add(prefix + "." + wsStatistic.getName() + ".total." + averageStatistic.getTotal());
                    result.add(prefix + "." + wsStatistic.getName() + ".min." + averageStatistic.getMin());
                    result.add(prefix + "." + wsStatistic.getName() + ".max." +averageStatistic.getMax());
                } else if ("TimeStatistic".equals(type)) {
                    WSTimeStatistic timeStatistic = (WSTimeStatistic) wsStatistic;
                    result.add(prefix + "." + wsStatistic.getName() + ".count." + timeStatistic.getCount());
                    result.add(prefix + "." + wsStatistic.getName() + ".total." + timeStatistic.getTotal());
                    result.add(prefix + "." + wsStatistic.getName() + ".min." + timeStatistic.getMin());
                    result.add(prefix + "." + wsStatistic.getName() + ".max." + timeStatistic.getMax());
                } else if ("BoundaryStatistic".equals(type)) {
                    WSBoundaryStatistic boundaryStatistic = (WSBoundaryStatistic) wsStatistic;
                    result.add(prefix + "." + wsStatistic.getName() + ".upperbound." + boundaryStatistic.getUpperBound());
                    result.add(prefix + "." + wsStatistic.getName() + ".lowerbound." + boundaryStatistic.getLowerBound());
                } else if ("RangeStatistic".equals(type)) {
                    WSRangeStatistic rangeStatistic = (WSRangeStatistic) wsStatistic;
                    result.add(prefix + "." + wsStatistic.getName() + ".highwatermark." + rangeStatistic.getHighWaterMark());
                    result.add(prefix + "." + wsStatistic.getName() + ".lowwatermark." + rangeStatistic.getLowWaterMark());
                    result.add(prefix + "." + wsStatistic.getName() + ".current." + rangeStatistic.getCurrent());
                    result.add(prefix + "." + wsStatistic.getName() + ".integral." + rangeStatistic.getIntegral());
                } else if ("BoundedRangeStatistic".equals(type)) {
                    WSBoundedRangeStatistic boundedRangeStatistic = (WSBoundedRangeStatistic) wsStatistic;
                    result.add(prefix + "." + wsStatistic.getName() + ".upperbound." + boundedRangeStatistic.getUpperBound());
                    result.add(prefix + "." + wsStatistic.getName() + ".lowerbound." + boundedRangeStatistic.getLowerBound());
                    result.add(prefix + "." + wsStatistic.getName() + ".highwatermark." + boundedRangeStatistic.getHighWaterMark());
                    result.add(prefix + "." + wsStatistic.getName() + ".lowwatermark." + boundedRangeStatistic.getLowWaterMark());
                    result.add(prefix + "." + wsStatistic.getName() + ".current." + boundedRangeStatistic.getCurrent());
                    result.add(prefix + "." + wsStatistic.getName() + ".integral." + boundedRangeStatistic.getIntegral());
                }
            }
        }
        return result;
    }
}