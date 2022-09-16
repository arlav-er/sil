
package it.eng.myportal.ws.pattoonline;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.eng.myportal.ws.pattoonline package. 
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

    private final static QName _InvioPatto_QNAME = new QName("http://pattoonline.ws.myportal.eng.it/", "InvioPatto");
    private final static QName _InvioPattoResponse_QNAME = new QName("http://pattoonline.ws.myportal.eng.it/", "InvioPattoResponse");
    private final static QName _RichiestaPatto_QNAME = new QName("http://pattoonline.ws.myportal.eng.it/", "RichiestaPatto");
    private final static QName _RichiestaPattoResponse_QNAME = new QName("http://pattoonline.ws.myportal.eng.it/", "RichiestaPattoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.eng.myportal.ws.pattoonline
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AccettazionePattoType }
     * 
     */
    public AccettazionePattoType createAccettazionePattoType() {
        return new AccettazionePattoType();
    }

    /**
     * Create an instance of {@link InvioPatto }
     * 
     */
    public InvioPatto createInvioPatto() {
        return new InvioPatto();
    }

    /**
     * Create an instance of {@link EsitoType }
     * 
     */
    public EsitoType createEsitoType() {
        return new EsitoType();
    }

    /**
     * Create an instance of {@link RichiestaPatto }
     * 
     */
    public RichiestaPatto createRichiestaPatto() {
        return new RichiestaPatto();
    }

    /**
     * Create an instance of {@link ResponsePatto }
     * 
     */
    public ResponsePatto createResponsePatto() {
        return new ResponsePatto();
    }

    /**
     * Create an instance of {@link PattoType }
     * 
     */
    public PattoType createPattoType() {
        return new PattoType();
    }

    /**
     * Create an instance of {@link PattoPortaleType }
     * 
     */
    public PattoPortaleType createPattoPortaleType() {
        return new PattoPortaleType();
    }

    /**
     * Create an instance of {@link PattoAccettatoType }
     * 
     */
    public PattoAccettatoType createPattoAccettatoType() {
        return new PattoAccettatoType();
    }

    /**
     * Create an instance of {@link CodiceFiscaleType }
     * 
     */
    public CodiceFiscaleType createCodiceFiscaleType() {
        return new CodiceFiscaleType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvioPatto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pattoonline.ws.myportal.eng.it/", name = "InvioPatto")
    public JAXBElement<InvioPatto> createInvioPatto(InvioPatto value) {
        return new JAXBElement<InvioPatto>(_InvioPatto_QNAME, InvioPatto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EsitoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pattoonline.ws.myportal.eng.it/", name = "InvioPattoResponse")
    public JAXBElement<EsitoType> createInvioPattoResponse(EsitoType value) {
        return new JAXBElement<EsitoType>(_InvioPattoResponse_QNAME, EsitoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaPatto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pattoonline.ws.myportal.eng.it/", name = "RichiestaPatto")
    public JAXBElement<RichiestaPatto> createRichiestaPatto(RichiestaPatto value) {
        return new JAXBElement<RichiestaPatto>(_RichiestaPatto_QNAME, RichiestaPatto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponsePatto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pattoonline.ws.myportal.eng.it/", name = "RichiestaPattoResponse")
    public JAXBElement<ResponsePatto> createRichiestaPattoResponse(ResponsePatto value) {
        return new JAXBElement<ResponsePatto>(_RichiestaPattoResponse_QNAME, ResponsePatto.class, null, value);
    }

}
