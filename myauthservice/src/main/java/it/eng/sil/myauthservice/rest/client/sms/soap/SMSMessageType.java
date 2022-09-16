
package it.eng.sil.myauthservice.rest.client.sms.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per SMSMessage_type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SMSMessage_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MsgID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="MobileDest" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *         &lt;element name="DateForSend" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMSMessage_type", propOrder = {
    "msgID",
    "text",
    "mobileDest",
    "dateForSend"
})
public class SMSMessageType {

    @XmlElement(name = "MsgID")
    protected String msgID;
    @XmlElement(name = "Text", required = true)
    protected String text;
    @XmlElement(name = "MobileDest", required = true)
    protected List<String> mobileDest;
    @XmlElement(name = "DateForSend")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateForSend;

    /**
     * Recupera il valore della proprietà msgID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgID() {
        return msgID;
    }

    /**
     * Imposta il valore della proprietà msgID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgID(String value) {
        this.msgID = value;
    }

    /**
     * Recupera il valore della proprietà text.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Imposta il valore della proprietà text.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the mobileDest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mobileDest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMobileDest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMobileDest() {
        if (mobileDest == null) {
            mobileDest = new ArrayList<String>();
        }
        return this.mobileDest;
    }

    /**
     * Recupera il valore della proprietà dateForSend.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateForSend() {
        return dateForSend;
    }

    /**
     * Imposta il valore della proprietà dateForSend.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateForSend(XMLGregorianCalendar value) {
        this.dateForSend = value;
    }

}
