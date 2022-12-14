//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.18 at 03:56:22 PM CET 
//

package it.eng.sil.coop.bean.blen.input.candidatura;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for DatiCurriculari element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="DatiCurriculari">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}EsperienzeLavorative" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}Istruzione" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}Formazione" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}Lingue" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}ConoscenzeInformatiche" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}AbilitazioniPatenti" minOccurs="0"/>
 *           &lt;element ref="{http://servizi.lavoro.gov.it/candidatura}ProfessioneDesiderataDisponibilita" maxOccurs="unbounded"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "esperienzeLavorative", "istruzione", "formazione", "lingue",
		"conoscenzeInformatiche", "abilitazioniPatenti", "professioneDesiderataDisponibilita" })
@XmlRootElement(name = "DatiCurriculari")
public class DatiCurriculari {

	@XmlElement(name = "EsperienzeLavorative", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected List<EsperienzeLavorative> esperienzeLavorative;
	@XmlElement(name = "Istruzione", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected List<Istruzione> istruzione;
	@XmlElement(name = "Formazione", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected List<Formazione> formazione;
	@XmlElement(name = "Lingue", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected List<Lingue> lingue;
	@XmlElement(name = "ConoscenzeInformatiche", namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected ConoscenzeInformatiche conoscenzeInformatiche;
	@XmlElement(name = "AbilitazioniPatenti", namespace = "http://servizi.lavoro.gov.it/candidatura")
	protected AbilitazioniPatenti abilitazioniPatenti;
	@XmlElement(name = "ProfessioneDesiderataDisponibilita", namespace = "http://servizi.lavoro.gov.it/candidatura", required = true)
	protected List<ProfessioneDesiderataDisponibilita> professioneDesiderataDisponibilita;

	/**
	 * Gets the value of the esperienzeLavorative property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the esperienzeLavorative property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getEsperienzeLavorative().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link EsperienzeLavorative }
	 * 
	 * 
	 */
	public List<EsperienzeLavorative> getEsperienzeLavorative() {
		if (esperienzeLavorative == null) {
			esperienzeLavorative = new ArrayList<EsperienzeLavorative>();
		}
		return this.esperienzeLavorative;
	}

	/**
	 * Gets the value of the istruzione property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the istruzione property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getIstruzione().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Istruzione }
	 * 
	 * 
	 */
	public List<Istruzione> getIstruzione() {
		if (istruzione == null) {
			istruzione = new ArrayList<Istruzione>();
		}
		return this.istruzione;
	}

	/**
	 * Gets the value of the formazione property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the formazione property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getFormazione().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Formazione }
	 * 
	 * 
	 */
	public List<Formazione> getFormazione() {
		if (formazione == null) {
			formazione = new ArrayList<Formazione>();
		}
		return this.formazione;
	}

	/**
	 * Gets the value of the lingue property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the lingue property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getLingue().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Lingue }
	 * 
	 * 
	 */
	public List<Lingue> getLingue() {
		if (lingue == null) {
			lingue = new ArrayList<Lingue>();
		}
		return this.lingue;
	}

	/**
	 * Gets the value of the conoscenzeInformatiche property.
	 * 
	 * @return possible object is {@link ConoscenzeInformatiche }
	 * 
	 */
	public ConoscenzeInformatiche getConoscenzeInformatiche() {
		return conoscenzeInformatiche;
	}

	/**
	 * Sets the value of the conoscenzeInformatiche property.
	 * 
	 * @param value
	 *            allowed object is {@link ConoscenzeInformatiche }
	 * 
	 */
	public void setConoscenzeInformatiche(ConoscenzeInformatiche value) {
		this.conoscenzeInformatiche = value;
	}

	/**
	 * Gets the value of the abilitazioniPatenti property.
	 * 
	 * @return possible object is {@link AbilitazioniPatenti }
	 * 
	 */
	public AbilitazioniPatenti getAbilitazioniPatenti() {
		return abilitazioniPatenti;
	}

	/**
	 * Sets the value of the abilitazioniPatenti property.
	 * 
	 * @param value
	 *            allowed object is {@link AbilitazioniPatenti }
	 * 
	 */
	public void setAbilitazioniPatenti(AbilitazioniPatenti value) {
		this.abilitazioniPatenti = value;
	}

	/**
	 * Gets the value of the professioneDesiderataDisponibilita property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the professioneDesiderataDisponibilita property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getProfessioneDesiderataDisponibilita().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link ProfessioneDesiderataDisponibilita }
	 * 
	 * 
	 */
	public List<ProfessioneDesiderataDisponibilita> getProfessioneDesiderataDisponibilita() {
		if (professioneDesiderataDisponibilita == null) {
			professioneDesiderataDisponibilita = new ArrayList<ProfessioneDesiderataDisponibilita>();
		}
		return this.professioneDesiderataDisponibilita;
	}

}
