package cellhealth.utils.properties.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alberto Pascual on 6/08/15.
 */
public class PmiStatsType {

    private boolean unit;
    private String unitSeparator;
    private boolean separateMetric;
    private Map<String, Boolean> countStatistic;
    private Map<String, Boolean> doubleStatisc;
    private Map<String, Boolean> averageStatistic;
    private Map<String, Boolean> boundaryStatistic;
    private Map<String, Boolean> rangeStatistic;
    private Map<String, Boolean> timeStatitic;
    private Map<String, Boolean> boundedRangeStatistic;

    public PmiStatsType() {}

    public void setCountStatistic(String type, boolean state){
        this.countStatistic = new HashMap<String, Boolean>();
        this.countStatistic.put(type, state);
    }

    public Map<String, Boolean> getCountStatistic(){
        return this.countStatistic;
    }

    public void setDoubleStatisc(String type, boolean state){
        this.doubleStatisc = new HashMap<String, Boolean>();
        this.doubleStatisc.put(type, state);
    }

    public Map<String, Boolean> getAverageStatistic() {
        return averageStatistic;
    }

    public void setAverageStatistic(Map<String, Boolean> averageStatistic) {
        this.averageStatistic = averageStatistic;
    }

    public Map<String, Boolean> getBoundaryStatistic() {
        return boundaryStatistic;
    }

    public void setBoundaryStatistic(Map<String, Boolean> boundaryStatistic) {
        this.boundaryStatistic = boundaryStatistic;
    }

    public Map<String, Boolean> getRangeStatistic() {
        return rangeStatistic;
    }

    public void setRangeStatistic(Map<String, Boolean> rangeStatistic) {
        this.rangeStatistic = rangeStatistic;
    }

    public Map<String, Boolean> getTimeStatitic() {
        return timeStatitic;
    }

    public void setTimeStatitic(Map<String, Boolean> timeStatitic) {
        this.timeStatitic = timeStatitic;
    }

    public Map<String, Boolean> getBoundedRangeStatistic() {
        return boundedRangeStatistic;
    }

    public void setBoundedRangeStatistic(Map<String, Boolean> boundedRangeStatistic) {
        this.boundedRangeStatistic = boundedRangeStatistic;
    }

    public Map<String, Boolean> getDoubleStatisc(){
        return this.doubleStatisc;
    }

    public boolean isUnit() {
        return unit;
    }

    public void setUnit(boolean unit) {
        this.unit = unit;
    }

    public String getUnitSeparator() {
        return unitSeparator;
    }

    public void setUnitSeparator(String unitSeparator) {
        this.unitSeparator = unitSeparator;
    }

    public boolean isSeparateMetric() {
        return separateMetric;
    }

    public void setSeparateMetric(boolean separateMetric) {
        this.separateMetric = separateMetric;
    }

}
