
package it.eng.myportal.ws.youthGuarantee.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per richiesta_invioUtenteYG_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="richiesta_invioUtenteYG_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UtenteYG" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "richiesta_invioUtenteYG_Type", propOrder = {
    "utenteYG"
})
public class RichiestaInvioUtenteYGType {

    @XmlElement(name = "UtenteYG", required = true)
    protected String utenteYG;

    /**
     * Recupera il valore della propriet� utenteYG.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtenteYG() {
        return utenteYG;
    }

    /**
     * Imposta il valore della propriet� utenteYG.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtenteYG(String value) {
        this.utenteYG = value;
    }

}
