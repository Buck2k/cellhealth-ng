package cellhealth.core.statistics;

import cellhealth.utils.properties.xml.PmiStatsType;
import com.ibm.websphere.pmi.stat.WSStatistic;

/**
 * Created by Alberto Pascual on 13/08/15.
 */
public abstract class AbstractParser<E extends WSStatistic> implements ParserStatistic<E> {

    public String getMetricSeparator(PmiStatsType pmiStatsType){
        if(pmiStatsType.isSeparateMetric()){
            return ".";
        } else {
            return "_";
        }
    }

    public String getUnity(PmiStatsType pmiStatsType, E statistic) {
        String unity = (pmiStatsType.isUnit())?"_" + statistic.getUnit() + " ":" ";
        if("N/A".equals(statistic.getUnit()) || statistic.getUnit() == null) {
            unity = " ";
        }
        return unity.toLowerCase();
    }

}
