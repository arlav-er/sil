//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.30 at 02:59:33 PM CEST 
//


package it.eng.myportal.cliclavoro.candidatura;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}DispComune" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}codprovinciadisp" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}codregionedisp" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}codstatodisp" maxOccurs="unbounded" minOccurs="0"/>
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
    "dispComune",
    "codprovinciadisp",
    "codregionedisp",
    "codstatodisp"
})
@XmlRootElement(name = "Territorio")
public class Territorio {

    @XmlElement(name = "DispComune")
    protected List<DispComune> dispComune;
    protected List<String> codprovinciadisp;
    protected List<String> codregionedisp;
    protected List<String> codstatodisp;

    /**
     * Gets the value of the dispComune property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dispComune property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDispComune().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DispComune }
     * 
     * 
     */
    public List<DispComune> getDispComune() {
        if (dispComune == null) {
            dispComune = new ArrayList<DispComune>();
        }
        return this.dispComune;
    }

    /**
     * Gets the value of the codprovinciadisp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codprovinciadisp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodprovinciadisp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCodprovinciadisp() {
        if (codprovinciadisp == null) {
            codprovinciadisp = new ArrayList<String>();
        }
        return this.codprovinciadisp;
    }

    /**
     * Gets the value of the codregionedisp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codregionedisp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodregionedisp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCodregionedisp() {
        if (codregionedisp == null) {
            codregionedisp = new ArrayList<String>();
        }
        return this.codregionedisp;
    }

    /**
     * Gets the value of the codstatodisp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codstatodisp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodstatodisp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCodstatodisp() {
        if (codstatodisp == null) {
            codstatodisp = new ArrayList<String>();
        }
        return this.codstatodisp;
    }

}
