//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.13 at 11:45:47 AM CEST 
//

package it.eng.sil.pojo.yg.sap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
 *         &lt;element ref="{}tipocontratto"/>
 *         &lt;element ref="{}categoriainquadramento" minOccurs="0"/>
 *         &lt;element ref="{}assunzioneLegge68"/>
 *         &lt;element name="lavInMobilita" type="{}Si-No"/>
 *         &lt;element name="lavoroStagionale" type="{}Si-No"/>
 *         &lt;element name="lavoroInAgricoltura" type="{}Si-No"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "tipocontratto", "categoriainquadramento", "assunzioneLegge68", "lavInMobilita",
		"lavoroStagionale", "lavoroInAgricoltura" })
@XmlRootElement(name = "tiporapporto")
public class Tiporapporto {

	@XmlElement(required = true)
	protected String tipocontratto;
	protected String categoriainquadramento;
	@XmlElement(required = true)
	protected SiNo assunzioneLegge68;
	@XmlElement(required = true)
	protected SiNo lavInMobilita;
	@XmlElement(required = true)
	protected SiNo lavoroStagionale;
	@XmlElement(required = true)
	protected SiNo lavoroInAgricoltura;

	/**
	 * Gets the value of the tipocontratto property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipocontratto() {
		return tipocontratto;
	}

	/**
	 * Sets the value of the tipocontratto property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTipocontratto(String value) {
		this.tipocontratto = value;
	}

	/**
	 * Gets the value of the categoriainquadramento property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCategoriainquadramento() {
		return categoriainquadramento;
	}

	/**
	 * Sets the value of the categoriainquadramento property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCategoriainquadramento(String value) {
		this.categoriainquadramento = value;
	}

	/**
	 * Gets the value of the assunzioneLegge68 property.
	 * 
	 * @return possible object is {@link SiNo }
	 * 
	 */
	public SiNo getAssunzioneLegge68() {
		return assunzioneLegge68;
	}

	/**
	 * Sets the value of the assunzioneLegge68 property.
	 * 
	 * @param value
	 *            allowed object is {@link SiNo }
	 * 
	 */
	public void setAssunzioneLegge68(SiNo value) {
		this.assunzioneLegge68 = value;
	}

	/**
	 * Gets the value of the lavInMobilita property.
	 * 
	 * @return possible object is {@link SiNo }
	 * 
	 */
	public SiNo getLavInMobilita() {
		return lavInMobilita;
	}

	/**
	 * Sets the value of the lavInMobilita property.
	 * 
	 * @param value
	 *            allowed object is {@link SiNo }
	 * 
	 */
	public void setLavInMobilita(SiNo value) {
		this.lavInMobilita = value;
	}

	/**
	 * Gets the value of the lavoroStagionale property.
	 * 
	 * @return possible object is {@link SiNo }
	 * 
	 */
	public SiNo getLavoroStagionale() {
		return lavoroStagionale;
	}

	/**
	 * Sets the value of the lavoroStagionale property.
	 * 
	 * @param value
	 *            allowed object is {@link SiNo }
	 * 
	 */
	public void setLavoroStagionale(SiNo value) {
		this.lavoroStagionale = value;
	}

	/**
	 * Gets the value of the lavoroInAgricoltura property.
	 * 
	 * @return possible object is {@link SiNo }
	 * 
	 */
	public SiNo getLavoroInAgricoltura() {
		return lavoroInAgricoltura;
	}

	/**
	 * Sets the value of the lavoroInAgricoltura property.
	 * 
	 * @param value
	 *            allowed object is {@link SiNo }
	 * 
	 */
	public void setLavoroInAgricoltura(SiNo value) {
		this.lavoroInAgricoltura = value;
	}

}
