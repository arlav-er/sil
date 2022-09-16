
package it.eng.sil.myauthservice.rest.client.sms.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SMSForStatusRequest_type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SMSForStatusRequest_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TrackingID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SMSMsgId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMSForStatusRequest_type", propOrder = {
    "trackingID",
    "smsMsgId"
})
public class SMSForStatusRequestType {

    @XmlElement(name = "TrackingID")
    protected String trackingID;
    @XmlElement(name = "SMSMsgId", required = true)
    protected String smsMsgId;

    /**
     * Recupera il valore della proprietà trackingID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrackingID() {
        return trackingID;
    }

    /**
     * Imposta il valore della proprietà trackingID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrackingID(String value) {
        this.trackingID = value;
    }

    /**
     * Recupera il valore della proprietà smsMsgId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSMSMsgId() {
        return smsMsgId;
    }

    /**
     * Imposta il valore della proprietà smsMsgId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSMSMsgId(String value) {
        this.smsMsgId = value;
    }

}
