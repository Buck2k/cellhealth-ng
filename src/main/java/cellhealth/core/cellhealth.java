package cellhealth.core;

import cellhealth.core.connection.WASConnection;
import cellhealth.core.connection.WASConnectionSOAP;
import cellhealth.core.threads.InstanceManager;
import cellhealth.core.threads.Metrics.ThreadManager;
import cellhealth.exception.CellhealthConnectionException;
import cellhealth.utils.logs.L4j;
import com.ibm.websphere.management.exception.ConnectorException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by Alberto Pascual on 10/06/15.
 */

public class cellhealth {

    public static void main(String[] args) throws Exception {
        boolean start = true;
        for(String arg: args){
            if("-l".equals(arg)){
                start = false;
                showListOfMetrics();
            }
            if("-t".equals(arg)){
                start = false;
                throwTest();
            }
        }
        if(start){
            startCellehealth();
        }
    }

    public static void startCellehealth() throws MalformedObjectNameException, ConnectorException, ReflectionException, AttributeNotFoundException, MBeanException, InstanceNotFoundException, CellhealthConnectionException {

        L4j.getL4j().info("Iniciando el proceso cellhealth - para la recopilación de métricas de WebSphere");

        WASConnectionSOAP connection = new WASConnectionSOAP();
        ThreadManager manager = new ThreadManager(connection);
        Thread threadManager = new Thread(manager,"manager");
        threadManager.start();
        //StartCellhealth startCellhealth = new StartCellhealth(new WASConnectionSOAP());
        //Thread instanceManager = new Thread(new InstanceManager(), "instanceManager");

        //instanceManager.start();

    }
    public static void showListOfMetrics() throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        ListMetrics listMetrics = new ListMetrics(new WASConnectionSOAP());
        listMetrics.list();
    }

    public static void throwTest() throws ConnectorException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        TestMetrics listMetrics = new TestMetrics(new WASConnectionSOAP());
        listMetrics.test();
    }
}
