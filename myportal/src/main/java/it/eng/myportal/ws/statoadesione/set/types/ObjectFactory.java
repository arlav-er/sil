
package it.eng.myportal.ws.statoadesione.set.types;

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

    private final static QName _SetStatoAdesioneYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "SetStatoAdesioneYG");
    private final static QName _RispostaSetStatoAdesioneYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "risposta_SetStatoAdesioneYG");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.lavoro.servizi.servizicoap.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RichiestaSetStatoAdesioneYGType }
     * 
     */
    public RichiestaSetStatoAdesioneYGType createRichiestaSetStatoAdesioneYGType() {
        return new RichiestaSetStatoAdesioneYGType();
    }

    /**
     * Create an instance of {@link RispostaSetStatoAdesioneYGType }
     * 
     */
    public RispostaSetStatoAdesioneYGType createRispostaSetStatoAdesioneYGType() {
        return new RispostaSetStatoAdesioneYGType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaSetStatoAdesioneYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "SetStatoAdesioneYG")
    public JAXBElement<RichiestaSetStatoAdesioneYGType> createSetStatoAdesioneYG(RichiestaSetStatoAdesioneYGType value) {
        return new JAXBElement<RichiestaSetStatoAdesioneYGType>(_SetStatoAdesioneYG_QNAME, RichiestaSetStatoAdesioneYGType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaSetStatoAdesioneYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "risposta_SetStatoAdesioneYG")
    public JAXBElement<RispostaSetStatoAdesioneYGType> createRispostaSetStatoAdesioneYG(RispostaSetStatoAdesioneYGType value) {
        return new JAXBElement<RispostaSetStatoAdesioneYGType>(_RispostaSetStatoAdesioneYG_QNAME, RispostaSetStatoAdesioneYGType.class, null, value);
    }

}
