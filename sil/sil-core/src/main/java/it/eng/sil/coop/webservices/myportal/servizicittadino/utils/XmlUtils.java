package it.eng.sil.coop.webservices.myportal.servizicittadino.utils;

import org.w3c.dom.Element;

public class XmlUtils {

	public static String getStringFromSimpleXmlElement(Object inputElement) {

		if (inputElement == null) {
			return null;
		}

		if (inputElement.getClass().equals(String.class)) {
			return inputElement.toString();
		}

		try {

			Element element = (Element) inputElement;
			return element.getFirstChild().getTextContent();
		
		} catch (Exception e) {

			return null;

		}

	}

}