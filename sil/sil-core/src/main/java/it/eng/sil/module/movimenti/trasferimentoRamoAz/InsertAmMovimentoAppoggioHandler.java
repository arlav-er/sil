/*
 * Created on 29-ott-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.movimenti.trasferimentoRamoAz;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author mancinid
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 * 
 * @deprecated
 */
public class InsertAmMovimentoAppoggioHandler extends DefaultHandler {

	ArrayList uniSilFields = new ArrayList();

	public void startDocument() {
		// System.out.println("Inizio documento ... ");
	}

	public void endDocument() {
		// System.out.println("Fine documento ... ");
	}

	public void characters(char[] ch, int start, int len) {
		// String str = new String(ch,start,len);
		// System.out.println("characters: " + str);
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		// System.out.println("Inizio elemento ___ " + qName);
		if (qName.equalsIgnoreCase("COLUMN")) {
			for (int i = 0; i < atts.getLength(); i++) {
				// System.out.println("Attributo ___ " + atts.getQName(i) + " valore ___ " + atts.getValue(i));
				if (atts.getQName(i).equalsIgnoreCase("DATA")) {
					uniSilFields.add(atts.getValue(i));
				}
			}
		}
	}

	public void endElement(String uri, String localName, String qName) {
		// System.out.println("Fine elemento " + qName);
	}

	/**
	 * @return
	 */
	public ArrayList getUniSilFields() {
		return uniSilFields;
	}

	/**
	 * @param list
	 */
	public void setUniSilFields(ArrayList list) {
		uniSilFields = list;
	}

}
