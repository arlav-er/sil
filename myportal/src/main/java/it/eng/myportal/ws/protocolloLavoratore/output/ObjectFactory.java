//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.09.14 alle 02:47:25 PM CEST 
//


package it.eng.myportal.ws.protocolloLavoratore.output;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.test.eng package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.test.eng
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetProtocollo }
     * 
     */
    public GetProtocollo createGetProtocollo() {
        return new GetProtocollo();
    }

    /**
     * Create an instance of {@link GetProtocollo.Esito }
     * 
     */
    public GetProtocollo.Esito createGetProtocolloEsito() {
        return new GetProtocollo.Esito();
    }

    /**
     * Create an instance of {@link GetProtocollo.Protocollo }
     * 
     */
    public GetProtocollo.Protocollo createGetProtocolloProtocollo() {
        return new GetProtocollo.Protocollo();
    }

}
