package cellhealth.core.statistics.parser;

import cellhealth.core.statistics.Stats;
import cellhealth.utils.properties.xml.PmiStatsType;
import com.ibm.websphere.pmi.stat.WSAverageStatistic;
import com.ibm.websphere.pmi.stat.WSStatistic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alberto Pascual on 13/08/15.
 */
public class ParserAverageStatistic<E extends WSAverageStatistic> extends AbstractParser<E>{

    private String metricName;
    private E parserStatistic;
    private Map<String, Boolean> mapPmiStatsType;
    private String metricSeparator;
    private String node;
    private String prefix;
    private String unity;

    public ParserAverageStatistic(PmiStatsType pmiStatsType, WSStatistic wsStatistic, String node, String prefix, String metricName) {
        this.metricName = metricName;
        this.metricSeparator = getMetricSeparator(pmiStatsType);
        this.parserStatistic =  (E) wsStatistic;
        this.unity = this.getUnity(pmiStatsType, this.parserStatistic);
        this.mapPmiStatsType = pmiStatsType.getAverageStatistic();
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
                    metric = String.valueOf(this.parserStatistic.getCount());
                } else if("total".equals(method)) {
                    metric = String.valueOf(this.parserStatistic.getTotal());
                } else if("min".equals(method)) {
                    metric = String.valueOf(this.parserStatistic.getMin());
                } else if("max".equals(method)){
                    metric = String.valueOf(this.parserStatistic.getMax());
                }
                stats.setMetric(this.prefix + "." + this.metricName + this.metricSeparator + method + this.unity + metric + " " + System.currentTimeMillis() / 1000L);
                result.add(stats);
            }
        }
        return result;
    }
}