//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.27 at 12:07:37 PM CET 
//

package it.eng.sil.coop.webservices.blen.input.statooccupazionale;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * it.eng.sil.coop.webservices.blen.input package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _CodiceFiscale_QNAME = new QName("", "CodiceFiscale");
	private final static QName _Cognome_QNAME = new QName("", "Cognome");
	private final static QName _Nome_QNAME = new QName("", "Nome");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
	 * it.eng.sil.coop.webservices.blen.input
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link StatoOccupazionale }
	 * 
	 */
	public StatoOccupazionale createStatoOccupazionale() {
		return new StatoOccupazionale();
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
	@XmlElementDecl(namespace = "", name = "Cognome")
	public JAXBElement<String> createCognome(String value) {
		return new JAXBElement<String>(_Cognome_QNAME, String.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Nome")
	public JAXBElement<String> createNome(String value) {
		return new JAXBElement<String>(_Nome_QNAME, String.class, null, value);
	}

}
