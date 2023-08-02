package frc.robot.Utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class RobotConfigReader {
    /** user configuration */
    private static final String HOME_DIR = "/home/lvuser/";

    /**
     * the hashmap that stores all the configs for hardware, like motor
     * ports
     */
    public final Map<String, Integer> hardwareConfigs = new HashMap<String, Integer>();

    /**
     * the hashmap that stores all the configs for chassis, like motor speed and PID
     * coefficients
     */
    public final Map<String, Double> chassisConfigs = new HashMap<String, Double>();

    /**
     * the hashmap that stores all the config for controlling, like the binding of
     * buttons axis
     */
    public final Map<String, Integer> controlConfigs = new HashMap<String, Integer>();

    public RobotConfigReader() throws Exception {
        /* read xml file from filesystem */
        File xmlFile = new File(HOME_DIR + "deploy/robotConfig.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        /* add xpath finder */
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        /* read the configurations for hardware */
        XPathExpression expr = xPath.compile("/robotConfig/hardware/*");
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            String constantName = nodes.item(i).getNodeName();
            expr = xPath.compile("/robotConfig/hardware/" + constantName + "/text()");
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node == null) {
                System.out.println("warning, constant: " + constantName + " not found in xml file, skipping");
                continue;
            }
            System.out.println("reading hardware constant: " + constantName + " with value: " + node.getNodeValue());
            hardwareConfigs.put(constantName, Integer.parseInt(node.getNodeValue()));
        }

        /* read the configurations for chassis */
        expr = xPath.compile("/robotConfig/chassis/*");
        nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            String constantName = nodes.item(i).getNodeName();
            expr = xPath.compile("/robotConfig/chassis/" + constantName + "/text()");
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node == null) {
                System.out.println("warning, chassis constant: " + constantName + " not found in xml file, skipping");
                continue;
            }
            System.out.println("reading chassis constant: " + constantName + " with value: " + node.getNodeValue());
            chassisConfigs.put(constantName, Double.parseDouble(node.getNodeValue()));
        }

        /* read the configurations for controlling */
        expr = xPath.compile("/robotConfig/control/*");
        nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            String constantName = nodes.item(i).getNodeName();
            expr = xPath.compile("/robotConfig/control/" + constantName + "/text()");
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node == null) {
                System.out
                        .println("warning, controlling constant: " + constantName + " not found in xml file, skipping");
                continue;
            }
            System.out.println("reading chassis constant: " + constantName + " with value: " + node.getNodeValue());
            controlConfigs.put(constantName, Integer.parseInt(node.getNodeValue()));
        }
    }
}
