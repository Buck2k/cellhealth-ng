package cellhealth.utils;

import cellhealth.utils.properties.xml.PmiStatsType;
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
import java.util.Map;

/**
 * Created by Alberto Pascual on 18/07/15.
 */
public class UtilsWSStatistic {

    public static List<String> parseStatistics(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
        List<String> result = new LinkedList<String>();
        String type;
        String separator = getMetricSeparator(pmiStatsType);
        if (wsStatistic != null && (wsStatistic.getName() != null || pmiStatsType.getMetricName() != null)) {
            String metricName = (pmiStatsType.getMetricName() == null || pmiStatsType.getMetricName().length() == 0)?wsStatistic.getName():pmiStatsType.getMetricName();
            type = Utils.getWSStatisticType(wsStatistic);
            if ("CountStatistic".equals(type)) {
                Map<String, Boolean> mapCounterStatistic = pmiStatsType.getCountStatistic();
                WSCountStatistic countStatistic = (WSCountStatistic) wsStatistic;
                if(mapCounterStatistic.get("count")){
                    String separatorType = (pmiStatsType.isUnit())?"_" + countStatistic.getUnit() + " ":" ";
                    if("N/A".equals(countStatistic.getUnit())) {
                        separatorType = " ";
                    }
                    separatorType = separatorType.toLowerCase();
                    result.add(prefix + "." + metricName + separator +"long" + separatorType + countStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
                }
            } else if ("DoubleStatistic".equals(type)) {
                WSDoubleStatistic doubleStatistic = (WSDoubleStatistic) wsStatistic;
                Map<String, Boolean> mapDoubleStatistic = pmiStatsType.getCountStatistic();
                if(mapDoubleStatistic.get("count")) {
                    String separatorType = (pmiStatsType.isUnit())?"_" + doubleStatistic.getUnit() + " ":" ";
                    if("N/A".equals(doubleStatistic.getUnit())) {
                        separatorType = " ";
                    }
                    separatorType = separatorType.toLowerCase();
                    result.add(prefix + "." + metricName + separator +"double" + separatorType + doubleStatistic.getDouble() + " " + System.currentTimeMillis() / 1000L);
                }
            } else if ("AverageStatistic".equals(type)) {
                result.addAll(getAverageStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("TimeStatistic".equals(type)) {
                result.addAll(getTimeStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("BoundaryStatistic".equals(type)) {
                result.addAll(getBoundaryStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("RangeStatistic".equals(type)) {
                result.addAll(getRangeStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("BoundedRangeStatistic".equals(type)) {
                result.addAll(getBoundedRangeStatistic(prefix, wsStatistic, pmiStatsType));
            }
        } else if(wsStatistic != null){
            //l4j
        }
        return result;
    }

    private static List<String> getAverageStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType) {
        String metricName = (pmiStatsType.getMetricName() == null || pmiStatsType.getMetricName().length() == 0)?wsStatistic.getName():pmiStatsType.getMetricName();
        String metricSeparator = getMetricSeparator(pmiStatsType);
        Map<String, Boolean> mapAverageStatistic = pmiStatsType.getAverageStatistic();
        List<String> result = new LinkedList<String>();
        WSAverageStatistic averageStatistic = (WSAverageStatistic) wsStatistic;
        String unity = (pmiStatsType.isUnit())?"_" + averageStatistic.getUnit() + " ":" ";
        if("N/A".equals(averageStatistic.getUnit())) {
            unity = " ";
        }
        unity = unity.toLowerCase();
        if(mapAverageStatistic.get("count")) {
            result.add(prefix + "." + metricName + metricSeparator + "count" + unity + averageStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapAverageStatistic.get("total")) {
            result.add(prefix + "." + metricName + metricSeparator + "total" + unity + averageStatistic.getTotal() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapAverageStatistic.get("min")) {
            result.add(prefix + "." + metricName + metricSeparator + "min" + unity + averageStatistic.getMin() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapAverageStatistic.get("max")) {
            result.add(prefix + "." + metricName + metricSeparator +"max" + unity + averageStatistic.getMax() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getTimeStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType) {
        String metricName = (pmiStatsType.getMetricName() == null || pmiStatsType.getMetricName().length() == 0)?wsStatistic.getName():pmiStatsType.getMetricName();
        Map<String, Boolean> mapTimeStatistic = pmiStatsType.getTimeStatitic();
        String metricSeparator = getMetricSeparator(pmiStatsType);
        List<String> result = new LinkedList<String>();
        WSTimeStatistic timeStatistic = (WSTimeStatistic) wsStatistic;
        String unity = (pmiStatsType.isUnit())?"_" + timeStatistic.getUnit() + " ":" ";
        if("N/A".equals(timeStatistic.getUnit())) {
            unity = " ";
        }
        unity = unity.toLowerCase();
        if(mapTimeStatistic.get("count")) {
            result.add(prefix + "." + metricName + metricSeparator + "count" + unity + timeStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("total")) {
            result.add(prefix + "." + metricName + metricSeparator + "total" + unity + timeStatistic.getTotal() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("min")) {
            result.add(prefix + "." + metricName + metricSeparator + "min" + unity + timeStatistic.getMin() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("max")) {
            result.add(prefix + "." + metricName + metricSeparator + "max" + unity + timeStatistic.getMax() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getBoundaryStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
        String metricName = (pmiStatsType.getMetricName() == null || pmiStatsType.getMetricName().length() == 0)?wsStatistic.getName():pmiStatsType.getMetricName();
        Map<String, Boolean> mapBoundaryStatistic = pmiStatsType.getBoundaryStatistic();
        String metricSeparator = getMetricSeparator(pmiStatsType);
        List<String> result = new LinkedList<String>();
        WSBoundaryStatistic boundaryStatistic = (WSBoundaryStatistic) wsStatistic;
        String unity = (pmiStatsType.isUnit())?"_" + boundaryStatistic.getUnit() + " ":" ";
        if("N/A".equals(boundaryStatistic.getUnit())) {
            unity = " ";
        }
        unity = unity.toLowerCase();
        if(mapBoundaryStatistic.get("upperBound")) {
            result.add(prefix + "." + metricName + metricSeparator + "upperbound" + unity + boundaryStatistic.getUpperBound() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundaryStatistic.get("lowebBound")) {
            result.add(prefix + "." + metricName + metricSeparator + "lowerbound" + unity + boundaryStatistic.getLowerBound() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getRangeStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
        String metricName = (pmiStatsType.getMetricName() == null || pmiStatsType.getMetricName().length() == 0)?wsStatistic.getName():pmiStatsType.getMetricName();
        Map<String, Boolean> mapRangeStatistic = pmiStatsType.getRangeStatistic();
        String metricSeparator = getMetricSeparator(pmiStatsType);
        List<String> result = new LinkedList<String>();
        WSRangeStatistic rangeStatistic = (WSRangeStatistic) wsStatistic;
        String unity = (pmiStatsType.isUnit())?"_" + rangeStatistic.getUnit() + " ":" ";
        if("N/A".equals(rangeStatistic.getUnit())) {
            unity = " ";
        }
        unity = unity.toLowerCase();
        if(mapRangeStatistic.get("highWaterMark")) {
            result.add(prefix + "." + metricName + metricSeparator + "highwatermark" + unity + rangeStatistic.getHighWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapRangeStatistic.get("lowWaterMark")) {
            result.add(prefix + "." + metricName + metricSeparator + "lowwatermark" + unity + rangeStatistic.getLowWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapRangeStatistic.get("current")) {
            result.add(prefix + "." + metricName + metricSeparator + "current" + unity + rangeStatistic.getCurrent() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapRangeStatistic.get("integral")) {
            result.add(prefix + "." + metricName + metricSeparator + "integral" + unity + rangeStatistic.getIntegral() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getBoundedRangeStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
        String metricName = (pmiStatsType.getMetricName() == null || pmiStatsType.getMetricName().length() == 0)?wsStatistic.getName():pmiStatsType.getMetricName();
        Map<String, Boolean> mapBoundedRangeStatistic = pmiStatsType.getBoundedRangeStatistic();
        String metricSeparator = getMetricSeparator(pmiStatsType);
        List<String> result = new LinkedList<String>();
        WSBoundedRangeStatistic boundedRangeStatistic = (WSBoundedRangeStatistic) wsStatistic;
        String unity = (pmiStatsType.isUnit())?"_" + boundedRangeStatistic.getUnit() + " ":" ";
        if("N/A".equals(boundedRangeStatistic.getUnit())) {
            unity = " ";
        }
        unity = unity.toLowerCase();
        if(mapBoundedRangeStatistic.get("upperBound")) {
            result.add(prefix + "." + metricName + metricSeparator + "upperbound" + unity + boundedRangeStatistic.getUpperBound() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("lowebBound")) {
            result.add(prefix + "." + metricName + metricSeparator + "lowerbound" + unity + boundedRangeStatistic.getLowerBound() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("highWaterMark")) {
            result.add(prefix + "." + metricName + metricSeparator + "highwatermark" + unity + boundedRangeStatistic.getHighWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("lowWaterMark")) {
            result.add(prefix + "." + metricName + metricSeparator + "lowwatermark" + unity + boundedRangeStatistic.getLowWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("current")) {
            result.add(prefix + "." + metricName + metricSeparator + "current" + unity + boundedRangeStatistic.getCurrent() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("integral")) {
            result.add(prefix + "." + metricName + metricSeparator + "integral" + unity + boundedRangeStatistic.getIntegral() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    public static String getMetricSeparator(PmiStatsType pmiStatsType){
        if(pmiStatsType.isSeparateMetric()){
            return ".";
        } else {
            return "_";
        }
    }
}
