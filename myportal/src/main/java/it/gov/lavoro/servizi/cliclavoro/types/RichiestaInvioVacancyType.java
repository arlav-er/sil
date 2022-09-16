
package it.gov.lavoro.servizi.cliclavoro.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per richiesta_invioVacancy_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="richiesta_invioVacancy_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VacancyXML" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "richiesta_invioVacancy_Type", propOrder = {
    "vacancyXML"
})
public class RichiestaInvioVacancyType {

    @XmlElement(name = "VacancyXML", required = true)
    protected String vacancyXML;

    /**
     * Recupera il valore della propriet vacancyXML.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVacancyXML() {
        return vacancyXML;
    }

    /**
     * Imposta il valore della propriet vacancyXML.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVacancyXML(String value) {
        this.vacancyXML = value;
    }

}
