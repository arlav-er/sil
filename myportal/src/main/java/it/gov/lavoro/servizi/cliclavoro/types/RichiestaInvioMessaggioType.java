
package it.gov.lavoro.servizi.cliclavoro.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per richiesta_invioMessaggio_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="richiesta_invioMessaggio_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessaggioXML" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "richiesta_invioMessaggio_Type", propOrder = {
    "messaggioXML"
})
public class RichiestaInvioMessaggioType {

    @XmlElement(name = "MessaggioXML", required = true)
    protected String messaggioXML;

    /**
     * Recupera il valore della propriet messaggioXML.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessaggioXML() {
        return messaggioXML;
    }

    /**
     * Imposta il valore della propriet messaggioXML.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessaggioXML(String value) {
        this.messaggioXML = value;
    }

}
