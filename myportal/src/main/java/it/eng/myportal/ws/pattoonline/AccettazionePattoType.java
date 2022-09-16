
package it.eng.myportal.ws.pattoonline;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per AccettazionePattoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AccettazionePattoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DtmAccettazione" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="TipoAccettazione"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="OTP"/&gt;
 *               &lt;enumeration value="SPID"/&gt;
 *               &lt;enumeration value="CIE"/&gt;
 *               &lt;enumeration value="RV"/&gt;
 *               &lt;enumeration value="SMS"/&gt;
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
@XmlType(name = "AccettazionePattoType", propOrder = {
    "dtmAccettazione",
    "tipoAccettazione"
})
public class AccettazionePattoType {

    @XmlElement(name = "DtmAccettazione", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dtmAccettazione;
    @XmlElement(name = "TipoAccettazione", required = true)
    protected AccettazionePattoType.TipoAccettazioneEnum tipoAccettazione;

    /**
     * Recupera il valore della proprieta dtmAccettazione.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDtmAccettazione() {
        return dtmAccettazione;
    }

    /**
     * Imposta il valore della proprieta dtmAccettazione.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDtmAccettazione(XMLGregorianCalendar value) {
        this.dtmAccettazione = value;
    }

    /**
     * Recupera il valore della proprieta tipoAccettazione.
     * 
     * @return
     *     possible object is
     *     {@link AccettazionePattoType.TipoAccettazioneEnum }
     *     
     */
    public AccettazionePattoType.TipoAccettazioneEnum getTipoAccettazione() {
        return tipoAccettazione;
    }

    /**
     * Imposta il valore della proprieta tipoAccettazione.
     * 
     * @param value
     *     allowed object is
     *     {@link AccettazionePattoType.TipoAccettazioneEnum }
     *     
     */
    public void setTipoAccettazione(AccettazionePattoType.TipoAccettazioneEnum value) {
        this.tipoAccettazione = value;
    }


    /**
     * <p>Classe Java per null.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * <p>
     * <pre>
     * &lt;simpleType&gt;
     *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *     &lt;enumeration value="OTP"/&gt;
     *     &lt;enumeration value="SPID"/&gt;
     *     &lt;enumeration value="CIE"/&gt;
     *     &lt;enumeration value="RV"/&gt;
     *     &lt;enumeration value="SMS"/&gt;
     *   &lt;/restriction&gt;
     * &lt;/simpleType&gt;
     * </pre>
     * 
     */
    @XmlType(name = "")
    @XmlEnum
    public enum TipoAccettazioneEnum {

        OTP,
        SPID,
        CIE,
        RV,
        SMS;

        public String value() {
            return name();
        }

        public static AccettazionePattoType.TipoAccettazioneEnum fromValue(String v) {
            return valueOf(v);
        }

    }

}
