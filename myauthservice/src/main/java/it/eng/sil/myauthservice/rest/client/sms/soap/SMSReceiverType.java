
package it.eng.sil.myauthservice.rest.client.sms.soap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SMSReceiver_type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SMSReceiver_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="statusCode" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="statusMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroMessaggi" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="msgReceived" type="{http://www.infotn.it/SMS-EAI/InvioSMS_types}MessageInfo_type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMSReceiver_type", propOrder = {
    "statusCode",
    "statusMessage",
    "numeroMessaggi",
    "msgReceived"
})
public class SMSReceiverType {

    @XmlElement(required = true)
    protected BigInteger statusCode;
    protected String statusMessage;
    protected BigInteger numeroMessaggi;
    protected List<MessageInfoType> msgReceived;

    /**
     * Recupera il valore della proprietà statusCode.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStatusCode() {
        return statusCode;
    }

    /**
     * Imposta il valore della proprietà statusCode.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStatusCode(BigInteger value) {
        this.statusCode = value;
    }

    /**
     * Recupera il valore della proprietà statusMessage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Imposta il valore della proprietà statusMessage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

    /**
     * Recupera il valore della proprietà numeroMessaggi.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumeroMessaggi() {
        return numeroMessaggi;
    }

    /**
     * Imposta il valore della proprietà numeroMessaggi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumeroMessaggi(BigInteger value) {
        this.numeroMessaggi = value;
    }

    /**
     * Gets the value of the msgReceived property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the msgReceived property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMsgReceived().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MessageInfoType }
     * 
     * 
     */
    public List<MessageInfoType> getMsgReceived() {
        if (msgReceived == null) {
            msgReceived = new ArrayList<MessageInfoType>();
        }
        return this.msgReceived;
    }

}
