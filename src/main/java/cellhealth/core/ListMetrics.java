package cellhealth.core;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import cellhealth.core.statistics.MBeanStats;
import cellhealth.utils.Utils;
import cellhealth.utils.logs.L4j;
import com.ibm.websphere.management.exception.ConnectorException;

import com.ibm.websphere.pmi.stat.WSStatistic;
import com.ibm.websphere.pmi.stat.WSStats;


import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Alberto Pascual on 23/06/15.
 */
public class ListMetrics {

    private MBeansManager mbeansManager;

    public ListMetrics(WASConnection wasConnection) throws ConnectorException, MalformedObjectNameException {
        this.mbeansManager = new MBeansManager(wasConnection);
    }

    public void list() throws ConnectorException, MalformedObjectNameException, MBeanException, ReflectionException, InstanceNotFoundException {
        ObjectName serverMBean = this.mbeansManager.getMBean("WebSphere:type=Server,processType=ManagedProcess,*");
        ObjectName serverPerfMBean = mbeansManager
                .getMBean(new StringBuilder("WebSphere:type=Perf,node=")
                        .append(serverMBean.getKeyProperty("node"))
                        .append(",process=")
                        .append(serverMBean.getKeyProperty("name"))
                        .append(",*").toString());
        Set<MBeanStats> typeBeans = new TreeSet<MBeanStats>();
        L4j.getL4j().info(new StringBuilder("Getting the list of names and possible metric of the node: ").append(serverMBean.getKeyProperty("node")).append(", instance: ").append(serverMBean.getKeyProperty("name")).toString());
        try {
            for(ObjectName objectName: mbeansManager.getMBeans("WebSphere:*")){

                String[] signature = new String[] {"javax.management.ObjectName","java.lang.Boolean"};
                Object[] params = new Object[] {objectName, new Boolean(true)};
                WSStats wsStats = (WSStats) mbeansManager.getClient().invoke(serverPerfMBean, "getStatsObject", params, signature);
                if(wsStats != null) {
                    MBeanStats mbeanStats = new MBeanStats();
                    mbeanStats.setName(wsStats.getName());
                    mbeanStats.setSubStats((wsStats.getSubStats().length > 0));
                    mbeanStats.setWsStats(wsStats);
                    typeBeans.add(mbeanStats);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n\n");
        System.out.println("List of MBeans (Statistics)");
        System.out.println("###########################\n\n");
        System.out.println();
        //this.mostrarStats(typeBeans);
    }

    private void mostrarStats(Set<MBeanStats> typeBeans){
        for(MBeanStats mbeanStat: typeBeans){
            if(mbeanStat.isSubStats()){
                System.out.println("\n-> " + mbeanStat.getName() + " have " + mbeanStat.getWsStats().getSubStats().length + " SubStats");
                for(WSStatistic wsStatistic: mbeanStat.getWsStats().getStatistics()){
                    System.out.println("\t ID:" + wsStatistic.getId() + " name:" + wsStatistic.getName() + " unity: " + wsStatistic.getUnit() + " type:" + Utils.getWSStatisticType(wsStatistic));
                    System.out.println("\t Description:" + wsStatistic.getDescription());
                }
                Set<MBeanStats> typeBeanSub = new TreeSet<MBeanStats>();
                for(WSStats substats:mbeanStat.getWsStats().getSubStats()) {
                    MBeanStats mbeanStats = new MBeanStats();
                    mbeanStats.setName(substats.getName());
                    mbeanStats.setSubStats((substats.getSubStats().length > 0));
                    mbeanStats.setWsStats(substats);
                    typeBeanSub.add(mbeanStats);
                }

                this.mostrarStats(typeBeanSub);
            } else {
                System.out.println("\n-> " + mbeanStat.getName());
                for(WSStatistic wsStatistic: mbeanStat.getWsStats().getStatistics()){
                    System.out.println("\t ID:" + wsStatistic.getId() + " name:" + wsStatistic.getName() + " unity: " + wsStatistic.getUnit() + " type:" + Utils.getWSStatisticType(wsStatistic));
                    System.out.println("\t Description:" + wsStatistic.getDescription());
                }
            }
        }
    }


}
