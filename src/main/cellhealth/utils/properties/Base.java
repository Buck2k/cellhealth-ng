package cellhealth.utils.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Alberto Pascual on 8/06/15.
 */

public class Base {

    private static Base instance;

    public static String DEFAULT;
    public static String L4JPATTERN;
    public static String APPNAME;
    public static String L4JPATTERNCONFIG;
    public static String JNDIPATH;
    public static String WASSERVICE;
    public static String JMXSERVICEURL;
    public static String TRUSTSTORE;
    public static String TRUSTSTORE_VALUE;
    public static String TRUSTSTOREPASSWORD;
    public static String TRUSTSTOREPASSWORD_VALUE;
    public static String REMOTECREDENTIAL;
    public static String MONITORROLE;
    public static String MONITORROLE_PASSWORD;
    public static String SERVERMBEAN;
    public static String PERFMBEAN;
    public static String JVMMBEAN;


    private Base(){}

    public static Base propertie(){
        if(instance == null) {
            instance = new Base();
            readBaseProperties();
        }
        return instance;
    }

    private static void readBaseProperties(){
        FileInputStream fileProperties = null;
        try {
            InputStream i  = instance.getClass().getClassLoader().getResourceAsStream("base.properties");
            fileProperties = new FileInputStream("");
            Properties properties = new Properties();
            properties.load(fileProperties);

            DEFAULT = properties.getProperty("default");
            L4JPATTERN = properties.getProperty("l4jPattern");
            APPNAME = properties.getProperty("appName");
            L4JPATTERNCONFIG = properties.getProperty("l4jPatternConfig");
            JNDIPATH = properties.getProperty("jndiPath");
            WASSERVICE = properties.getProperty("WASservice");
            if(WASSERVICE != null && JNDIPATH != null) {
                JMXSERVICEURL = new StringBuilder(WASSERVICE).append(JNDIPATH).toString();
            }
            TRUSTSTORE = properties.getProperty("trustStore");
            TRUSTSTORE_VALUE = properties.getProperty("trustStoreValue");
            TRUSTSTOREPASSWORD = properties.getProperty("trustStorePassword");
            TRUSTSTOREPASSWORD_VALUE = properties.getProperty("trustStorePasswordValue");
            REMOTECREDENTIAL = properties.getProperty("remoteCredential");
            MONITORROLE = properties.getProperty("monitorRole");
            MONITORROLE_PASSWORD = properties.getProperty("monitorRolePassword");
            SERVERMBEAN = properties.getProperty("serverMBean");
            PERFMBEAN = properties.getProperty("perfMBean");
            JVMMBEAN = properties.getProperty("jvmMBean");

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
