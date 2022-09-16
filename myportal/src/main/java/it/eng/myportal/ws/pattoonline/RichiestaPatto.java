
package it.eng.myportal.ws.pattoonline;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RichiestaPatto complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RichiestaPatto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Patto" type="{http://pattoonline.ws.myportal.eng.it/}PattoType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RichiestaPatto", propOrder = {
    "patto"
})
public class RichiestaPatto {

    @XmlElement(name = "Patto", required = true)
    protected PattoType patto;

    /**
     * Recupera il valore della proprieta patto.
     * 
     * @return
     *     possible object is
     *     {@link PattoType }
     *     
     */
    public PattoType getPatto() {
        return patto;
    }

    /**
     * Imposta il valore della proprieta patto.
     * 
     * @param value
     *     allowed object is
     *     {@link PattoType }
     *     
     */
    public void setPatto(PattoType value) {
        this.patto = value;
    }

}
