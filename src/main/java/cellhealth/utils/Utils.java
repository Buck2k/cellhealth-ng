package cellhealth.utils;

import cellhealth.core.connection.MBeansManager;
import cellhealth.utils.constants.Constants;
import cellhealth.utils.logs.L4j;
import com.ibm.websphere.pmi.stat.WSStatistic;

import javax.management.ObjectName;
import java.util.Set;

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
        String[] nodeSplit = node.split("Node");
        return nodeSplit[0];
    }

    public static void showInstances(MBeansManager mbeansManager){
        Set<ObjectName> runtimes = mbeansManager.getAllServerRuntimes();
        for(ObjectName serverRuntime: runtimes){
            String serverName = serverRuntime.getKeyProperty(Constants.NAME);
            String node = serverRuntime.getKeyProperty(Constants.NODE);
            String showServerHost = (( node==null ) || ( node.length() == 0 ))?"<NOT SET IN CONFIG>":node;
            L4j.getL4j().info("SERVER :" + serverName + " NODE: " + showServerHost);
        }
    }
}
