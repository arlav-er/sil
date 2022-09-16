//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.06.05 alle 11:35:53 AM CEST 
//


package it.eng.myportal.ws.adesioneReimpiego.input;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element ref="{}CodiceFiscale"/>
 *         &lt;element ref="{}DataRiferimento"/>
 *         &lt;element ref="{}Dichiarazione"/>
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
    "codiceFiscale",
    "dataRiferimento",
    "dichiarazione"
})
@XmlRootElement(name = "Reimpiego")
public class Reimpiego {

    @XmlElement(name = "CodiceFiscale", required = true)
    protected String codiceFiscale;
    @XmlElement(name = "DataRiferimento", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataRiferimento;
    @XmlElement(name = "Dichiarazione", required = true)
    @XmlSchemaType(name = "string")
    protected DichiarazioneCheck dichiarazione;

    /**
     * Recupera il valore della propriet� codiceFiscale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Imposta il valore della propriet� codiceFiscale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceFiscale(String value) {
        this.codiceFiscale = value;
    }

    /**
     * Recupera il valore della propriet� dataRiferimento.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRiferimento() {
        return dataRiferimento;
    }

    /**
     * Imposta il valore della propriet� dataRiferimento.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRiferimento(XMLGregorianCalendar value) {
        this.dataRiferimento = value;
    }

    /**
     * Recupera il valore della propriet� dichiarazione.
     * 
     * @return
     *     possible object is
     *     {@link DichiarazioneCheck }
     *     
     */
    public DichiarazioneCheck getDichiarazione() {
        return dichiarazione;
    }

    /**
     * Imposta il valore della propriet� dichiarazione.
     * 
     * @param value
     *     allowed object is
     *     {@link DichiarazioneCheck }
     *     
     */
    public void setDichiarazione(DichiarazioneCheck value) {
        this.dichiarazione = value;
    }

}
