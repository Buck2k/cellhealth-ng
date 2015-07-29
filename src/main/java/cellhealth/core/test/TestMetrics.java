package cellhealth.core.test;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import cellhealth.utils.properties.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Alberto Pascual on 7/07/15.
 */
public class TestMetrics {

    private MBeansManager mbeansManager;
    protected static final int DEFAULT_CONTENT_LINE_LEN = 100;
    private final DateFormat secondDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static final char SEPARATOR_CHAR = ',';

    /**
     * The comma separator used in the CSV files to separate different
     * property values
     */
    public static final String SEPARATOR = "" + SEPARATOR_CHAR;
    protected static final long BYTES_IN_MEGABYTE = 1024 * 1024;

    public void test() {
        System.out.println(Settings.propertie().getHostWebsphere());
        System.out.println(Settings.propertie().getPortWebsphere());

//        try {
//        WSStats wsStats = null;
//        ObjectName serverPerfMBean = mbeansManager.getMBean("WebSphere:type=Perf,node=wastestNode01,process=nodeagent,*");
//        ObjectName objectName = mbeansManager.getMBean("WebSphere:name=nodeagent,node=wastestNode01,process=nodeagent,*");
//        if (objectName != null) {
//            String[] signature = new String[]{"javax.management.ObjectName", "java.lang.Boolean"};
//            Object[] params = new Object[]{objectName, true};
//
//                wsStats = (WSStats) mbeansManager.getClient().invoke(serverPerfMBean, "getStatsObject", params, signature);
//
//        }
//        System.out.println(objectName);
//        MBeanAttributeInfo[] mBeanAttributeInfo = new MBeanAttributeInfo[0];
//        try {
//            mBeanAttributeInfo = mbeansManager.getClient().getMBeanInfo(objectName).getAttributes();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        for(MBeanAttributeInfo mb: mBeanAttributeInfo){
//            System.out.println("####################" + mb.getName() + " #######" + mb.getType());
//            System.out.println(mbeansManager.getClient().getAttribute(objectName, mb.getName()) + "\n");
//        }
//        } catch (Exception e) {
//            L4j.getL4j().error("Capturer ", e);
//        }
    }

    public TestMetrics(WASConnection wasConnection) {
        this.mbeansManager = new MBeansManager(wasConnection);
    }
}
