
package it.eng.myportal.ws.statoadesione.get.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for richiesta_getStatoAdesioneYG_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="richiesta_getStatoAdesioneYG_Type">
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
@XmlType(name = "richiesta_getStatoAdesioneYG_Type", propOrder = {
    "datiStatoAdesione"
})
public class RichiestaGetStatoAdesioneYGType {

    @XmlElement(name = "DatiStatoAdesione", required = true)
    protected String datiStatoAdesione;

    /**
     * Gets the value of the datiStatoAdesione property.
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
     * Sets the value of the datiStatoAdesione property.
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
