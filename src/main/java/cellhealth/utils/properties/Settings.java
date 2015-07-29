package cellhealth.utils.properties;

import cellhealth.utils.constants.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Alberto Pascual on 8/06/15.
 */

public class Settings {

    private static Settings instance;

    private String hostWebsphere;
    private String portWebsphere;
    private String l4jPattern;
    private String l4jPatternConfig;
    private String appName;
    private String l4jName;
    private String l4jGetLogger;
    private String pathConf;
    private String pathSenderConf;

    private Settings(){}

    public static synchronized Settings propertie(){
        if(instance == null) {
            instance = new Settings();
            readBaseProperties();
            readL4jProperties();
        }
        return instance;
    }

    private static void readBaseProperties(){
        FileInputStream fileProperties;
        try {
            fileProperties = new FileInputStream(Constants.PATH_CELLHEALT_PROPERTIES);
            Properties confProperties = new Properties();
            confProperties.load(fileProperties);
            instance.setHostWebsphere(confProperties.getProperty("host_websphere"));
            instance.setPortWebsphere(confProperties.getProperty("port_soap_websphere"));
            instance.setPathConf(Constants.PATH_DIR_PROPERTIES);
            instance.setPathSenderConf(Constants.PATH_DIR_PROPERTIES + confProperties.getProperty("sender_properties"));
            fileProperties.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readL4jProperties(){
        FileInputStream fileProperties;
        try {
            fileProperties = new FileInputStream(Constants.PATH_L4J_PROPERTIES);
            Properties confProperties = new Properties();
            confProperties.load(fileProperties);
            instance.setAppName(confProperties.getProperty("appName"));
            instance.setL4jPattern(confProperties.getProperty("l4jPattern"));
            instance.setL4jPatternConfig(confProperties.getProperty("l4jPatternConfig:"));
            instance.setL4jName(confProperties.getProperty("l4jName"));
            instance.setL4jGetLogger(confProperties.getProperty("l4jgetLogger"));
            fileProperties.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHostWebsphere() {
        return hostWebsphere;
    }

    public void setHostWebsphere(String hostWebsphere) {
        this.hostWebsphere = hostWebsphere;
    }

    public static Settings getInstance() {
        return instance;
    }

    public static void setInstance(Settings instance) {
        Settings.instance = instance;
    }

    public String getL4jPattern() {
        return l4jPattern;
    }

    public void setL4jPattern(String l4jPattern) {
        this.l4jPattern = l4jPattern;
    }

    public String getL4jPatternConfig() {
        return l4jPatternConfig;
    }

    public void setL4jPatternConfig(String l4jPatternConfig) {
        this.l4jPatternConfig = l4jPatternConfig;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getL4jName() {
        return l4jName;
    }

    public void setL4jName(String l4jName) {
        this.l4jName = l4jName;
    }

    public String getL4jGetLogger() {
        return l4jGetLogger;
    }

    public void setL4jGetLogger(String l4jGetLogger) {
        this.l4jGetLogger = l4jGetLogger;
    }

    public String getPathConf() {
        return pathConf;
    }

    public void setPathConf(String pathConf) {
        this.pathConf = pathConf;
    }

    public String getPortWebsphere() {
        return portWebsphere;
    }

    public void setPortWebsphere(String portWebsphere) {
        this.portWebsphere = portWebsphere;
    }

    public String getPathSenderConf() {
        return pathSenderConf;
    }

    public void setPathSenderConf(String pathSenderConf) {
        this.pathSenderConf = pathSenderConf;
    }
}