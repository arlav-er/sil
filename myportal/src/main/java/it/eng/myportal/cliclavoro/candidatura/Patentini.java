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
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idpatentinosil" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idpatentino" minOccurs="0"/>
 *         &lt;element name="notepatentini" type="{http://servizi.lavoro.gov.it/candidatura}Stringa1000" minOccurs="0"/>
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
    "idpatentinosil",
    "idpatentino",
    "notepatentini"
})
@XmlRootElement(name = "Patentini")
public class Patentini {

    protected String idpatentinosil;
    protected String idpatentino;
    protected String notepatentini;

    /**
     * Gets the value of the idpatentinosil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdpatentinosil() {
        return idpatentinosil;
    }

    /**
     * Sets the value of the idpatentinosil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdpatentinosil(String value) {
        this.idpatentinosil = value;
    }

    /**
     * Gets the value of the idpatentino property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdpatentino() {
        return idpatentino;
    }

    /**
     * Sets the value of the idpatentino property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdpatentino(String value) {
        this.idpatentino = value;
    }

    /**
     * Gets the value of the notepatentini property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotepatentini() {
        return notepatentini;
    }

    /**
     * Sets the value of the notepatentini property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotepatentini(String value) {
        this.notepatentini = value;
    }

}
