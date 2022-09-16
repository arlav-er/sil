
package it.eng.myportal.ws.statoadesione.set.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per richiesta_SetStatoAdesioneYG_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="richiesta_SetStatoAdesioneYG_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DatiStatoAdesione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "richiesta_SetStatoAdesioneYG_Type", propOrder = {
    "datiStatoAdesione"
})
public class RichiestaSetStatoAdesioneYGType {

    @XmlElement(name = "DatiStatoAdesione", required = true)
    protected String datiStatoAdesione;

    /**
     * Recupera il valore della propriet� datiStatoAdesione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatiStatoAdesione() {
        return datiStatoAdesione;
    }

    /**
     * Imposta il valore della propriet� datiStatoAdesione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatiStatoAdesione(String value) {
        this.datiStatoAdesione = value;
    }

}
