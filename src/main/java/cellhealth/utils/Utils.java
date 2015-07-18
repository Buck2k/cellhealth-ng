package cellhealth.utils;

import com.ibm.websphere.pmi.stat.WSStatistic;

/**
 * Created by alberto on 1/07/15.
 */
public class Utils {
    public static String getWSStatisticType(WSStatistic wsstatistic) {
        String chain = wsstatistic.toString().replace(" ", "");
        String[] blocs = chain.split("\\,");

        for(String type: chain.split("\\,")){
            String[] splitType = type.split("=");
            if("type".equals(splitType[0])) {
                return splitType[1];
            }
        }
        return "N/A";
    }
    public static String getHostByNode(String node){
        return node.replace("Node", "");
    }
}
