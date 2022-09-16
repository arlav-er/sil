//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.12.17 alle 03:34:33 PM CET 
//


package it.eng.sil.coop.webservices.art16online.istanze.xsd.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per CandidaturaType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="CandidaturaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Anagrafica" type="{}AnagraficaType"/>
 *         &lt;element name="Residenza" type="{}ResidenzaType"/>
 *         &lt;element name="Contatti" type="{}ContattiType"/>
 *         &lt;element name="ExtraUE" type="{}ExtraUEType" minOccurs="0"/>
 *         &lt;element name="ISEE" type="{}ISEEType" minOccurs="0"/>
 *         &lt;element name="Istanza" type="{}IstanzaType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="dataaggiornamento" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CandidaturaType", propOrder = {
    "anagrafica",
    "residenza",
    "contatti",
    "extraUE",
    "isee",
    "istanza"
})
@XmlRootElement(name = "CandidaturaType")
public class CandidaturaType {

    @XmlElement(name = "Anagrafica", required = true)
    protected AnagraficaType anagrafica;
    @XmlElement(name = "Residenza", required = true)
    protected ResidenzaType residenza;
    @XmlElement(name = "Contatti", required = true)
    protected ContattiType contatti;
    @XmlElement(name = "ExtraUE")
    protected ExtraUEType extraUE;
    @XmlElement(name = "ISEE")
    protected ISEEType isee;
    @XmlElement(name = "Istanza", required = true)
    protected IstanzaType istanza;
    @XmlAttribute(name = "dataaggiornamento")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataaggiornamento;

    /**
     * Recupera il valore della proprietà anagrafica.
     * 
     * @return
     *     possible object is
     *     {@link AnagraficaType }
     *     
     */
    public AnagraficaType getAnagrafica() {
        return anagrafica;
    }

    /**
     * Imposta il valore della proprietà anagrafica.
     * 
     * @param value
     *     allowed object is
     *     {@link AnagraficaType }
     *     
     */
    public void setAnagrafica(AnagraficaType value) {
        this.anagrafica = value;
    }

    /**
     * Recupera il valore della proprietà residenza.
     * 
     * @return
     *     possible object is
     *     {@link ResidenzaType }
     *     
     */
    public ResidenzaType getResidenza() {
        return residenza;
    }

    /**
     * Imposta il valore della proprietà residenza.
     * 
     * @param value
     *     allowed object is
     *     {@link ResidenzaType }
     *     
     */
    public void setResidenza(ResidenzaType value) {
        this.residenza = value;
    }

    /**
     * Recupera il valore della proprietà contatti.
     * 
     * @return
     *     possible object is
     *     {@link ContattiType }
     *     
     */
    public ContattiType getContatti() {
        return contatti;
    }

    /**
     * Imposta il valore della proprietà contatti.
     * 
     * @param value
     *     allowed object is
     *     {@link ContattiType }
     *     
     */
    public void setContatti(ContattiType value) {
        this.contatti = value;
    }

    /**
     * Recupera il valore della proprietà extraUE.
     * 
     * @return
     *     possible object is
     *     {@link ExtraUEType }
     *     
     */
    public ExtraUEType getExtraUE() {
        return extraUE;
    }

    /**
     * Imposta il valore della proprietà extraUE.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtraUEType }
     *     
     */
    public void setExtraUE(ExtraUEType value) {
        this.extraUE = value;
    }

    /**
     * Recupera il valore della proprietà isee.
     * 
     * @return
     *     possible object is
     *     {@link ISEEType }
     *     
     */
    public ISEEType getISEE() {
        return isee;
    }

    /**
     * Imposta il valore della proprietà isee.
     * 
     * @param value
     *     allowed object is
     *     {@link ISEEType }
     *     
     */
    public void setISEE(ISEEType value) {
        this.isee = value;
    }

    /**
     * Recupera il valore della proprietà istanza.
     * 
     * @return
     *     possible object is
     *     {@link IstanzaType }
     *     
     */
    public IstanzaType getIstanza() {
        return istanza;
    }

    /**
     * Imposta il valore della proprietà istanza.
     * 
     * @param value
     *     allowed object is
     *     {@link IstanzaType }
     *     
     */
    public void setIstanza(IstanzaType value) {
        this.istanza = value;
    }

    /**
     * Recupera il valore della proprietà dataaggiornamento.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataaggiornamento() {
        return dataaggiornamento;
    }

    /**
     * Imposta il valore della proprietà dataaggiornamento.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataaggiornamento(XMLGregorianCalendar value) {
        this.dataaggiornamento = value;
    }

}
