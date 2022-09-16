
package it.eng.myportal.ws.pattoonline;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per PattoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PattoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CodiceFiscale" type="{http://pattoonline.ws.myportal.eng.it/}CodiceFiscaleType"/&gt;
 *         &lt;element name="DataPatto" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="CodServiziAmministrativi"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NumProtocollo"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AnnoProtocollo" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="CodProvinciaProv"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PattoType", propOrder = {
    "codiceFiscale",
    "dataPatto",
    "codServiziAmministrativi",
    "numProtocollo",
    "annoProtocollo",
    "codProvinciaProv"
})
public class PattoType {

    @XmlElement(name = "CodiceFiscale", required = true)
    @XmlSchemaType(name = "string")
    protected CodiceFiscaleType codiceFiscale;
    @XmlElement(name = "DataPatto", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataPatto;
    @XmlElement(name = "CodServiziAmministrativi", required = true)
    protected String codServiziAmministrativi;
    @XmlElement(name = "NumProtocollo", required = true)
    protected String numProtocollo;
    @XmlElement(name = "AnnoProtocollo", required = true)
    protected BigInteger annoProtocollo;
    @XmlElement(name = "CodProvinciaProv", required = true)
    protected String codProvinciaProv;

    /**
     * Recupera il valore della proprieta codiceFiscale.
     * 
     * @return
     *     possible object is
     *     {@link CodiceFiscaleType }
     *     
     */
    public CodiceFiscaleType getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Imposta il valore della proprieta codiceFiscale.
     * 
     * @param value
     *     allowed object is
     *     {@link CodiceFiscaleType }
     *     
     */
    public void setCodiceFiscale(CodiceFiscaleType value) {
        this.codiceFiscale = value;
    }

    /**
     * Recupera il valore della proprieta dataPatto.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataPatto() {
        return dataPatto;
    }

    /**
     * Imposta il valore della proprieta dataPatto.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataPatto(XMLGregorianCalendar value) {
        this.dataPatto = value;
    }

    /**
     * Recupera il valore della proprieta codServiziAmministrativi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodServiziAmministrativi() {
        return codServiziAmministrativi;
    }

    /**
     * Imposta il valore della proprieta codServiziAmministrativi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodServiziAmministrativi(String value) {
        this.codServiziAmministrativi = value;
    }

    /**
     * Recupera il valore della proprieta numProtocollo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumProtocollo() {
        return numProtocollo;
    }

    /**
     * Imposta il valore della proprieta numProtocollo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumProtocollo(String value) {
        this.numProtocollo = value;
    }

    /**
     * Recupera il valore della proprieta annoProtocollo.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAnnoProtocollo() {
        return annoProtocollo;
    }

    /**
     * Imposta il valore della proprieta annoProtocollo.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAnnoProtocollo(BigInteger value) {
        this.annoProtocollo = value;
    }

    /**
     * Recupera il valore della proprieta codProvinciaProv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodProvinciaProv() {
        return codProvinciaProv;
    }

    /**
     * Imposta il valore della proprieta codProvinciaProv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodProvinciaProv(String value) {
        this.codProvinciaProv = value;
    }

}
