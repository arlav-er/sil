
package it.eng.myportal.ws.statoadesione.notifica.types;

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

    private final static QName _NotificaCambioStatoAdesioneYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "notificaCambioStatoAdesioneYG");
    private final static QName _RispostaNotificaCambioStatoAdesioneYG_QNAME = new QName("http://servizi.lavoro.gov.it/servizicoap/types", "risposta_notificaCambioStatoAdesioneYG");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.lavoro.servizi.servizicoap.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RispostaNotificaCambioStatoAdesioneYGType }
     * 
     */
    public RispostaNotificaCambioStatoAdesioneYGType createRispostaNotificaCambioStatoAdesioneYGType() {
        return new RispostaNotificaCambioStatoAdesioneYGType();
    }

    /**
     * Create an instance of {@link RichiestaNotificaCambioStatoAdesioneYGType }
     * 
     */
    public RichiestaNotificaCambioStatoAdesioneYGType createRichiestaNotificaCambioStatoAdesioneYGType() {
        return new RichiestaNotificaCambioStatoAdesioneYGType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaNotificaCambioStatoAdesioneYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "notificaCambioStatoAdesioneYG")
    public JAXBElement<RichiestaNotificaCambioStatoAdesioneYGType> createNotificaCambioStatoAdesioneYG(RichiestaNotificaCambioStatoAdesioneYGType value) {
        return new JAXBElement<RichiestaNotificaCambioStatoAdesioneYGType>(_NotificaCambioStatoAdesioneYG_QNAME, RichiestaNotificaCambioStatoAdesioneYGType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaNotificaCambioStatoAdesioneYGType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/servizicoap/types", name = "risposta_notificaCambioStatoAdesioneYG")
    public JAXBElement<RispostaNotificaCambioStatoAdesioneYGType> createRispostaNotificaCambioStatoAdesioneYG(RispostaNotificaCambioStatoAdesioneYGType value) {
        return new JAXBElement<RispostaNotificaCambioStatoAdesioneYGType>(_RispostaNotificaCambioStatoAdesioneYG_QNAME, RispostaNotificaCambioStatoAdesioneYGType.class, null, value);
    }

}
