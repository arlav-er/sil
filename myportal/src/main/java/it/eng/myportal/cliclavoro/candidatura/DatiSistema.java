//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.30 at 02:59:33 PM CEST 
//


package it.eng.myportal.cliclavoro.candidatura;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}ambitodiffusione"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}datainserimento"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}datascadenza" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idintermediario" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}codicefiscaleintermediario" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}denominazioneintermediario" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}indirizzo" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idcomune" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}cap" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}telefono" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}fax" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}email" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}visibilita" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}tipocandidatura"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}motivochiusura" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}codicecandidatura"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}codicecandidaturaprecedente" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}percettore" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ambitodiffusione",
    "datainserimento",
    "datascadenza",
    "idintermediario",
    "codicefiscaleintermediario",
    "denominazioneintermediario",
    "indirizzo",
    "idcomune",
    "cap",
    "telefono",
    "fax",
    "email",
    "visibilita",
    "tipocandidatura",
    "motivochiusura",
    "codicecandidatura",
    "codicecandidaturaprecedente",
    "percettore"
})
@XmlRootElement(name = "DatiSistema")
public class DatiSistema {

    @XmlElement(required = true)
    protected String ambitodiffusione;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datainserimento;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar datascadenza;
    protected String idintermediario;
    protected String codicefiscaleintermediario;
    protected String denominazioneintermediario;
    protected String indirizzo;
    protected String idcomune;
    protected String cap;
    protected String telefono;
    protected String fax;
    protected String email;
    protected SiNo visibilita;
    @XmlElement(required = true)
    protected String tipocandidatura;
    protected String motivochiusura;
    @XmlElement(required = true)
    protected String codicecandidatura;
    protected String codicecandidaturaprecedente;
    protected SiNo percettore;

    /**
     * Gets the value of the ambitodiffusione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmbitodiffusione() {
        return ambitodiffusione;
    }

    /**
     * Sets the value of the ambitodiffusione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmbitodiffusione(String value) {
        this.ambitodiffusione = value;
    }

    /**
     * Gets the value of the datainserimento property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatainserimento() {
        return datainserimento;
    }

    /**
     * Sets the value of the datainserimento property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatainserimento(XMLGregorianCalendar value) {
        this.datainserimento = value;
    }

    /**
     * Gets the value of the datascadenza property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatascadenza() {
        return datascadenza;
    }

    /**
     * Sets the value of the datascadenza property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatascadenza(XMLGregorianCalendar value) {
        this.datascadenza = value;
    }

    /**
     * Gets the value of the idintermediario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdintermediario() {
        return idintermediario;
    }

    /**
     * Sets the value of the idintermediario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdintermediario(String value) {
        this.idintermediario = value;
    }

    /**
     * Gets the value of the codicefiscaleintermediario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodicefiscaleintermediario() {
        return codicefiscaleintermediario;
    }

    /**
     * Sets the value of the codicefiscaleintermediario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodicefiscaleintermediario(String value) {
        this.codicefiscaleintermediario = value;
    }

    /**
     * Gets the value of the denominazioneintermediario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDenominazioneintermediario() {
        return denominazioneintermediario;
    }

    /**
     * Sets the value of the denominazioneintermediario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDenominazioneintermediario(String value) {
        this.denominazioneintermediario = value;
    }

    /**
     * Gets the value of the indirizzo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Sets the value of the indirizzo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndirizzo(String value) {
        this.indirizzo = value;
    }

    /**
     * Gets the value of the idcomune property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdcomune() {
        return idcomune;
    }

    /**
     * Sets the value of the idcomune property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdcomune(String value) {
        this.idcomune = value;
    }

    /**
     * Gets the value of the cap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCap() {
        return cap;
    }

    /**
     * Sets the value of the cap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCap(String value) {
        this.cap = value;
    }

    /**
     * Gets the value of the telefono property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Sets the value of the telefono property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefono(String value) {
        this.telefono = value;
    }

    /**
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFax(String value) {
        this.fax = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the visibilita property.
     * 
     * @return
     *     possible object is
     *     {@link SiNo }
     *     
     */
    public SiNo getVisibilita() {
        return visibilita;
    }

    /**
     * Sets the value of the visibilita property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiNo }
     *     
     */
    public void setVisibilita(SiNo value) {
        this.visibilita = value;
    }

    /**
     * Gets the value of the tipocandidatura property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipocandidatura() {
        return tipocandidatura;
    }

    /**
     * Sets the value of the tipocandidatura property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipocandidatura(String value) {
        this.tipocandidatura = value;
    }

    /**
     * Gets the value of the motivochiusura property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivochiusura() {
        return motivochiusura;
    }

    /**
     * Sets the value of the motivochiusura property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivochiusura(String value) {
        this.motivochiusura = value;
    }

    /**
     * Gets the value of the codicecandidatura property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodicecandidatura() {
        return codicecandidatura;
    }

    /**
     * Sets the value of the codicecandidatura property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodicecandidatura(String value) {
        this.codicecandidatura = value;
    }

    /**
     * Gets the value of the codicecandidaturaprecedente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodicecandidaturaprecedente() {
        return codicecandidaturaprecedente;
    }

    /**
     * Sets the value of the codicecandidaturaprecedente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodicecandidaturaprecedente(String value) {
        this.codicecandidaturaprecedente = value;
    }

    /**
     * Gets the value of the percettore property.
     * 
     * @return
     *     possible object is
     *     {@link SiNo }
     *     
     */
    public SiNo getPercettore() {
        return percettore;
    }

    /**
     * Sets the value of the percettore property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiNo }
     *     
     */
    public void setPercettore(SiNo value) {
        this.percettore = value;
    }

}
