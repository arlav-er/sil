//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.11 at 04:28:25 PM CET 
//

package it.eng.sil.pojo.yg.utenteYG;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
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

	private final static QName _IdentificativoSap_QNAME = new QName("", "IdentificativoSap");
	private final static QName _Regione_QNAME = new QName("", "Regione");
	private final static QName _Sezione0_QNAME = new QName("", "sezione0");
	private final static QName _Dataadesione_QNAME = new QName("", "dataadesione");
	private final static QName _CodiceFiscale_QNAME = new QName("", "CodiceFiscale");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
	 * generated
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Utente }
	 * 
	 */
	public Utente createUtente() {
		return new Utente();
	}

	/**
	 * Create an instance of {@link UtenteygType }
	 * 
	 */
	public UtenteygType createUtenteygType() {
		return new UtenteygType();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "IdentificativoSap")
	public JAXBElement<String> createIdentificativoSap(String value) {
		return new JAXBElement<String>(_IdentificativoSap_QNAME, String.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Regione")
	public JAXBElement<Integer> createRegione(Integer value) {
		return new JAXBElement<Integer>(_Regione_QNAME, Integer.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link UtenteygType }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "sezione0")
	public JAXBElement<UtenteygType> createSezione0(UtenteygType value) {
		return new JAXBElement<UtenteygType>(_Sezione0_QNAME, UtenteygType.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "dataadesione")
	public JAXBElement<XMLGregorianCalendar> createDataadesione(XMLGregorianCalendar value) {
		return new JAXBElement<XMLGregorianCalendar>(_Dataadesione_QNAME, XMLGregorianCalendar.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "CodiceFiscale")
	public JAXBElement<String> createCodiceFiscale(String value) {
		return new JAXBElement<String>(_CodiceFiscale_QNAME, String.class, null, value);
	}

}
