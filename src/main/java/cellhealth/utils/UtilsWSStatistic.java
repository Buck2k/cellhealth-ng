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

import java.util.HashMap;
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
        if (wsStatistic != null && wsStatistic.getName() != null) {
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
                    result.add(prefix + "." + wsStatistic.getName() + separator +"long" + separatorType + countStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
                }
            } else if ("DoubleStatistic".equals(type)) {
                WSDoubleStatistic doubleStatistic = (WSDoubleStatistic) wsStatistic;
                Map<String, Boolean> mapDoubleStatistic = pmiStatsType.getCountStatistic();
                if(mapDoubleStatistic.get("count")) {
                    String separatorType = (pmiStatsType.isUnit())?"_" + doubleStatistic.getUnit() + " ":" ";
                    separatorType = separatorType.toLowerCase();
                    if("N/A".equals(doubleStatistic.getUnit())) {
                        separatorType = " ";
                    }
                    result.add(prefix + "." + wsStatistic.getName() + separator +"double" + separatorType + doubleStatistic.getDouble() + " " + System.currentTimeMillis() / 1000L);
                }
            } else if ("AverageStatistic".equals(type)) {
                result.addAll(getAverageStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("TimeStatistic".equals(type)) {
                result.addAll(getTimeStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("BoundaryStatistic".equals(type)) {
                result.addAll(getBoundaryStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("RangeStatistic".equals(type)) {
                System.out.println("2");
                result.addAll(getRangeStatistic(prefix, wsStatistic, pmiStatsType));
            } else if ("BoundedRangeStatistic".equals(type)) {
                System.out.println("3");
                result.addAll(getBoundedRangeStatistic(prefix, wsStatistic, pmiStatsType));
            }
        } else if(wsStatistic != null && wsStatistic.getName() == null){
            //l4j
        }
        return result;
    }

    private static List<String> getAverageStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType) {
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
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "count" + unity + averageStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapAverageStatistic.get("total")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "total" + unity + averageStatistic.getTotal() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapAverageStatistic.get("min")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "min" + unity + averageStatistic.getMin() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapAverageStatistic.get("max")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator +"max" + unity + averageStatistic.getMax() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getTimeStatistics(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType) {
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
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "count" + unity + timeStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("total")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "total" + unity + timeStatistic.getTotal() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("min")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "min" + unity + timeStatistic.getMin() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("max")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "max" + unity + timeStatistic.getMax() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getTimeStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType) {
        //TODO
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
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "count" + unity + timeStatistic.getCount() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("total")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "total" + unity + timeStatistic.getTotal() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("min")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "min" + unity + timeStatistic.getMin() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapTimeStatistic.get("max")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "max" + unity + timeStatistic.getMax() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getBoundaryStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
        List<String> bondaryStatistics = new LinkedList<String>();
        prefix = prefix + "." + wsStatistic.getName() + getMetricSeparator(pmiStatsType);
        Long timestamp = (System.currentTimeMillis() / 1000L);
        for(Map.Entry<String,String> entry: getTypes("boundaryStatistic", wsStatistic).entrySet()){
            if(pmiStatsType.getBoundaryStatistic().get(entry.getKey())){
                bondaryStatistics.add(prefix + entry.getKey() + getUnity(pmiStatsType, wsStatistic) + entry.getValue() + " " + timestamp);
            }
        }
        return bondaryStatistics;
    }

    private static List<String> getRangeStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
        List<String> rangeStatistics = new LinkedList<String>();
        prefix = prefix + "." + wsStatistic.getName() + getMetricSeparator(pmiStatsType);
        Long timestamp = (System.currentTimeMillis() / 1000L);
        for(Map.Entry<String,String> entry: getTypes("rangeStatistic", wsStatistic).entrySet()){
            if(pmiStatsType.getRangeStatistic().get(entry.getKey())){
                rangeStatistics.add(prefix + entry.getKey() + getUnity(pmiStatsType, wsStatistic) + entry.getValue() + " " + timestamp);
            }
        }
        return rangeStatistics;
    }

    private static String getUnity(PmiStatsType pmiStatsType, WSStatistic wsStatistic){
        String unity = (pmiStatsType.isUnit())?"_" + wsStatistic.getUnit() + " ":" ";
        if("N/A".equals(wsStatistic.getUnit())) {
            unity = " ";
        }
        return unity.toLowerCase();
    }

    private static Map<String, String> getTypes(String statisticType, WSStatistic wsStatistic){
        Map<String, String> metrics = new HashMap<String, String>();
        if("boundaryStatistic".equals(statisticType)) {
            WSBoundaryStatistic wsBoundaryStatistic = (WSBoundaryStatistic) wsStatistic;
            metrics.put("upperbound", String.valueOf(wsBoundaryStatistic.getUpperBound()));
            metrics.put("lowebound", String.valueOf(wsBoundaryStatistic.getLowerBound()));
        } else if("rangeStatistic".equals(statisticType)) {
            WSRangeStatistic wsRangeStatistic = (WSRangeStatistic) wsStatistic;
            metrics.put("highWaterMark", String.valueOf(wsRangeStatistic.getHighWaterMark()));
            metrics.put("lowWaterMark", String.valueOf(wsRangeStatistic.getLowWaterMark()));
            metrics.put("current", String.valueOf(wsRangeStatistic.getCurrent()));
            metrics.put("integral", String.valueOf(wsRangeStatistic.getIntegral()));
        } else if("timeStatistic".equals(statisticType)){
            WSTimeStatistic wsTimeStatistic = (WSTimeStatistic) wsStatistic;
            metrics.put("count", String.valueOf(wsTimeStatistic.getCount()));
            metrics.put("total", String.valueOf(wsTimeStatistic.getTotal()));
            metrics.put("min", String.valueOf(wsTimeStatistic.getMin()));
            metrics.put("max", String.valueOf(wsTimeStatistic.getMax()));
        }
        return metrics;
    }


    private static List<String> getRangeStatistics(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
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
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "highwatermark" + unity + rangeStatistic.getHighWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapRangeStatistic.get("lowWaterMark")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "lowwatermark" + unity + rangeStatistic.getLowWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapRangeStatistic.get("current")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "current" + unity + rangeStatistic.getCurrent() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapRangeStatistic.get("integral")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "integral" + unity + rangeStatistic.getIntegral() + " " + System.currentTimeMillis() / 1000L);
        }
        return result;
    }

    private static List<String> getBoundedRangeStatistic(String prefix, WSStatistic wsStatistic, PmiStatsType pmiStatsType){
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
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "upperbound" + unity + boundedRangeStatistic.getUpperBound() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("lowebBound")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "lowerbound" + unity + boundedRangeStatistic.getLowerBound() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("highWaterMark")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "highwatermark" + unity + boundedRangeStatistic.getHighWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("lowWaterMark")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "lowwatermark" + unity + boundedRangeStatistic.getLowWaterMark() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("current")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "current" + unity + boundedRangeStatistic.getCurrent() + " " + System.currentTimeMillis() / 1000L);
        }
        if(mapBoundedRangeStatistic.get("integral")) {
            result.add(prefix + "." + wsStatistic.getName() + metricSeparator + "integral" + unity + boundedRangeStatistic.getIntegral() + " " + System.currentTimeMillis() / 1000L);
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
