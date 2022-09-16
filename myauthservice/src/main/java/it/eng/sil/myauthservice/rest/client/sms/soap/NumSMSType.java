
package it.eng.sil.myauthservice.rest.client.sms.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per numSMS_type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="numSMS_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numeroSMS" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "numSMS_type", propOrder = {
    "numeroSMS"
})
public class NumSMSType {

    protected int numeroSMS;

    /**
     * Recupera il valore della proprietà numeroSMS.
     * 
     */
    public int getNumeroSMS() {
        return numeroSMS;
    }

    /**
     * Imposta il valore della proprietà numeroSMS.
     * 
     */
    public void setNumeroSMS(int value) {
        this.numeroSMS = value;
    }

}
