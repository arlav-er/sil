//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.06.05 alle 11:35:53 AM CEST 
//


package it.eng.myportal.ws.adesioneReimpiego.input;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
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

    private final static QName _DataRiferimento_QNAME = new QName("", "DataRiferimento");
    private final static QName _CodiceFiscale_QNAME = new QName("", "CodiceFiscale");
    private final static QName _Dichiarazione_QNAME = new QName("", "Dichiarazione");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Reimpiego }
     * 
     */
    public Reimpiego createReimpiego() {
        return new Reimpiego();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DataRiferimento")
    public JAXBElement<XMLGregorianCalendar> createDataRiferimento(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DataRiferimento_QNAME, XMLGregorianCalendar.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DichiarazioneCheck }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Dichiarazione")
    public JAXBElement<DichiarazioneCheck> createDichiarazione(DichiarazioneCheck value) {
        return new JAXBElement<DichiarazioneCheck>(_Dichiarazione_QNAME, DichiarazioneCheck.class, null, value);
    }

}
