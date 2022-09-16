
package it.eng.myportal.ws.pattoonline;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ResponsePatto complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ResponsePatto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PattoPortale" type="{http://pattoonline.ws.myportal.eng.it/}PattoPortaleType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponsePatto", propOrder = {
    "pattoPortale"
})
public class ResponsePatto {

    @XmlElement(name = "PattoPortale", required = true)
    protected PattoPortaleType pattoPortale;

    /**
     * Recupera il valore della proprieta pattoPortale.
     * 
     * @return
     *     possible object is
     *     {@link PattoPortaleType }
     *     
     */
    public PattoPortaleType getPattoPortale() {
        return pattoPortale;
    }

    /**
     * Imposta il valore della proprieta pattoPortale.
     * 
     * @param value
     *     allowed object is
     *     {@link PattoPortaleType }
     *     
     */
    public void setPattoPortale(PattoPortaleType value) {
        this.pattoPortale = value;
    }

}
