//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.05.13 alle 03:31:52 PM CEST 
//


package it.eng.myportal.ws.adesioneReimpiego.output;

import javax.xml.bind.annotation.XmlRegistry;


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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Risposta }
     * 
     */
    public Risposta createRisposta() {
        return new Risposta();
    }

    /**
     * Create an instance of {@link Risposta.DatiStatoOccupazionale }
     * 
     */
    public Risposta.DatiStatoOccupazionale createRispostaDatiStatoOccupazionale() {
        return new Risposta.DatiStatoOccupazionale();
    }

    /**
     * Create an instance of {@link Risposta.Esito }
     * 
     */
    public Risposta.Esito createRispostaEsito() {
        return new Risposta.Esito();
    }

    /**
     * Create an instance of {@link Risposta.DatiStatoOccupazionale.StatoOccupazionale }
     * 
     */
    public Risposta.DatiStatoOccupazionale.StatoOccupazionale createRispostaDatiStatoOccupazionaleStatoOccupazionale() {
        return new Risposta.DatiStatoOccupazionale.StatoOccupazionale();
    }

    /**
     * Create an instance of {@link Risposta.DatiStatoOccupazionale.DatiCPI }
     * 
     */
    public Risposta.DatiStatoOccupazionale.DatiCPI createRispostaDatiStatoOccupazionaleDatiCPI() {
        return new Risposta.DatiStatoOccupazionale.DatiCPI();
    }

}
