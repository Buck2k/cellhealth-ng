package cellhealth.core.statistics;

import cellhealth.utils.Utils;
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

/**
 * Created by Alberto Pascual on 13/08/15.
 */
public class ParserWSStatistics {

    private String prefix;
    private WSStatistic wsStatistic;
    private PmiStatsType pmiStatsType;
    private String node;
    private String metriName;

    public ParserWSStatistics(WSStatistic wsStatistic, PmiStatsType pmiStatsType, String node, String prefix, String metriName){
        this.prefix = prefix;
        this.wsStatistic = wsStatistic;
        this.pmiStatsType = pmiStatsType;
        this.node = node;
        this.metriName = metriName;
    }

    public List<Stats> parseStatistics(){
        List<Stats> result = new LinkedList<Stats>();
        String type;
        if (this.wsStatistic != null && (this.wsStatistic.getName() != null || metriName != null)) {
            type = Utils.getWSStatisticType(this.wsStatistic);
            if ("CountStatistic".equals(type)) {
                ParserCountStatistic<WSCountStatistic> parserCountStatistic = new ParserCountStatistic(this.pmiStatsType, this.wsStatistic, this.node, this.prefix, this.metriName);
                result.addAll(parserCountStatistic.getStatistic());
            } else if ("DoubleStatistic".equals(type)) {
                ParserDoubleStatistic<WSDoubleStatistic> parserDoubleStatistic = new ParserDoubleStatistic<WSDoubleStatistic>(this.pmiStatsType, this.wsStatistic, this.node, this.prefix, this.metriName);
                result.addAll(parserDoubleStatistic.getStatistic());
            } else if ("AverageStatistic".equals(type)) {
                ParserAverageStatistic<WSAverageStatistic> parserAverageStatistic = new ParserAverageStatistic<WSAverageStatistic>(this.pmiStatsType, this.wsStatistic, this.node, this.prefix, this.metriName);
                result.addAll(parserAverageStatistic.getStatistic());
            } else if ("TimeStatistic".equals(type)) {
                ParserTimeStatistic<WSTimeStatistic> parserTimeStatistic = new ParserTimeStatistic<WSTimeStatistic>(this.pmiStatsType, this.wsStatistic, this.node, this.prefix, this.metriName);
                result.addAll(parserTimeStatistic.getStatistic());
            } else if ("BoundaryStatistic".equals(type)) {
                ParserBoundaryStatistic<WSBoundaryStatistic> parserBoundaryStatistic = new ParserBoundaryStatistic<WSBoundaryStatistic>(this.pmiStatsType, this.wsStatistic, this.node, this.prefix, this.metriName);
                result.addAll(parserBoundaryStatistic.getStatistic());
            } else if ("RangeStatistic".equals(type)) {
                ParserRangeStatistic<WSRangeStatistic> parserRangeStatistic = new ParserRangeStatistic<WSRangeStatistic>(this.pmiStatsType, this.wsStatistic, this.node, this.prefix, this.metriName);
                result.addAll(parserRangeStatistic.getStatistic());
            } else if ("BoundedRangeStatistic".equals(type)) {
                ParserBoundedRangeStatistic<WSBoundedRangeStatistic> parserBoundedRangeStatistic = new ParserBoundedRangeStatistic<WSBoundedRangeStatistic>(this.pmiStatsType, this.wsStatistic, this.node, this.prefix, this.metriName);
                result.addAll(parserBoundedRangeStatistic.getStatistic());
            }
        } else if(this.wsStatistic != null){
            //l4j
        }
        return result;
    }
}
