//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.30 at 02:59:33 PM CEST 
//


package it.eng.myportal.cliclavoro.candidatura;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}titolocorso" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idsede" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}durata" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idtipologiadurata" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idattestazione" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idqualifica" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}descrqualifica" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}codice" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}annoform" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}descrizione" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}contenuto" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}sede" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}completato" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}idattestazionesil" minOccurs="0"/>
 *         &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}cdnambitodisciplinare" minOccurs="0"/>
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
    "titolocorso",
    "idsede",
    "durata",
    "idtipologiadurata",
    "idattestazione",
    "idqualifica",
    "descrqualifica",
    "codice",
    "annoform",
    "descrizione",
    "contenuto",
    "sede",
    "completato",
    "idattestazionesil",
    "cdnambitodisciplinare"
})
@XmlRootElement(name = "Formazione")
public class Formazione {

    protected String titolocorso;
    protected String idsede;
    protected String durata;
    protected TipologiaDurata idtipologiadurata;
    protected Attestazionecheck idattestazione;
    protected String idqualifica;
    protected String descrqualifica;
    protected String codice;
    protected String annoform;
    protected String descrizione;
    protected String contenuto;
    protected String sede;
    protected SiNo completato;
    protected String idattestazionesil;
    protected String cdnambitodisciplinare;

    /**
     * Gets the value of the titolocorso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitolocorso() {
        return titolocorso;
    }

    /**
     * Sets the value of the titolocorso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitolocorso(String value) {
        this.titolocorso = value;
    }

    /**
     * Gets the value of the idsede property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdsede() {
        return idsede;
    }

    /**
     * Sets the value of the idsede property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdsede(String value) {
        this.idsede = value;
    }

    /**
     * Gets the value of the durata property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDurata() {
        return durata;
    }

    /**
     * Sets the value of the durata property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDurata(String value) {
        this.durata = value;
    }

    /**
     * Gets the value of the idtipologiadurata property.
     * 
     * @return
     *     possible object is
     *     {@link TipologiaDurata }
     *     
     */
    public TipologiaDurata getIdtipologiadurata() {
        return idtipologiadurata;
    }

    /**
     * Sets the value of the idtipologiadurata property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipologiaDurata }
     *     
     */
    public void setIdtipologiadurata(TipologiaDurata value) {
        this.idtipologiadurata = value;
    }

    /**
     * Gets the value of the idattestazione property.
     * 
     * @return
     *     possible object is
     *     {@link Attestazionecheck }
     *     
     */
    public Attestazionecheck getIdattestazione() {
        return idattestazione;
    }

    /**
     * Sets the value of the idattestazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link Attestazionecheck }
     *     
     */
    public void setIdattestazione(Attestazionecheck value) {
        this.idattestazione = value;
    }

    /**
     * Gets the value of the idqualifica property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdqualifica() {
        return idqualifica;
    }

    /**
     * Sets the value of the idqualifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdqualifica(String value) {
        this.idqualifica = value;
    }

    /**
     * Gets the value of the descrqualifica property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrqualifica() {
        return descrqualifica;
    }

    /**
     * Sets the value of the descrqualifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrqualifica(String value) {
        this.descrqualifica = value;
    }

    /**
     * Gets the value of the codice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Sets the value of the codice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodice(String value) {
        this.codice = value;
    }

    /**
     * Gets the value of the annoform property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnnoform() {
        return annoform;
    }

    /**
     * Sets the value of the annoform property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnnoform(String value) {
        this.annoform = value;
    }

    /**
     * Gets the value of the descrizione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Sets the value of the descrizione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizione(String value) {
        this.descrizione = value;
    }

    /**
     * Gets the value of the contenuto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContenuto() {
        return contenuto;
    }

    /**
     * Sets the value of the contenuto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContenuto(String value) {
        this.contenuto = value;
    }

    /**
     * Gets the value of the sede property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSede() {
        return sede;
    }

    /**
     * Sets the value of the sede property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSede(String value) {
        this.sede = value;
    }

    /**
     * Gets the value of the completato property.
     * 
     * @return
     *     possible object is
     *     {@link SiNo }
     *     
     */
    public SiNo getCompletato() {
        return completato;
    }

    /**
     * Sets the value of the completato property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiNo }
     *     
     */
    public void setCompletato(SiNo value) {
        this.completato = value;
    }

    /**
     * Gets the value of the idattestazionesil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdattestazionesil() {
        return idattestazionesil;
    }

    /**
     * Sets the value of the idattestazionesil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdattestazionesil(String value) {
        this.idattestazionesil = value;
    }

    /**
     * Gets the value of the cdnambitodisciplinare property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCdnambitodisciplinare() {
        return cdnambitodisciplinare;
    }

    /**
     * Sets the value of the cdnambitodisciplinare property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCdnambitodisciplinare(String value) {
        this.cdnambitodisciplinare = value;
    }

}
