package cellhealth.utils;

import com.ibm.websphere.pmi.stat.WSAverageStatistic;
import com.ibm.websphere.pmi.stat.WSBoundaryStatistic;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSCountStatistic;
import com.ibm.websphere.pmi.stat.WSDoubleStatistic;
import com.ibm.websphere.pmi.stat.WSRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStatistic;
import com.ibm.websphere.pmi.stat.WSTimeStatistic;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alberto Pascual on 18/07/15.
 */
public class UtilsWSStatistic {

    public static List<String> parseStatistics(String prefix, WSStatistic wsStatistic){
        List<String> result = new LinkedList<String>();
        String type = null;
        if (wsStatistic != null) {
            type = Utils.getWSStatisticType(wsStatistic);
            if ("CountStatistic".equals(type)) {
                WSCountStatistic countStatistic = (WSCountStatistic) wsStatistic;
                result.add(prefix + "." + wsStatistic.getName() + ".long " + countStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
            } else if ("DoubleStatistic".equals(type)) {
                WSDoubleStatistic doubleStatistic = (WSDoubleStatistic) wsStatistic;
                result.add(prefix + "." + wsStatistic.getName() + ".double " + doubleStatistic.getDouble() + " " + System.currentTimeMillis() / 1000L);
            } else if ("AverageStatistic".equals(type)) {
                result.addAll(getAverageStatistic(prefix, wsStatistic));
            } else if ("TimeStatistic".equals(type)) {
                result.addAll(getTimeStatistic(prefix, wsStatistic));
            } else if ("BoundaryStatistic".equals(type)) {
                result.addAll(getBoundaryStatistic(prefix, wsStatistic));
            } else if ("RangeStatistic".equals(type)) {
                result.addAll(getRangeStatistic(prefix, wsStatistic));
            } else if ("BoundedRangeStatistic".equals(type)) {
                result.addAll(getBoundedRangeStatistic(prefix, wsStatistic));
            }
        } else {
            //L4j.getL4j().info( metricGroup.getStatsType() + " " + metric.getId() + " Unable to get " + metricGroup.getStatsType() + " to id " + metric.getId() + " stats for server: " + this.serverName);
        }
        return result;
    }

    private static List<String> getAverageStatistic(String prefix, WSStatistic wsStatistic) {
        List<String> result = new LinkedList<String>();
        WSAverageStatistic averageStatistic = (WSAverageStatistic) wsStatistic;
        result.add(prefix + "." + wsStatistic.getName() + ".count " + averageStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".total " + averageStatistic.getTotal() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".min " + averageStatistic.getMin() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".max " + averageStatistic.getMax() + " " + System.currentTimeMillis() / 1000L);
        return result;
    }

    private static List<String> getTimeStatistic(String prefix, WSStatistic wsStatistic) {
        List<String> result = new LinkedList<String>();
        WSTimeStatistic timeStatistic = (WSTimeStatistic) wsStatistic;
        result.add(prefix + "." + wsStatistic.getName() + ".count " + timeStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".total " + timeStatistic.getTotal() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".min " + timeStatistic.getMin() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".max " + timeStatistic.getMax() + " " + System.currentTimeMillis() / 1000L);
        return result;
    }

    private static List<String> getBoundaryStatistic(String prefix, WSStatistic wsStatistic){
        List<String> result = new LinkedList<String>();
        WSBoundaryStatistic boundaryStatistic = (WSBoundaryStatistic) wsStatistic;
        result.add(prefix + "." + wsStatistic.getName() + ".upperbound " + boundaryStatistic.getUpperBound() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".lowerbound " + boundaryStatistic.getLowerBound() + " " + System.currentTimeMillis() / 1000L);
        return result;
    }

    private static List<String> getRangeStatistic(String prefix, WSStatistic wsStatistic){
        List<String> result = new LinkedList<String>();
        WSRangeStatistic rangeStatistic = (WSRangeStatistic) wsStatistic;
        result.add(prefix + "." + wsStatistic.getName() + ".highwatermark " + rangeStatistic.getHighWaterMark() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".lowwatermark " + rangeStatistic.getLowWaterMark() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".current " + rangeStatistic.getCurrent() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".integral " + rangeStatistic.getIntegral() + " " + System.currentTimeMillis() / 1000L);
        return result;
    }

    private static List<String> getBoundedRangeStatistic(String prefix, WSStatistic wsStatistic){
        List<String> result = new LinkedList<String>();
        WSBoundedRangeStatistic boundedRangeStatistic = (WSBoundedRangeStatistic) wsStatistic;
        result.add(prefix + "." + wsStatistic.getName() + ".upperbound " + boundedRangeStatistic.getUpperBound() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".lowerbound " + boundedRangeStatistic.getLowerBound() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".highwatermark " + boundedRangeStatistic.getHighWaterMark() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".lowwatermark " + boundedRangeStatistic.getLowWaterMark() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".current " + boundedRangeStatistic.getCurrent() + " " + System.currentTimeMillis() / 1000L);
        result.add(prefix + "." + wsStatistic.getName() + ".integral " + boundedRangeStatistic.getIntegral() + " " + System.currentTimeMillis() / 1000L);
        return result;
    }
}
