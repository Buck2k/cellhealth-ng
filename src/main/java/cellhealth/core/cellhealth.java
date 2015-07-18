package cellhealth.core;

import cellhealth.core.connection.WASConnectionSOAP;
import cellhealth.core.threads.Metrics.ThreadManager;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.xml.ReadMetricXml;
import com.ibm.websphere.management.exception.ConnectorException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

/**
 * Created by Alberto Pascual on 10/06/15.
 */

public class cellhealth {

    public static void main(String[] args) throws Exception {
        boolean error = false;
        if (args.length == 1) {
            if ("-l".equals(args[0])) {
                showListOfMetrics();
            } else if ("-t".equals(args[0])) {
                throwTest();
            } else {
                error = true;
            }
        } else if(args.length == 0) {
            L4j.getL4j().info("################################");
            L4j.getL4j().info("# WebSphere metrics collection #");
            L4j.getL4j().info("#        CellHealth-ng         #");
            L4j.getL4j().info("################################");
            L4j.getL4j().info("Try '-h' to see options");
            startCellehealth();
        } else {
            error = false;
        }
        if(error){
            L4j.getL4j().info("Option not supported");
            L4j.getL4j().info("Try '-h' to see options");
        }
    }

    public static void startCellehealth() {
        L4j.getL4j().info("Starting CellHealth - Normal mode");
        ReadMetricXml readMetricXml = new ReadMetricXml();
        ThreadManager manager = new ThreadManager(readMetricXml.getMetricGroup());
        Thread threadManager = new Thread(manager,"manager");
        threadManager.start();
    }
    public static void showListOfMetrics() throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        L4j.getL4j().info("Starting CellHealth - List metrics");
        ListMetrics listMetrics = new ListMetrics(new WASConnectionSOAP());
        listMetrics.list();
    }

    public static void throwTest() throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        TestMetrics listMetrics = new TestMetrics(new WASConnectionSOAP());
        listMetrics.test();
    }
}