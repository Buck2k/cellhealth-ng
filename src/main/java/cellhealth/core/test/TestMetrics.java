package cellhealth.core.test;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;
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

    public TestMetrics(WASConnection wasConnection) {
        this.mbeansManager = new MBeansManager(wasConnection);
    }
    public void test(){
        ObjectName objectName = mbeansManager.getMBean("WebSphere:*,process=server1,type=JVM");
        try
        {
            MBeanInfo mb = mbeansManager.getClient().getMBeanInfo(objectName);
            System.out.println("Operaciones");
            for(MBeanOperationInfo op: mb.getOperations()){
                System.out.println(op.toString() + "\n");

            }
            System.out.println("\n\nAtributos");
            for(MBeanAttributeInfo at: mb.getAttributes()){
                System.out.println(at.toString() + "\n");
            }

            //Object o = mbeansManager.getClient().getAttribute(objectName, "stats");
        }
        catch (Exception e)
        {
            System.out.println("Excepción al invocar launchProcess: " + e);
        }

    }
}
