
package it.eng.myportal.ws.youthGuarantee.types;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.lavoro.servizi.servizicoap.types package. 
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

    private final static QName _RispostaInvioUtenteYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "risposta_invioUtenteYG");
    private final static QName _InvioUtenteYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "invioUtenteYG");
    private final static QName _RispostaCheckUtenteYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "risposta_checkUtenteYG");
    private final static QName _CheckUtenteYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "checkUtenteYG");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.lavoro.servizi.servizicoap.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RichiestaCheckUtenteYGType }
     * 
     */
    public RichiestaCheckUtenteYGType createRichiestaCheckUtenteYGType() {
        return new RichiestaCheckUtenteYGType();
    }

    /**
     * Create an instance of {@link RispostaInvioUtenteYGType }
     * 
     */
    public RispostaInvioUtenteYGType createRispostaInvioUtenteYGType() {
        return new RispostaInvioUtenteYGType();
    }

    /**
     * Create an instance of {@link RichiestaInvioUtenteYGType }
     * 
     */
    public RichiestaInvioUtenteYGType createRichiestaInvioUtenteYGType() {
        return new RichiestaInvioUtenteYGType();
    }

    /**
     * Create an instance of {@link RispostaCheckUtenteYGType }
     * 
     */
    public RispostaCheckUtenteYGType createRispostaCheckUtenteYGType() {
        return new RispostaCheckUtenteYGType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaInvioUtenteYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "risposta_invioUtenteYG")
    public JAXBElement<RispostaInvioUtenteYGType> createRispostaInvioUtenteYG(RispostaInvioUtenteYGType value) {
        return new JAXBElement<RispostaInvioUtenteYGType>(_RispostaInvioUtenteYG_QNAME, RispostaInvioUtenteYGType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaInvioUtenteYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "invioUtenteYG")
    public JAXBElement<RichiestaInvioUtenteYGType> createInvioUtenteYG(RichiestaInvioUtenteYGType value) {
        return new JAXBElement<RichiestaInvioUtenteYGType>(_InvioUtenteYG_QNAME, RichiestaInvioUtenteYGType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaCheckUtenteYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "risposta_checkUtenteYG")
    public JAXBElement<RispostaCheckUtenteYGType> createRispostaCheckUtenteYG(RispostaCheckUtenteYGType value) {
        return new JAXBElement<RispostaCheckUtenteYGType>(_RispostaCheckUtenteYG_QNAME, RispostaCheckUtenteYGType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaCheckUtenteYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "checkUtenteYG")
    public JAXBElement<RichiestaCheckUtenteYGType> createCheckUtenteYG(RichiestaCheckUtenteYGType value) {
        return new JAXBElement<RichiestaCheckUtenteYGType>(_CheckUtenteYG_QNAME, RichiestaCheckUtenteYGType.class, null, value);
    }

}
