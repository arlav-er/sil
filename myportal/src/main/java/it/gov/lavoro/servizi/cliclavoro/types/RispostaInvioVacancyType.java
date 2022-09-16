
package it.gov.lavoro.servizi.cliclavoro.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per risposta_invioVacancy_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="risposta_invioVacancy_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tipo_Risposta">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="OK"/>
 *               &lt;enumeration value="KO"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Descr_Esito" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "risposta_invioVacancy_Type", propOrder = {
    "tipoRisposta",
    "descrEsito"
})
public class RispostaInvioVacancyType {

    @XmlElement(name = "Tipo_Risposta", required = true)
    protected String tipoRisposta;
    @XmlElement(name = "Descr_Esito", required = true)
    protected String descrEsito;

    /**
     * Recupera il valore della propriet tipoRisposta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoRisposta() {
        return tipoRisposta;
    }

    /**
     * Imposta il valore della propriet tipoRisposta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoRisposta(String value) {
        this.tipoRisposta = value;
    }

    /**
     * Recupera il valore della propriet descrEsito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrEsito() {
        return descrEsito;
    }

    /**
     * Imposta il valore della propriet descrEsito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrEsito(String value) {
        this.descrEsito = value;
    }

}
