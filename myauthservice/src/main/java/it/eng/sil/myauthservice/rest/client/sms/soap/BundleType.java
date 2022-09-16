
package it.eng.sil.myauthservice.rest.client.sms.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per Bundle_type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Bundle_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bundleId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="serviceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="creditoResiduo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="creditoIniziale" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="dataAttivazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bundle_type", propOrder = {
    "bundleId",
    "serviceCode",
    "creditoResiduo",
    "creditoIniziale",
    "dataAttivazione"
})
public class BundleType {

    protected int bundleId;
    protected String serviceCode;
    protected Integer creditoResiduo;
    protected Integer creditoIniziale;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataAttivazione;

    /**
     * Recupera il valore della proprietà bundleId.
     * 
     */
    public int getBundleId() {
        return bundleId;
    }

    /**
     * Imposta il valore della proprietà bundleId.
     * 
     */
    public void setBundleId(int value) {
        this.bundleId = value;
    }

    /**
     * Recupera il valore della proprietà serviceCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceCode() {
        return serviceCode;
    }

    /**
     * Imposta il valore della proprietà serviceCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceCode(String value) {
        this.serviceCode = value;
    }

    /**
     * Recupera il valore della proprietà creditoResiduo.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCreditoResiduo() {
        return creditoResiduo;
    }

    /**
     * Imposta il valore della proprietà creditoResiduo.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCreditoResiduo(Integer value) {
        this.creditoResiduo = value;
    }

    /**
     * Recupera il valore della proprietà creditoIniziale.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCreditoIniziale() {
        return creditoIniziale;
    }

    /**
     * Imposta il valore della proprietà creditoIniziale.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCreditoIniziale(Integer value) {
        this.creditoIniziale = value;
    }

    /**
     * Recupera il valore della proprietà dataAttivazione.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAttivazione() {
        return dataAttivazione;
    }

    /**
     * Imposta il valore della proprietà dataAttivazione.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAttivazione(XMLGregorianCalendar value) {
        this.dataAttivazione = value;
    }

}
