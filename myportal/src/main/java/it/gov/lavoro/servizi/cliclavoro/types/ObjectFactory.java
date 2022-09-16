package it.gov.lavoro.servizi.cliclavoro.types;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
     

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.lavoro.servizi.cliclavoro.types package. 
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

    private final static QName _RispostaInvioVacancy_QNAME = new QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioVacancy");
    private final static QName _RispostaInvioCandidatura_QNAME = new QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioCandidatura");
    private final static QName _InvioVacancy_QNAME = new QName("http://servizi.lavoro.gov.it/cliclavoro/types", "invioVacancy");
    private final static QName _InvioMessaggio_QNAME = new QName("http://servizi.lavoro.gov.it/cliclavoro/types", "invioMessaggio");
    private final static QName _InvioCandidatura_QNAME = new QName("http://servizi.lavoro.gov.it/cliclavoro/types", "invioCandidatura");
    private final static QName _RispostaInvioMessaggio_QNAME = new QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioMessaggio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.lavoro.servizi.cliclavoro.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RispostaInvioCandidaturaType }
     * 
     */
    public RispostaInvioCandidaturaType createRispostaInvioCandidaturaType() {
        return new RispostaInvioCandidaturaType();
    }
 
    /**
     * Create an instance of {@link RispostaInvioVacancyType }
     * 
     */
    public RispostaInvioVacancyType createRispostaInvioVacancyType() {
        return new RispostaInvioVacancyType();
    }

    /**
     * Create an instance of {@link RichiestaInvioMessaggioType }
     * 
     */
    public RichiestaInvioMessaggioType createRichiestaInvioMessaggioType() {
        return new RichiestaInvioMessaggioType();
    }

    /**
     * Create an instance of {@link RichiestaInvioVacancyType }
     * 
     */
    public RichiestaInvioVacancyType createRichiestaInvioVacancyType() {
        return new RichiestaInvioVacancyType();
    }

    /**
     * Create an instance of {@link RispostaInvioMessaggioType }
     * 
     */
    public RispostaInvioMessaggioType createRispostaInvioMessaggioType() {
        return new RispostaInvioMessaggioType();
    }

    /**
     * Create an instance of {@link RichiestaInvioCandidaturaType }
     * 
     */
    public RichiestaInvioCandidaturaType createRichiestaInvioCandidaturaType() {
        return new RichiestaInvioCandidaturaType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaInvioVacancyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/cliclavoro/types", name = "risposta_invioVacancy")
    public JAXBElement<RispostaInvioVacancyType> createRispostaInvioVacancy(RispostaInvioVacancyType value) {
        return new JAXBElement<RispostaInvioVacancyType>(_RispostaInvioVacancy_QNAME, RispostaInvioVacancyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaInvioCandidaturaType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/cliclavoro/types", name = "risposta_invioCandidatura")
    public JAXBElement<RispostaInvioCandidaturaType> createRispostaInvioCandidatura(RispostaInvioCandidaturaType value) {
        return new JAXBElement<RispostaInvioCandidaturaType>(_RispostaInvioCandidatura_QNAME, RispostaInvioCandidaturaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaInvioVacancyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/cliclavoro/types", name = "invioVacancy")
    public JAXBElement<RichiestaInvioVacancyType> createInvioVacancy(RichiestaInvioVacancyType value) {
        return new JAXBElement<RichiestaInvioVacancyType>(_InvioVacancy_QNAME, RichiestaInvioVacancyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaInvioMessaggioType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/cliclavoro/types", name = "invioMessaggio")
    public JAXBElement<RichiestaInvioMessaggioType> createInvioMessaggio(RichiestaInvioMessaggioType value) {
        return new JAXBElement<RichiestaInvioMessaggioType>(_InvioMessaggio_QNAME, RichiestaInvioMessaggioType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RichiestaInvioCandidaturaType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/cliclavoro/types", name = "invioCandidatura")
    public JAXBElement<RichiestaInvioCandidaturaType> createInvioCandidatura(RichiestaInvioCandidaturaType value) {
        return new JAXBElement<RichiestaInvioCandidaturaType>(_InvioCandidatura_QNAME, RichiestaInvioCandidaturaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RispostaInvioMessaggioType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servizi.lavoro.gov.it/cliclavoro/types", name = "risposta_invioMessaggio")
    public JAXBElement<RispostaInvioMessaggioType> createRispostaInvioMessaggio(RispostaInvioMessaggioType value) {
        return new JAXBElement<RispostaInvioMessaggioType>(_RispostaInvioMessaggio_QNAME, RispostaInvioMessaggioType.class, null, value);
    }

}
