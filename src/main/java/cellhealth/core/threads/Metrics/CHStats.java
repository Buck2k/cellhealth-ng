package cellhealth.core.threads.Metrics;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alberto Pascual on 23/07/15.
 */
public class CHStats {

    private List<String> stats;
    private String pathChStats;
    private int metrics;
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public CHStats(){
        this.stats = new LinkedList<String>();
    }

    public List<String> getStats() {
        return stats;
    }

    public int getMetrics() {
        return metrics;
    }

    public void count(int count){
        this.metrics = this.metrics + count;
    }

    public void add(String stat){
        this.stats.add(stat);
    }

    public String getPathChStats() {
        return pathChStats;
    }

    public void setPathChStats(String pathChStats) {
        this.pathChStats = pathChStats;
    }
}
