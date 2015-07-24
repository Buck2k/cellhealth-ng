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
    private String l4jpattern;
    private String l4jpatternconfig;
    private String appname;
    private String l4jname;
    private String l4jgetlogger;

    private Settings(){}

    public static synchronized Settings propertie(){
        if(instance == null) {
            instance = new Settings();
            readBaseProperties();
        }
        return instance;
    }

    private static void readBaseProperties(){
        FileInputStream fileProperties;
        try {
            fileProperties = new FileInputStream(Constants.PATH_CELLHEALT_PROPERTIES);
            Properties confProperties = new Properties();
            confProperties.load(fileProperties);

            instance.setAppname(confProperties.getProperty("appName"));

            instance.setHostWebsphere(confProperties.getProperty("host_websphere"));
            instance.setPortWebsphere(confProperties.getProperty("port_soap_websphere"));

            instance.setL4jpattern(confProperties.getProperty("l4jPattern"));
            instance.setL4jpatternconfig(confProperties.getProperty("l4jPatternConfig:"));
            instance.setL4jname(confProperties.getProperty("l4jName"));
            instance.setL4jgetlogger(confProperties.getProperty("l4jgetLogger"));

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

    public String getL4jgetlogger() {
        return l4jgetlogger;
    }

    public void setL4jgetlogger(String l4jgetlogger) {
        this.l4jgetlogger = l4jgetlogger;
    }

    public String getL4jname() {
        return l4jname;
    }

    public void setL4jname(String l4jname) {
        this.l4jname = l4jname;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getL4jpatternconfig() {
        return l4jpatternconfig;
    }

    public void setL4jpatternconfig(String l4jpatternconfig) {
        this.l4jpatternconfig = l4jpatternconfig;
    }

    public String getL4jpattern() {
        return l4jpattern;
    }

    public void setL4jpattern(String l4jpattern) {
        this.l4jpattern = l4jpattern;
    }

    public String getPortWebsphere() {
        return portWebsphere;
    }

    public void setPortWebsphere(String portWebsphere) {
        this.portWebsphere = portWebsphere;
    }
}
