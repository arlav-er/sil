package it.gov.lavoro.servizi.cliclavoro.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per richiesta_invioCandidatura_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="richiesta_invioCandidatura_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CandidaturaXML" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "richiesta_invioCandidatura_Type", propOrder = {
    "candidaturaXML"
})
public class RichiestaInvioCandidaturaType {

    @XmlElement(name = "CandidaturaXML", required = true)
    protected String candidaturaXML;

    /**
     * Recupera il valore della propriet candidaturaXML.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCandidaturaXML() {
        return candidaturaXML;
    }

    /**
     * Imposta il valore della propriet candidaturaXML.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCandidaturaXML(String value) {
        this.candidaturaXML = value;
    }

}
