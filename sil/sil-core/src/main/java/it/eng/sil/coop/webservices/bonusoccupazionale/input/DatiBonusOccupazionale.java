//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.11 at 12:37:48 PM CET 
//

package it.eng.sil.coop.webservices.bonusoccupazionale.input;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodiceFiscale" type="{}CodiceFiscale_Type"/>
 *         &lt;element name="DataAdesione" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="Regione" type="{}Regione"/>
 *         &lt;element name="identificativosap" type="{}IdentificativoSap" minOccurs="0"/>
 *         &lt;element name="codprovincia" type="{}Stringa3Obbl" minOccurs="0"/>
 *         &lt;element ref="{}politiche_attive" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "codiceFiscale", "dataAdesione", "regione", "identificativosap", "codprovincia",
		"politicheAttive" })
@XmlRootElement(name = "DatiBonusOccupazionale")
public class DatiBonusOccupazionale {

	@XmlElement(name = "CodiceFiscale", required = true)
	protected String codiceFiscale;
	@XmlElement(name = "DataAdesione", required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar dataAdesione;
	@XmlElement(name = "Regione")
	protected int regione;
	protected String identificativosap;
	protected String codprovincia;
	@XmlElement(name = "politiche_attive")
	protected List<PoliticheAttive> politicheAttive;

	/**
	 * Gets the value of the codiceFiscale property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the value of the codiceFiscale property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodiceFiscale(String value) {
		this.codiceFiscale = value;
	}

	/**
	 * Gets the value of the dataAdesione property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDataAdesione() {
		return dataAdesione;
	}

	/**
	 * Sets the value of the dataAdesione property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDataAdesione(XMLGregorianCalendar value) {
		this.dataAdesione = value;
	}

	/**
	 * Gets the value of the regione property.
	 * 
	 */
	public int getRegione() {
		return regione;
	}

	/**
	 * Sets the value of the regione property.
	 * 
	 */
	public void setRegione(int value) {
		this.regione = value;
	}

	/**
	 * Gets the value of the identificativosap property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdentificativosap() {
		return identificativosap;
	}

	/**
	 * Sets the value of the identificativosap property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdentificativosap(String value) {
		this.identificativosap = value;
	}

	/**
	 * Gets the value of the codprovincia property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodprovincia() {
		return codprovincia;
	}

	/**
	 * Sets the value of the codprovincia property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodprovincia(String value) {
		this.codprovincia = value;
	}

	/**
	 * Gets the value of the politicheAttive property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the politicheAttive property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPoliticheAttive().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link PoliticheAttive }
	 * 
	 * 
	 */
	public List<PoliticheAttive> getPoliticheAttive() {
		if (politicheAttive == null) {
			politicheAttive = new ArrayList<PoliticheAttive>();
		}
		return this.politicheAttive;
	}

}