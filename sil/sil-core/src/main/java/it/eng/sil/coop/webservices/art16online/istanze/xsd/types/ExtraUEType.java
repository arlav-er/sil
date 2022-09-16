//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.12.17 alle 03:34:33 PM CET 
//


package it.eng.sil.coop.webservices.art16online.istanze.xsd.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per ExtraUEType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ExtraUEType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="titolosoggiorno" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="1"/>
 *             &lt;pattern value="\d{1}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="numerotitolosogg">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="15"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="motivopermesso" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;length value="5"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="scadenzatitolosogg" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtraUEType")
public class ExtraUEType {

    @XmlAttribute(name = "titolosoggiorno", required = true)
    protected String titolosoggiorno;
    @XmlAttribute(name = "numerotitolosogg")
    protected String numerotitolosogg;
    @XmlAttribute(name = "motivopermesso", required = true)
    protected String motivopermesso;
    @XmlAttribute(name = "scadenzatitolosogg", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar scadenzatitolosogg;

    /**
     * Recupera il valore della proprietà titolosoggiorno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitolosoggiorno() {
        return titolosoggiorno;
    }

    /**
     * Imposta il valore della proprietà titolosoggiorno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitolosoggiorno(String value) {
        this.titolosoggiorno = value;
    }

    /**
     * Recupera il valore della proprietà numerotitolosogg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumerotitolosogg() {
        return numerotitolosogg;
    }

    /**
     * Imposta il valore della proprietà numerotitolosogg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumerotitolosogg(String value) {
        this.numerotitolosogg = value;
    }

    /**
     * Recupera il valore della proprietà motivopermesso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivopermesso() {
        return motivopermesso;
    }

    /**
     * Imposta il valore della proprietà motivopermesso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivopermesso(String value) {
        this.motivopermesso = value;
    }

    /**
     * Recupera il valore della proprietà scadenzatitolosogg.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getScadenzatitolosogg() {
        return scadenzatitolosogg;
    }

    /**
     * Imposta il valore della proprietà scadenzatitolosogg.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setScadenzatitolosogg(XMLGregorianCalendar value) {
        this.scadenzatitolosogg = value;
    }

}
