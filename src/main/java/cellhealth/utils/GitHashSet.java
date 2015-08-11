package cellhealth.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Alberto Pascual on 11/08/15.
 */
public class GitHashSet {

    public static void main(String[] args) throws IOException, TransformerException {
        Process process = Runtime.getRuntime().exec("git rev-parse HEAD");
        InputStream inputstream = process.getInputStream();
        BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream);
        byte[] contents = new byte[1024];
        int bytesRead=0;
        String commitHash = null;
        while ((bytesRead = bufferedinputstream.read(contents)) != -1) {
            String s = new String(contents, 0, bytesRead);
            commitHash = s.substring(0,9);
        }

        Document dom = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("pom.xml");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Element root = dom.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if("properties".equals(node.getNodeName())){
                    NodeList propertiesList = node.getChildNodes();
                    if (propertiesList != null && propertiesList.getLength() > 0) {
                        for (int y = 0; y < propertiesList.getLength(); y++) {
                            Node nodeProperties = propertiesList.item(y);
                            if("commit.hash".equals(nodeProperties.getNodeName())){
                                nodeProperties.setTextContent(commitHash);
                            }
                        }
                    }
                }

            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(dom);
        StreamResult result = new StreamResult(new File("pom.xml"));
        transformer.transform(source, result);
    }
}


