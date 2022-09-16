package com.engiweb.framework.tags;

import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ParseOPTIONbySAX extends DefaultHandler {
	private static String xmlStr = "";
	private String descrizione = "";
	private String codice = "";
	// private boolean takeDescr = false;
	private boolean inOptionSelected = false;

	/*
	 * public ParseOPTIONbySAX() { super(); }
	 */
	public ParseOPTIONbySAX(String newXmlStr) {
		super();
		xmlStr = newXmlStr;
	}

	public static void parse() throws Exception {
		parse(xmlStr);
	}

	public static ParseOPTIONbySAX parse(String xmlString) throws Exception {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		ParseOPTIONbySAX handler = new ParseOPTIONbySAX(xmlString);
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		try {
			xr.parse(new InputSource(new StringReader(xmlString)));
			return handler;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// //////////////////////////////////////////////////////////////////
	// Event handlers.
	// //////////////////////////////////////////////////////////////////
	/*
	 * public void startDocument() { System.out.println("Start document"); }
	 * 
	 * public void endDocument() { System.out.println("End document"); }
	 */
	public void startElement(String uri, String name, String qName, Attributes atts) {
		boolean findSel = false;
		// System.out.print("<" + qName + ">");

		if (qName.equalsIgnoreCase("OPTION")) {
			for (int i = 0; i < atts.getLength() && !findSel; i++) {
				if (atts.getLocalName(i).equalsIgnoreCase("selected")) {
					for (int j = 0; j < atts.getLength() && !findSel; j++)
						if (atts.getLocalName(j).equalsIgnoreCase("value")) {
							findSel = true;
							inOptionSelected = true;
							codice = atts.getValue(j);
						}
				}
			} // for
		} // if

	} // startElement

	public void endElement(String uri, String name, String qName) {
		if (qName.equalsIgnoreCase("OPTION")) {
			if (inOptionSelected) {
				inOptionSelected = false;
				// System.out.print("E\' stato selezionato CODICE: " + codice+"
				// -> DESCRIZIONE: " + descrizione);
			}
		}
		// System.out.println("</" + qName + ">");
		// System.out.print("\n");

	} // endElement

	public void characters(char[] ch, int start, int length) {
		if (inOptionSelected) {
			String testo = new String(ch, start, length);
			// System.out.println(" testo: " + testo);
			descrizione += testo;
		}
	} // characters

	public String getCodiceSel() {
		return this.codice;
	}

	public String getDescrSel() {
		return this.descrizione;
	}

} // end class
