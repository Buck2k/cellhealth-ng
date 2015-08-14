package cellhealth.core.statistics.parser;

import cellhealth.core.statistics.Stats;
import cellhealth.utils.properties.xml.PmiStatsType;
import com.ibm.websphere.pmi.stat.WSDoubleStatistic;
import com.ibm.websphere.pmi.stat.WSStatistic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alberto Pascual on 13/08/15.
 */
public class ParserDoubleStatistic<E extends WSDoubleStatistic> extends AbstractParser<E>{

    private String metricName;
    private E parserStatistic;
    private Map<String, Boolean> mapPmiStatsType;
    private String metricSeparator;
    private String node;
    private String prefix;
    private String unity;

    public ParserDoubleStatistic(PmiStatsType pmiStatsType, WSStatistic wsStatistic, String node, String prefix, String metricName) {
        this.metricName = metricName;
        this.metricSeparator = getMetricSeparator(pmiStatsType);
        this.parserStatistic =  (E) wsStatistic;
        this.unity = this.getUnity(pmiStatsType, this.parserStatistic);
        this.mapPmiStatsType = pmiStatsType.getDoubleStatisc();
        this.prefix = prefix;
        this.node = node;
    }

    public List<Stats> getStatistic() {
        List<Stats> result = new LinkedList<Stats>();
        for (Map.Entry<String,Boolean> entry : this.mapPmiStatsType.entrySet()) {
            if(entry.getValue() != null && entry.getValue()){
                String method = entry.getKey();
                Stats stats = new Stats();
                stats.setHost(this.node);
                String metric = "";
                if("count".equals(method)){
                    metric = String.valueOf(this.parserStatistic.getDouble());
                }
                stats.setMetric(this.prefix + "." + this.metricName + this.metricSeparator + method + this.unity + metric + " " + System.currentTimeMillis() / 1000L);
                result.add(stats);
            }
        }
        return result;
    }
}
