//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.30 at 02:59:33 PM CEST 
//


package it.eng.myportal.cliclavoro.candidatura;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idpatenteguidasil" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idpatenteguida" minOccurs="0"/>
 *         &lt;element name="notepatenti" type="{http://servizi.lavoro.gov.it/candidatura}Stringa1000" minOccurs="0"/>
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
    "idpatenteguidasil",
    "idpatenteguida",
    "notepatenti"
})
@XmlRootElement(name = "Patenti")
public class Patenti {

    protected String idpatenteguidasil;
    protected String idpatenteguida;
    protected String notepatenti;

    /**
     * Gets the value of the idpatenteguidasil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdpatenteguidasil() {
        return idpatenteguidasil;
    }

    /**
     * Sets the value of the idpatenteguidasil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdpatenteguidasil(String value) {
        this.idpatenteguidasil = value;
    }

    /**
     * Gets the value of the idpatenteguida property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdpatenteguida() {
        return idpatenteguida;
    }

    /**
     * Sets the value of the idpatenteguida property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdpatenteguida(String value) {
        this.idpatenteguida = value;
    }

    /**
     * Gets the value of the notepatenti property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotepatenti() {
        return notepatenti;
    }

    /**
     * Sets the value of the notepatenti property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotepatenti(String value) {
        this.notepatenti = value;
    }

}
