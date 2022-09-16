
package it.eng.sil.myauthservice.rest.client.sms.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java per SendStatusResponse_type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SendStatusResponse_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SMSStatus" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="SendStatus" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;simpleContent&gt;
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                           &lt;attribute name="recipient" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="sendDate" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/simpleContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
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
@XmlType(name = "SendStatusResponse_type", propOrder = {
    "smsStatus"
})
public class SendStatusResponseType {

    @XmlElement(name = "SMSStatus")
    protected SendStatusResponseType.SMSStatus smsStatus;

    /**
     * Recupera il valore della proprietà smsStatus.
     * 
     * @return
     *     possible object is
     *     {@link SendStatusResponseType.SMSStatus }
     *     
     */
    public SendStatusResponseType.SMSStatus getSMSStatus() {
        return smsStatus;
    }

    /**
     * Imposta il valore della proprietà smsStatus.
     * 
     * @param value
     *     allowed object is
     *     {@link SendStatusResponseType.SMSStatus }
     *     
     */
    public void setSMSStatus(SendStatusResponseType.SMSStatus value) {
        this.smsStatus = value;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="SendStatus" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;simpleContent&gt;
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                 &lt;attribute name="recipient" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="sendDate" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *               &lt;/extension&gt;
     *             &lt;/simpleContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sendStatus"
    })
    public static class SMSStatus {

        @XmlElement(name = "SendStatus")
        protected List<SendStatusResponseType.SMSStatus.SendStatus> sendStatus;
        @XmlAttribute(name = "id", required = true)
        protected String id;

        /**
         * Gets the value of the sendStatus property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sendStatus property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSendStatus().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SendStatusResponseType.SMSStatus.SendStatus }
         * 
         * 
         */
        public List<SendStatusResponseType.SMSStatus.SendStatus> getSendStatus() {
            if (sendStatus == null) {
                sendStatus = new ArrayList<SendStatusResponseType.SMSStatus.SendStatus>();
            }
            return this.sendStatus;
        }

        /**
         * Recupera il valore della proprietà id.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Imposta il valore della proprietà id.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }


        /**
         * <p>Classe Java per anonymous complex type.
         * 
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;simpleContent&gt;
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *       &lt;attribute name="recipient" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="sendDate" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *     &lt;/extension&gt;
         *   &lt;/simpleContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class SendStatus {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "recipient")
            protected String recipient;
            @XmlAttribute(name = "sendDate")
            protected String sendDate;
            @XmlAttribute(name = "status")
            protected String status;

            /**
             * Recupera il valore della proprietà value.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Imposta il valore della proprietà value.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Recupera il valore della proprietà recipient.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRecipient() {
                return recipient;
            }

            /**
             * Imposta il valore della proprietà recipient.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRecipient(String value) {
                this.recipient = value;
            }

            /**
             * Recupera il valore della proprietà sendDate.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSendDate() {
                return sendDate;
            }

            /**
             * Imposta il valore della proprietà sendDate.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSendDate(String value) {
                this.sendDate = value;
            }

            /**
             * Recupera il valore della proprietà status.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getStatus() {
                return status;
            }

            /**
             * Imposta il valore della proprietà status.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setStatus(String value) {
                this.status = value;
            }

        }

    }

}
