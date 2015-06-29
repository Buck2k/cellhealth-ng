package cellhealth.core.statistics;

import cellhealth.core.connection.MBeansManager;
import com.ibm.websphere.pmi.stat.WSStats;

/**
 * Created by Alberto Pascual on 25/06/15.
 */
public class MBeanStats implements Comparable<MBeanStats>{

    private String name;

    private WSStats wsStats;

    public boolean isSubStats() {
        return subStats;
    }

    public void setSubStats(boolean subStats) {
        this.subStats = subStats;
    }

    private boolean subStats;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WSStats getWsStats() {
        return wsStats;
    }

    public void setWsStats(WSStats wsStats) {
        this.wsStats = wsStats;
    }


    public int compareTo(MBeanStats o) {
        return name.compareTo(o.getName());
    }
}
