/*
 * This file is part of Dependency-Track.
 *
 * Dependency-Track is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Dependency-Track is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Dependency-Track. If not, see http://www.gnu.org/licenses/.
 */
package org.owasp.dependencytrack.persistence;

import alpine.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class parses CWEs and adds them to the database (if necessary).
 * cwec_v2.11.xml obtained from https://cwe.mitre.org/data/xml/cwec_v2.11.xml
 */
public class CweImporter {

    private static final Logger LOGGER = Logger.getLogger(CweImporter.class);
    private static final Map<Integer, String> CWE_MAPPINGS = new TreeMap<>();

    public void processCweDefinitions() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        try (QueryManager qm = new QueryManager()) {
            LOGGER.info("Syncing CWEs with datastore");

            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final InputStream is = this.getClass().getClassLoader().getResourceAsStream("nist/cwec_v2.11.xml");
            final Document doc = builder.parse(is);
            final XPathFactory xPathfactory = XPathFactory.newInstance();
            final XPath xpath = xPathfactory.newXPath();

            final XPathExpression expr1 = xpath.compile("/Weakness_Catalog/Categories/Category");
            final XPathExpression expr2 = xpath.compile("/Weakness_Catalog/Weaknesses/Weakness");
            final XPathExpression expr3 = xpath.compile("/Weakness_Catalog/Compound_Elements/Compound_Element");

            parseNodes((NodeList) expr1.evaluate(doc, XPathConstants.NODESET));
            parseNodes((NodeList) expr2.evaluate(doc, XPathConstants.NODESET));
            parseNodes((NodeList) expr3.evaluate(doc, XPathConstants.NODESET));

            for (Map.Entry<Integer, String> entry : CWE_MAPPINGS.entrySet()) {
                qm.createCweIfNotExist(entry.getKey(), entry.getValue().replaceAll("\\\\", "\\\\\\\\"));
            }
            LOGGER.info("CWE sync complete");
        }
    }

    private static void parseNodes(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            final NamedNodeMap attributes = node.getAttributes();
            final Integer id = Integer.valueOf(attributes.getNamedItem("ID").getNodeValue());
            final String desc = attributes.getNamedItem("Name").getNodeValue();
            CWE_MAPPINGS.put(id, desc);
        }
    }

}
