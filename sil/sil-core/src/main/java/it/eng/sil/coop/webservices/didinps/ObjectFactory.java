//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.04.29 at 12:09:08 PM CEST 
//

package it.eng.sil.coop.webservices.didinps;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * it.eng.sil.coop.webservices.didinps package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _ListaDIDDIDTelefono_QNAME = new QName("", "Telefono");
	private final static QName _ListaDIDDIDCellulare_QNAME = new QName("", "Cellulare");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
	 * it.eng.sil.coop.webservices.didinps
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link ListaDID }
	 * 
	 */
	public ListaDID createListaDID() {
		return new ListaDID();
	}

	/**
	 * Create an instance of {@link ListaDID.DID }
	 * 
	 */
	public ListaDID.DID createListaDIDDID() {
		return new ListaDID.DID();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Telefono", scope = ListaDID.DID.class)
	public JAXBElement<String> createListaDIDDIDTelefono(String value) {
		return new JAXBElement<String>(_ListaDIDDIDTelefono_QNAME, String.class, ListaDID.DID.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Cellulare", scope = ListaDID.DID.class)
	public JAXBElement<String> createListaDIDDIDCellulare(String value) {
		return new JAXBElement<String>(_ListaDIDDIDCellulare_QNAME, String.class, ListaDID.DID.class, value);
	}

}
