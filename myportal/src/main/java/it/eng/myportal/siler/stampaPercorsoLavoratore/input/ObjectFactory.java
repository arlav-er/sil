//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.12 at 04:37:42 PM CEST 
//


package it.eng.myportal.siler.stampaPercorsoLavoratore.input;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.eng.myportal.test package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TipologiaInformazione_QNAME = new QName("", "TipologiaInformazione");
    private final static QName _CodiceFiscale_QNAME = new QName("", "CodiceFiscale");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.eng.myportal.test
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PercorsoLavoratore }
     * 
     */
    public PercorsoLavoratore createPercorsoLavoratore() {
        return new PercorsoLavoratore();
    }

    /**
     * Create an instance of {@link PercorsoLavoratore.Stampa }
     * 
     */
    public PercorsoLavoratore.Stampa createPercorsoLavoratoreStampa() {
        return new PercorsoLavoratore.Stampa();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TipologiaInformazione")
    public JAXBElement<String> createTipologiaInformazione(String value) {
        return new JAXBElement<String>(_TipologiaInformazione_QNAME, String.class, null, value);
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