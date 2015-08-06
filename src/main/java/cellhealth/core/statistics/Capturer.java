package cellhealth.core.statistics;

import cellhealth.core.connection.MBeansManager;
import cellhealth.utils.Utils;
import cellhealth.utils.UtilsWSStatistic;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.xml.CellHealthMetrics;
import cellhealth.utils.properties.xml.Metric;
import cellhealth.utils.properties.xml.MetricGroup;
import cellhealth.utils.properties.xml.PmiStatsType;
import com.ibm.websphere.pmi.stat.WSStatistic;
import com.ibm.websphere.pmi.stat.WSStats;

import javax.management.ObjectName;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Capturer {

    private MBeansManager mbeansManager;
    private String serverName;
    private String node;
    private List<MetricGroup> metricGroups;
    private PmiStatsType pmiStatsType;
    private ObjectName serverPerfMBean;
    private String host;
    private String prefix;

    public Capturer(MBeansManager mbeansManager, String node, String serverName, CellHealthMetrics cellHealthMetrics) {
        this.mbeansManager = mbeansManager;
        this.serverName = serverName;
        this.node = node;
        this.metricGroups = cellHealthMetrics.getMetricGroups();
        this.pmiStatsType = cellHealthMetrics.getPmiStatsType();
        //Obtiene el host de un servidor
        this.host = Utils.getHostByNode(mbeansManager.getNodeServerMBean());
    }

    /**
     * Metodo que obtiene las estadisticas de servidor pasado por parametros, menos DMGR
     * @return listado de estadisticas
     */
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
        ObjectName objectName = mbeansManager.getMBean("WebSphere:name=" + this.serverName + ",node=" + this.node + ",process=" + this.serverName + ",type=Server,*");
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
                WSStats especificStats = wsStats.getStats(metricGroup.getStatsType());
                if(especificStats != null){
                    stats.addAll(getStatsType(metricGroup, especificStats, true));
                } else {
                    L4j.getL4j().warning("Node: " + this.node + " Server: " + this.serverName + " Not found statstype " + metricGroup.getStatsType());
                }
            }
        } else {
            L4j.getL4j().warning("Node: " + this.node + " Server: " + this.serverName + " Not found stats");
        }
        return stats;
    }
    public List<String> getStatsType(MetricGroup metricGroup, WSStats wsStats, boolean isInstance) {
        List<String> result = new LinkedList<String>();
        List<String> globalStats;
        List<String> instances;
        this.prefix = this.serverPerfMBean.getKeyProperty("cell") + "." + this.serverPerfMBean.getKeyProperty("process") + "." + metricGroup.getPrefix();
        globalStats = getGlobalStats(wsStats, metricGroup, this.prefix);
        if(globalStats.size() > 0) {
            result.addAll(globalStats);
        }
        if(wsStats.getSubStats().length > 0){
            instances = getInstances(Arrays.asList(wsStats.getSubStats()), metricGroup, this.prefix, isInstance);
            if(instances.size() > 0){
                result.addAll(instances);
            }
        }
        return result;
    }

    public List<String> getInstances(List<WSStats> wsStats, MetricGroup metricGroup, String path, boolean isInstance) {
        List<String> result = new LinkedList<String>();
        for(WSStats substats: wsStats){
            if(isInstance) {
                if(metricGroup.getInstanceFilter() != null && metricGroup.getInstanceFilter().size() > 0 && metricGroup.getInstanceFilter().contains(substats.getName())) {
                    String auxPath = path + "." + Utils.getParseBeanName(substats.getName());
                    for(Metric metric: metricGroup.getMetrics()){
                        WSStatistic wsStatistic = substats.getStatistic(metric.getId());
                        result.addAll(UtilsWSStatistic.parseStatistics(auxPath, wsStatistic, this.pmiStatsType));

                    }
                    if(substats.getSubStats().length > 0){
                        result.addAll(getInstances(Arrays.asList(substats.getSubStats()), metricGroup, auxPath,false));
                    }
                } else if(metricGroup.getInstanceFilter() == null || metricGroup.getInstanceFilter().size() == 0) {
                    String auxPath = path + "." + Utils.getParseBeanName(substats.getName());
                    for(Metric metric: metricGroup.getMetrics()){
                        WSStatistic wsStatistic = substats.getStatistic(metric.getId());
                        result.addAll(UtilsWSStatistic.parseStatistics(auxPath, wsStatistic, this.pmiStatsType));

                    }
                    if(substats.getSubStats().length > 0){
                        result.addAll(getInstances(Arrays.asList(substats.getSubStats()), metricGroup, auxPath,false));
                    }
                }
            } else {
                String auxPath = path + "." + Utils.getParseBeanName(substats.getName());
                for (Metric metric : metricGroup.getMetrics()) {
                    WSStatistic wsStatistic = substats.getStatistic(metric.getId());
                    result.addAll(UtilsWSStatistic.parseStatistics(auxPath, wsStatistic, this.pmiStatsType));

                }
                if (substats.getSubStats().length > 0) {
                    result.addAll(getInstances(Arrays.asList(substats.getSubStats()), metricGroup, auxPath, false));
                }
            }

        }
        return result;
    }

    public List<String> getGlobalStats(WSStats wsStats, MetricGroup metricGroup, String prefix) {
        List<String> result = new LinkedList<String>();
        if((metricGroup.getAllowGlobal() && wsStats.getSubStats().length > 0) || (wsStats.getSubStats() == null || wsStats.getSubStats().length == 0)){
            if(metricGroup.getAllowGlobal() && wsStats.getSubStats().length > 0){
                prefix = prefix + ".global";
            }
            for (Metric metric : metricGroup.getMetrics()) {
                WSStatistic wsStatistic = wsStats.getStatistic(metric.getId());
                result.addAll(UtilsWSStatistic.parseStatistics(prefix, wsStatistic, this.pmiStatsType));
            }

        }
        return result;
    }

    public String getHost() {
        return this.host;
    }

    public String getPrefix(){
        return this.prefix;
    }

}