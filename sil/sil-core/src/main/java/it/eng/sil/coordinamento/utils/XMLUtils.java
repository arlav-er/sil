/*
 * Created on May 24, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coordinamento.utils;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author savino
 */
public class XMLUtils {

//	public static String toXMLString(Element nodo) throws Exception {
//		StringWriter stringOut = null;
//		try {
//			OutputFormat format = new OutputFormat(nodo.getOwnerDocument());
//			format.setOmitXMLDeclaration(false);
//			// format.setEncoding("utf-8");
//			format.setEncoding("UTF-8");
//			format.setIndenting(true);
// TODO?
//			format.setIndent(2);
// TODO?
//			format.setLineWidth(0);
//			// format.setLineSeparator("");
// TODO?
//			format.setPreserveEmptyAttributes(true);
//
//			stringOut = new StringWriter();
//			XMLSerializer serial = new XMLSerializer(stringOut, format);
//
//			serial.serialize(nodo);
//		} // try
//		catch (Exception ex) {
//			// ex.printStackTrace();
//			throw ex;
//		}
//		return stringOut.toString();
//	}
	
	
	public static String toXMLString(Element nodo) throws Exception {
		StringWriter stringOut = new StringWriter();
		
	    try {
	       DOMSource domSource = new DOMSource(nodo.getOwnerDocument());
	       StreamResult result = new StreamResult(stringOut);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	       transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	       	       
	       transformer.transform(domSource, result);
	    }
	    catch(TransformerException ex) {
	       throw ex;
	    }
	    return stringOut.toString().trim();		
	}

	public static String getNodeValue(Node n) {
		NodeList nl = n.getChildNodes();
		Node node = null;
		String nodeValue = null;
		for (int i = 0; i < nl.getLength(); i++) {
			node = nl.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				break;
		}
		if (node != null)
			nodeValue = node.getNodeValue();
		return nodeValue;
	}

}
