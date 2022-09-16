//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.12.17 alle 03:34:33 PM CET 
//


package it.eng.sil.coop.webservices.art16online.istanze.xsd.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Esito" type="{}EsitoType"/>
 *         &lt;element name="IstanzaArt16" type="{}IstanzaArt16Type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "esito",
    "istanzaArt16"
})
@XmlRootElement(name = "ResponseIstanzaArt16")
public class ResponseIstanzaArt16 {

    @XmlElement(name = "Esito", required = true)
    protected EsitoType esito;
    @XmlElement(name = "IstanzaArt16")
    protected IstanzaArt16Type istanzaArt16;

    /**
     * Recupera il valore della proprietà esito.
     * 
     * @return
     *     possible object is
     *     {@link EsitoType }
     *     
     */
    public EsitoType getEsito() {
        return esito;
    }

    /**
     * Imposta il valore della proprietà esito.
     * 
     * @param value
     *     allowed object is
     *     {@link EsitoType }
     *     
     */
    public void setEsito(EsitoType value) {
        this.esito = value;
    }

    /**
     * Recupera il valore della proprietà istanzaArt16.
     * 
     * @return
     *     possible object is
     *     {@link IstanzaArt16Type }
     *     
     */
    public IstanzaArt16Type getIstanzaArt16() {
        return istanzaArt16;
    }

    /**
     * Imposta il valore della proprietà istanzaArt16.
     * 
     * @param value
     *     allowed object is
     *     {@link IstanzaArt16Type }
     *     
     */
    public void setIstanzaArt16(IstanzaArt16Type value) {
        this.istanzaArt16 = value;
    }

}
