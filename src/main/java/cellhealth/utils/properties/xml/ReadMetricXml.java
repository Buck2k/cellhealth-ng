package cellhealth.utils.properties.xml;


import cellhealth.utils.logs.L4j;
import cellhealth.utils.properties.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alberto on 1/07/15.
 */
public class ReadMetricXml {

    private Document dom;

    public ReadMetricXml() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.dom = db.parse(Settings.getInstance().getPathConf() + "metrics.xml");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public List<MetricGroup> getMetricGroup() {
        List<MetricGroup> metricProperties = new LinkedList<MetricGroup>();
        Element root = this.dom.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if ("MetricGroup".equals(node.getNodeName())) {
                        Element element = (Element) node;
                        metricProperties.add(getMetricGroupProperties(node, element));
                    }
                }

            }
        }
        return metricProperties;
    }

    private MetricGroup getMetricGroupProperties(Node node, Element element) {
        MetricGroup metricGroup = new MetricGroup();
        metricGroup.setStatsType(getStringValue(element, "StatsType"));
        metricGroup.setAllowGlobal(getBooleanValue(element, "AllowGlobal"));
        metricGroup.setPrefix(getStringValue(element, "Prefix"));
        metricGroup.setUniqueInstance(getBooleanValue(element, "UniqueInstance"));
        metricGroup.setInstanceFilter(getInstanceFilter(element, "InstanceFilter"));
        List<Metric> metrics = getMetriGroupMetrics(node.getChildNodes());
        metricGroup.setMetrics(metrics);
        if(metricGroup.getStatsType() == null || metricGroup.getPrefix() == null) {
            L4j.getL4j().error("Metrics.xml", new NullPointerException());
        }
        return metricGroup;
    }

    private List<Metric> getMetriGroupMetrics(NodeList nodeList) {
        List<Metric> metrics = new LinkedList<Metric>();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if ("Metrics".equals(node.getNodeName())) {
                        metrics = getMetriGroupMetrics(node.getChildNodes());
                    } else if ("Metric".equals(node.getNodeName())) {
                        Metric metric = getMetricGroupMetricProperties((Element) node);
                        metrics.add(metric);
                    }
                }
            }
        }
        return metrics;
    }

    private Metric getMetricGroupMetricProperties(Element element) {
        Metric metric = new Metric();
        String id = element.getAttribute("id");
        if(id == null){
            L4j.getL4j().error("Metrics.xml", new NullPointerException());
        }
        metric.setId(Integer.parseInt(id));
        metric.setName(getStringValue(element, "Name"));
        metric.setScale(getIntValue(element, "Scale"));
        return metric;
    }

    private String getStringValue(Element element, String tagName) {
        String string = null;
        NodeList nl = element.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element aux = (Element) nl.item(0);
            string = aux.getFirstChild().getNodeValue();
        }
        return string;
    }

    private boolean getBooleanValue(Element element, String tagName) {
        String bool = "false";
        if(getStringValue(element, tagName) != null){
            bool = getStringValue(element, tagName);
        }
        return Boolean.valueOf(bool);
    }

    private int getIntValue(Element element, String tagName) {
        if(getStringValue(element, tagName) != null) {
            return Integer.parseInt(getStringValue(element, tagName));
        }
        return 0;
    }

    private List<String> getInstanceFilter(Element element, String tagName) {
        List<String> listInstances = new LinkedList<String>();
        tagName = getStringValue(element, tagName);
        if(tagName != null) {
            String aux = tagName.replace(" ", "");
            for (String split : aux.split(",")) {
                split = split.replace(",", "");
                listInstances.add(split);
            }
        }
        return listInstances;
    }
}