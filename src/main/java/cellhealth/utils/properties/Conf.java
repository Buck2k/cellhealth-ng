package cellhealth.utils.properties;

import cellhealth.utils.constants.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Alberto Pascual on 8/06/15.
 */

public class Conf {

    private static Conf instance;

    public static String HOST_WEBSPHERE;
    public static String PORT_WEBSPHERE;
    public static String L4JPATTERN;
    public static String L4JPATTERNCONFIG;
    public static String APPNAME;
    public static String L4JNAME;
    public static String L4JGETLOGGER;


    private Conf(){}

    public static Conf propertie(){
        if(instance == null) {
            instance = new Conf();
            readBaseProperties();
        }
        return instance;
    }

    private static void readBaseProperties(){
        FileInputStream fileProperties = null;
        try {
            System.out.println(Constants.PATH_CELLHEALT_PROPERTIES);
            fileProperties = new FileInputStream(Constants.PATH_CELLHEALT_PROPERTIES);
            Properties confProperties = new Properties();
            confProperties.load(fileProperties);

            APPNAME = confProperties.getProperty("appName");

            HOST_WEBSPHERE = confProperties.getProperty("host_websphere");
            PORT_WEBSPHERE = confProperties.getProperty("port_soap_websphere");

            L4JPATTERN = confProperties.getProperty("l4jPattern");
            L4JPATTERNCONFIG = confProperties.getProperty("l4jPatternConfig:");
            L4JNAME = confProperties.getProperty("l4jName");
            L4JGETLOGGER = confProperties.getProperty("l4jgetLogger");


            fileProperties.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileProperties.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
