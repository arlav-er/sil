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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per IstanzaType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="IstanzaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datacandidatura" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="annoprotocollo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="protocollo">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="30"/>
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="idistanza">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="200"/>
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IstanzaType", propOrder = {
    "datacandidatura",
    "annoprotocollo",
    "protocollo",
    "idistanza"
})
public class IstanzaType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar datacandidatura;
    protected int annoprotocollo;
    @XmlElement(required = true)
    protected String protocollo;
    @XmlElement(required = true)
    protected String idistanza;

    /**
     * Recupera il valore della proprietà datacandidatura.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatacandidatura() {
        return datacandidatura;
    }

    /**
     * Imposta il valore della proprietà datacandidatura.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatacandidatura(XMLGregorianCalendar value) {
        this.datacandidatura = value;
    }

    /**
     * Recupera il valore della proprietà annoprotocollo.
     * 
     */
    public int getAnnoprotocollo() {
        return annoprotocollo;
    }

    /**
     * Imposta il valore della proprietà annoprotocollo.
     * 
     */
    public void setAnnoprotocollo(int value) {
        this.annoprotocollo = value;
    }

    /**
     * Recupera il valore della proprietà protocollo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocollo() {
        return protocollo;
    }

    /**
     * Imposta il valore della proprietà protocollo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocollo(String value) {
        this.protocollo = value;
    }

    /**
     * Recupera il valore della proprietà idistanza.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdistanza() {
        return idistanza;
    }

    /**
     * Imposta il valore della proprietà idistanza.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdistanza(String value) {
        this.idistanza = value;
    }

}
