
package it.eng.myportal.ws.statoadesione.get.types;

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

    private final static QName _GetStatoAdesioneYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "getStatoAdesioneYG");
    private final static QName _RispostaGetStatoAdesioneYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "risposta_getStatoAdesioneYG");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.lavoro.servizi.servizicoap.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RichiestaGetStatoAdesioneYGType }
     * 
     */
    public RichiestaGetStatoAdesioneYGType createRichiestaGetStatoAdesioneYGType() {
        return new RichiestaGetStatoAdesioneYGType();
    }

    /**
     * Create an instance of {@link RispostaGetStatoAdesioneYGType }
     * 
     */
    public RispostaGetStatoAdesioneYGType createRispostaGetStatoAdesioneYGType() {
        return new RispostaGetStatoAdesioneYGType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaGetStatoAdesioneYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "getStatoAdesioneYG")
    public JAXBElement<RichiestaGetStatoAdesioneYGType> createGetStatoAdesioneYG(RichiestaGetStatoAdesioneYGType value) {
        return new JAXBElement<RichiestaGetStatoAdesioneYGType>(_GetStatoAdesioneYG_QNAME, RichiestaGetStatoAdesioneYGType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaGetStatoAdesioneYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "risposta_getStatoAdesioneYG")
    public JAXBElement<RispostaGetStatoAdesioneYGType> createRispostaGetStatoAdesioneYG(RispostaGetStatoAdesioneYGType value) {
        return new JAXBElement<RispostaGetStatoAdesioneYGType>(_RispostaGetStatoAdesioneYG_QNAME, RispostaGetStatoAdesioneYGType.class, null, value);
    }

}
