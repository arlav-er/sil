package com.engiweb.framework.util;

import java.io.FileReader;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.configuration.ConfigSingleton;

public abstract class XMLUtil {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(XMLUtil.class.getName());
	public static final String XML_MAPPINGS = "XML_MAPPINGS.MAPPING";
	public static final String REPLACING = "REPLACING";
	public static final String REPLACED = "REPLACED";
	public static final String XML_HEADER_PREFIX = "<?xml";
	public static final String XML_HEADER_DEFAULT_VERSION = "1.0";
	public static final String XML_HEADER_DEFAULT_ENCODING = "utf-8";
	public static final String XML_HEADER_DEFAULT_DOCTYPE = "/WEB-INF/conf/xhtml-lat1.ent";
	private static final int _xmlIdentationSpaces = 2;
	private static boolean _mappingsRetrieved = false;
	private static int _mappingsNumber = 0;
	private static Vector _replacingMappings = null;
	private static Vector _replacedMappings = null;
	private static String _doctypeDefinition = null;

	public static String getIndentation(int level) {
		StringBuffer indentationLevel = new StringBuffer();
		for (int i = 0; i < _xmlIdentationSpaces; i++)
			indentationLevel.append(" ");
		StringBuffer indentation = new StringBuffer();
		for (int i = 0; i < level; i++)
			indentation.append(indentationLevel);
		return indentation.toString();
	} // public static String getIndentation(int level)

	public static String normalizeAttribute(String attribute) {
		if (attribute == null)
			return null;
		char[] attributeArray = attribute.toCharArray();
		StringBuffer normalizedAttribute = new StringBuffer();
		for (int i = 0; i < attributeArray.length; i++) {
			char c = attributeArray[i];
			switch (c) {
			case '<': {
				normalizedAttribute.append("&lt;");
				break;
			} // case '<'
			case '>': {
				normalizedAttribute.append("&gt;");
				break;
			}
			case '&': {
				normalizedAttribute.append("&amp;");
				break;
			}
			case '"': {
				normalizedAttribute.append("&quot;");
				break;
			}

			/*
			 * case '\r': case '\n': { normalizedAttribute.append("&#");
			 * normalizedAttribute.append(Integer.toString(c)); normalizedAttribute.append(';'); break; }
			 */

			default: {
				normalizedAttribute.append(c);
			}
			} // switch (c)
		} // for (int i = 0; i < attributeArray.length; i++)
		return normalizedAttribute.toString();
	} // public static String normalizeAttribute(String attribute)

	private static String parseAttribute(String toParse, String replacing, String replaced) {
		if (toParse == null)
			return null;
		if ((replacing == null) || (replaced == null))
			return toParse;
		StringBuffer parsed = new StringBuffer();
		int parameterIndex = toParse.indexOf(replacing);
		while (parameterIndex != -1) {
			parsed.append(toParse.substring(0, parameterIndex));
			parsed.append(replaced);
			toParse = toParse.substring(parameterIndex + replacing.length(), toParse.length());
			parameterIndex = toParse.indexOf(replacing);
		} // while (parameterIndex != -1)
		parsed.append(toParse);
		return parsed.toString();
	} // private static String parseAttribute(String toParse, String

	// replacing, String replaced)
	public static String parseAttribute(String attribute) {
		if (attribute == null)
			return null;
		String parsed = attribute;
		if (!_mappingsRetrieved) {
			synchronized (com.engiweb.framework.util.XMLUtil.class) {
				if (!_mappingsRetrieved) {
					_mappingsRetrieved = true;
					ConfigSingleton configure = ConfigSingleton.getInstance();
					Vector mappings = configure.getAttributeAsVector(XML_MAPPINGS);
					if (mappings == null) {
						_mappingsNumber = 0;
						return parsed;
					} // if (mappings == null)
					_mappingsNumber = mappings.size();
					_replacingMappings = new Vector();
					_replacedMappings = new Vector();
					for (int i = 0; i < _mappingsNumber; i++) {
						SourceBean mapping = (SourceBean) mappings.elementAt(i);
						_replacingMappings.addElement(mapping.getAttribute(REPLACING));
						_replacedMappings.addElement(mapping.getAttribute(REPLACED));
					} // for (int i = 0; i < _mappingsNumber; i++)
				} // if (!_mappingsRetrieved)
			} // synchronized(AbstractXMLObject.class)
		} // if (!_mappingsRetrieved)
		for (int i = 0; i < _mappingsNumber; i++)
			parsed = parseAttribute(parsed, (String) _replacingMappings.elementAt(i),
					(String) _replacedMappings.elementAt(i));
		return parsed;
	} // public static String parseAttribute(String attribute)

	public static String getDoctypeFilename() {
		String rootPath = ConfigSingleton.getRootPath();
		if (rootPath == null) {
			_logger.debug("XMLUtil::getDoctype: rootPath non valido");

			rootPath = "";
		} // if (rootPath == null)
		String doctypeFilename = (String) ConfigSingleton.getInstance().getAttribute("COMMON.XHTML_LAT1_ENT");
		if (doctypeFilename == null) {
			_logger.fatal("XMLUtil::getDoctype: doctypeFilename non valido");

			doctypeFilename = XML_HEADER_DEFAULT_DOCTYPE;
		} // if (doctypeFilename == null)
		return rootPath + doctypeFilename;
	} // public static String getDoctypeFilename()

	public static String getDoctypeDefinition() {
		if (_doctypeDefinition == null)
			synchronized (XMLUtil.class) {
				if (_doctypeDefinition == null) {
					StringBuffer doctypeDefinitionBuffer = new StringBuffer();
					try {
						FileReader inDoctypeFile = new FileReader(getDoctypeFilename());
						int ch = 0;
						while ((ch = inDoctypeFile.read()) != -1)
							doctypeDefinitionBuffer.append((char) ch);
					} // try
					catch (Exception ex) {
						it.eng.sil.util.TraceWrapper.fatal(_logger, "XMLUtil::getDoctypeDefinition: ", ex);

					} // catch (Exception ex)
					_doctypeDefinition = doctypeDefinitionBuffer.toString();
				} // if (_doctypeDefinition == null)
			} // synchronized(XMLUtil.class)
		return _doctypeDefinition;
	} // public static String getDoctypeDefinition()

	public static String toXML(XMLObject xmlObject, boolean inlineEntity) {
		return toXML(xmlObject, inlineEntity, true);
	} // public static String toXML(XMLObject xmlObject, boolean inlineEntity)

	// Restituisce l'XML completo di intestazione
	public static String toXML(XMLObject xmlObject, boolean inlineEntity, boolean indent) {
		return toXML(xmlObject, inlineEntity, indent, false);
	}

	/**
	 * Restituisce l'XML completo di intestazione se il parametro omitDeclaration è false, altrimenti restituisce
	 * l'XML senza intestazione se è true
	 * 
	 * @author roccetti
	 */
	public static String toXML(XMLObject xmlObject, boolean inlineEntity, boolean indent, boolean omitDeclaration) {

		Document document = xmlObject.toDocument();
		StringWriter stringOut = new StringWriter();

		try {
			DOMSource domSource = new DOMSource(document);
			StreamResult result = new StreamResult(stringOut);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitDeclaration ? "yes" : "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, XML_HEADER_DEFAULT_ENCODING);
			transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");

			transformer.transform(domSource, result);
		} catch (TransformerException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "XMLUtil::toXML: ", ex);
		}
		return stringOut.toString().trim();

	} // public static String toXML(XMLObject xmlObject, boolean inlineEntity)

	public static Document toDocument(XMLObject xmlObject) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			document.appendChild(xmlObject.toElement(document));

		} catch (ParserConfigurationException e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "XMLUtil::toDocument: ", e);

		}

		// DocumentImpl document = new DocumentImpl();
		// document.appendChild(xmlObject.toElement(document));
		// DocumentTypeImpl dtd = new DocumentTypeImpl(document, "dtd");
		// dtd.setInternalSubset(getDoctypeDefinition());
		// document.appendChild(dtd);

		return document;

	} // public static Document toDocument(XMLObject xmlObject)
} // public class AbstractXMLObject
