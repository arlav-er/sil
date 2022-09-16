//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.13 at 03:50:22 PM CEST 
//

package it.eng.sil.coop.webservices.rinnovaPatto.input;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * generated package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _Email_QNAME = new QName("", "Email");
	private final static QName _CodiceFiscale_QNAME = new QName("", "CodiceFiscale");
	private final static QName _Cellulare_QNAME = new QName("", "Cellulare");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
	 * generated
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link RinnovoPatto }
	 * 
	 */
	public RinnovoPatto createRinnovoPatto() {
		return new RinnovoPatto();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Email")
	public JAXBElement<String> createEmail(String value) {
		return new JAXBElement<String>(_Email_QNAME, String.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "CodiceFiscale")
	public JAXBElement<String> createCodiceFiscale(String value) {
		return new JAXBElement<String>(_CodiceFiscale_QNAME, String.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Cellulare")
	public JAXBElement<String> createCellulare(String value) {
		return new JAXBElement<String>(_Cellulare_QNAME, String.class, null, value);
	}

}
