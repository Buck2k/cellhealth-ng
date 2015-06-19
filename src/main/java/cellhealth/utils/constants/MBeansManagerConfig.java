package cellhealth.utils.constants;

/**
 * Created by alberto on 18/06/15.
 */
public class MBeansManagerConfig {

    public static final String QUERY_SERVER = "WebSphere:*,type=Server";
    public static final String QUERY_PERF = "WebSphere:*,type=Perf";
    public static final String QUERY_ADD_PROCCES = ",process=";
    public static final String MBEANSTATDESCRIPTOR = "com.ibm.websphere.pmi.stat.MBeanStatDescriptor";
    public static final String JAVA_BOOLEAN = "java.lang.Boolean";
    public static final String STATS_OBJECT = "getStatsObject";
    public static final String QUERY_NODEAGENT = "WebSphere:type=NodeAgent,node=";
    public static final String NODEAGENT = "nodeagent";
}
