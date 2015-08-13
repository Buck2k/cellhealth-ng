package cellhealth.core.statistics;

import cellhealth.utils.properties.xml.PmiStatsType;
import com.ibm.websphere.pmi.stat.WSStatistic;

import java.util.List;

/**
 * Created by Alberto Pascual on 13/08/15.
 */
public interface ParserStatistic<E extends WSStatistic> {

    public List<Stats> getStatistic();

    public String getUnity(PmiStatsType pmiStatsType, E statistic);

}
