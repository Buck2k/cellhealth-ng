package cellhealth.core;

import cellhealth.core.connection.WASConnectionSOAP;
import cellhealth.core.test.TestListBeans;
import cellhealth.core.test.TestMetrics;
import cellhealth.core.threads.Metrics.ThreadManager;
import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.xml.ReadMetricXml;

import java.util.Scanner;

/**
 * Created by Alberto Pascual on 10/06/15.
 */

public class cellhealth {

    //-b bean list
    //-o operation list
    // -a attribute list
    // -l
    // -tb

    public static void main(String[] args) throws Exception {
        boolean error = false;
        if (args.length == 1) {
            if ("-l".equals(args[0])) {
                showListOfMetrics();
            } else if ("-b".equals(args[0])) {
                launchListBean(1);
            } else if ("-o".equals(args[0])) {
                launchListBean(2);
            } else if ("-a".equals(args[0])) {
                launchListBean(3);
            } else if ("-t".equals(args[0])) {
                launchTest();
            } else if ("-tb".equals(args[0])) {
                launchTestBeansList();
            }else {
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
    public static void showListOfMetrics() {
        L4j.getL4j().info("Starting CellHealth - List metrics");
        ListMetrics listMetrics = new ListMetrics(new WASConnectionSOAP());
        listMetrics.list();
    }

    public static void launchListBean(int option){
        Scanner scanner = new Scanner(System.in);
        InfoBeans infoBeans = new InfoBeans(new WASConnectionSOAP());

        System.out.print("List results based on the query (*:* default): ");
        String query = scanner.nextLine();
        if(query != null && query.length() > 0) {
            infoBeans.setQuery(query);
        }
        if(option == 1) {
            infoBeans.listBean();
        } else if(option == 2){
            infoBeans.listOperationsBean();
        } else if(option == 3) {
            infoBeans.listAttributesBean();
        }
    }

    public static void launchTest() {
        TestMetrics listMetrics = new TestMetrics(new WASConnectionSOAP());
        listMetrics.test();
    }

    public static void launchTestBeansList() {
        TestListBeans listBeans = new TestListBeans(new WASConnectionSOAP());
        listBeans.list();
    }


}