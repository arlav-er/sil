
package it.eng.sil.myauthservice.rest.client.sms.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SMS_type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SMS_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SMS" type="{http://www.infotn.it/SMS-EAI/InvioSMS_types}SMSMessage_type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMS_type", propOrder = {
    "sender",
    "sms"
})
public class SMSType {

    protected String sender;
    @XmlElement(name = "SMS", required = true)
    protected SMSMessageType sms;

    /**
     * Recupera il valore della proprietà sender.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSender() {
        return sender;
    }

    /**
     * Imposta il valore della proprietà sender.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSender(String value) {
        this.sender = value;
    }

    /**
     * Recupera il valore della proprietà sms.
     * 
     * @return
     *     possible object is
     *     {@link SMSMessageType }
     *     
     */
    public SMSMessageType getSMS() {
        return sms;
    }

    /**
     * Imposta il valore della proprietà sms.
     * 
     * @param value
     *     allowed object is
     *     {@link SMSMessageType }
     *     
     */
    public void setSMS(SMSMessageType value) {
        this.sms = value;
    }

}
