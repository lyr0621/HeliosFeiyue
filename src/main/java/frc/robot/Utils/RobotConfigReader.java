package frc.robot.Utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

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
     * the hashmap that stores all the configs
     */
    private Map<String, Map> robotConfigs= new HashMap(1);

    /** the configurations to tune, in the form of configDomain/configName */
    private final List<String> configsToTune = new ArrayList(1);

    private File xmlFile;
    private Document doc;
    XPathFactory xPathFactory;
    XPath xPath;

    public RobotConfigReader() throws Exception {
        /* read xml file from filesystem */
        xmlFile = new File(HOME_DIR + "deploy/robotConfig.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        /* add xpath finder */
        xPathFactory = XPathFactory.newInstance();
        xPath = xPathFactory.newXPath();

        /* Get all configuration elements (e.g., hardwareConfig, chassisConfig) */
        XPathExpression expr = xPath.compile("/robotConfig/*");
        NodeList configDomains = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < configDomains.getLength(); i++) {
            Node configNode = configDomains.item(i);
            String domainName = configNode.getNodeName();
            readDomain(domainName);
        }
    }

    private void readDomain(String domainName) throws XPathExpressionException {
        /* read the configurations for hardware */
        XPathExpression expr = xPath.compile("/robotConfig/" + domainName + "/*");
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        Map domainConfigs = null;
        String domainType = "null";
        for (int i = 0; i < nodes.getLength(); i++) {
            String constantName = nodes.item(i).getNodeName();
            String currentType = readConstant(domainName, constantName, domainConfigs);
            if (currentType.equals("null")) // if the current constant is not read
                continue;
            if (domainType.equals("null"))
                domainType = currentType;
            if (! domainType.equals(currentType))
                throw new IllegalArgumentException("configs inside one domain should have the same type");
        }

        robotConfigs.put(domainName, domainConfigs);
    }

    /**
     * read a specific constant from the xml file
     * @param domainName the name of the domain that the constant belongs to
     * @param constantName the name of that constant
     * @param domainConfigs the current map of the configurations inside the domain that this constant belongs to
     * @return the type of the constant read
     *  */
    private String readConstant(String domainName, String constantName, Map domainConfigs) throws XPathExpressionException {
        /* only reads double and int, for boolean, just do int and then do param != 0 to judge true or false */

        // XPathExpression expr = xPath.compile("/robotConfig/hardware/" + constantName + "/text()");
        XPathExpression expr = xPath.compile("/robotConfig/" + domainName + "/" + constantName);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

        if (node == null) {
            System.out.println("warning, constant: " + constantName + " not found in xml file, skipping");
            return "null";
        }
        /* gets the type */
        String type = node.getAttributes().getNamedItem("type").getNodeValue();
        System.out.println("constant name:" + constantName + "has type:" + type);
        switch (type) {
            case "int" : {
                if (domainConfigs == null)
                    domainConfigs = new HashMap<String, Integer>();
                domainConfigs.put(constantName, Integer.parseInt(node.getTextContent()));
                break;
            }
            case "double" : {
                if (domainConfigs == null)
                    domainConfigs = new HashMap<String, Double>();
                domainConfigs.put(constantName, Double.parseDouble(node.getTextContent()));
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown type of robot config");
            }
        }
        System.out.println("reading " + domainName + " constant: " + constantName + ", value: " + node.getTextContent());
        return type;
    }

    /**
     * gets the configuration in a given path
     * @param configPath in domainName/constantName
     * @return the value of the constant
     */
    public Object getConfig(String configPath) {
        String domainName = configPath.split("/")[0];
        String constantName = configPath.split("/")[1];
        return getConfig(domainName, constantName);
    }

    /**
     * gets the configuration in a given path
     * @param domainName the name of the domain that the constant belongs to
     * @param constantName the name of the constant
     * @return the value of the constant
     */
    public Object getConfig(String domainName, String constantName) {
        return robotConfigs.get(domainName).get(constantName);
    }

    private void startTuningConfig(String configPath) {
        String domainName = configPath.split("/")[0];
        String constantName = configPath.split("/")[1];
        startTuningConfig(domainName, constantName);
    }

    /** start to tune a configuration on the dashboard (shuffleboard suggested) */
    private void startTuningConfig(String domainName, String constantName) {

    }
}
