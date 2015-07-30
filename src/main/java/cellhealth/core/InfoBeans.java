package cellhealth.core;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSStats;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Alberto Pascual on 24/07/15.
 */
public class InfoBeans {
    private MBeansManager mbeansManager;
    private Scanner scanner;
    private final String prefix;
    private File file;
    private String query;

    public InfoBeans(WASConnection wasConnection) {
        this.mbeansManager = new MBeansManager(wasConnection);
        this.scanner = new Scanner(System.in);
        this.query = "*:*";
        this.prefix = "logs/";
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void listBean(){
        this.startFile("listbeans.info");
        for(ObjectName objectName: mbeansManager.getMBeans(this.query)){
            print("############## BEAN ##############\n");
            print("name: " + objectName.getKeyProperty("name") + "\n");
            print("cell: " + objectName.getKeyProperty("cell") + "\n");
            print("node: " + objectName.getKeyProperty("node") + "\n");
            print("process: " + objectName.getKeyProperty("process") + "\n");
            print("ObjectName: " + objectName + "\n");
            print("##################################\n\n");

        }
    }

    public void listOperationsBean(){
        this.startFile("listOperationBeans.info");
        for(ObjectName objectName: mbeansManager.getMBeans(this.query)){
            print("############## BEAN ##############\n");
            print("ObjectName: " + objectName + "\n");
            print("Operations");
            MBeanInfo mbeanInfo = getBean(objectName);
            for(MBeanOperationInfo mbeanOperationInfo: mbeanInfo.getOperations()){
                print("\n\t Name: " + mbeanOperationInfo.getName() + "\n");
                print("\t Return type: " + mbeanOperationInfo.getReturnType() + "\n");
                print("\t Description: " + mbeanOperationInfo.getDescription() + "\n");
            }
            print("##################################\n\n");
        }
    }

    public void listAttributesBean(){
        this.startFile("listAttributeBeans.info");
        for(ObjectName objectName: mbeansManager.getMBeans(this.query)){
            print("############## BEAN ##############\n");
            print("ObjectName: " + objectName + "\n");
            print("Attributes");
            MBeanInfo mbeanInfo = getBean(objectName);
            for(MBeanAttributeInfo mbeanAttributeInfo: mbeanInfo.getAttributes()){
                print("\n\t Name: " + mbeanAttributeInfo.getName() + "\n");
                print("\t Type: " + mbeanAttributeInfo.getType() + "\n");
                print("\t Description: " + mbeanAttributeInfo.getDescription() + "\n");
            }
            print("##################################\n\n");
        }
    }

    public void listAttributesBeanTemp(){
        this.startFile("listAttributeBeans.info");
        for(ObjectName objectName: mbeansManager.getMBeans(this.query)){
            print("############## BEAN ##############\n");
            print("ObjectName = " + objectName.toString());

            MBeanInfo mbeanInfo = getBean(objectName);
            boolean haencontradostats = false;
            for(MBeanAttributeInfo mbeanAttributeInfo: mbeanInfo.getAttributes()){
                if("stats".equals(mbeanAttributeInfo.getName())){
                    try {
                        WSStats wsStats;

                            ObjectName serverPerfMBean = mbeansManager.getMBean("WebSphere:type=Perf,node=" + objectName.getKeyProperty("node") + ",process=" + objectName.getKeyProperty("process") + ",*");
                            String[] signature = new String[]{"javax.management.ObjectName", "java.lang.Boolean"};
                            Object[] params = new Object[]{objectName, false};
                            if(serverPerfMBean == null){
                                print("Can't construct perf bean\n");
                            } else {
                                wsStats = (WSStats) mbeansManager.getClient().invoke(serverPerfMBean, "getStatsObject", params, signature);
                                if (wsStats != null) {
                                    print(" Stats = " + wsStats.getStatsType() + "\n");
                                } else {
                                    print(" wstats = null \n");
                                }
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    haencontradostats = true;
                }
            }
            if(!haencontradostats){
                print(" PMItype = null\n");
            }
            print("##################################\n\n");
        }
    }

    public MBeanInfo getBean(ObjectName objectName){
        MBeanInfo mbeanInfo = null;
        try {
            mbeanInfo = mbeansManager.getClient().getMBeanInfo(objectName);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (ConnectorException e) {
            e.printStackTrace();
        }
        return mbeanInfo;
    }

    public void startFile(String path){
        String pathMetricsResult = prefix + path;
        this.file = new File(pathMetricsResult);
        if(this.file.exists()){
            this.file.delete();
        }
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void print(String line) {
        System.out.print(line);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(this.file, true);
            fileWriter.write(line);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
