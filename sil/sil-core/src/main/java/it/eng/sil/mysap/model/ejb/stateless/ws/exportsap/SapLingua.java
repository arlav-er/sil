/**
 * SapLingua.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapLingua extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoLetto;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoParlato;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoScritto;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeLingua deLingua;

	private java.lang.Boolean flgCertificazione;

	private java.lang.Boolean flgMadrelingua;

	private java.lang.Integer idSapLingua;

	public SapLingua() {
	}

	public SapLingua(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoLetto,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoParlato,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoScritto,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeLingua deLingua, java.lang.Boolean flgCertificazione,
			java.lang.Boolean flgMadrelingua, java.lang.Integer idSapLingua) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deGradoLetto = deGradoLetto;
		this.deGradoParlato = deGradoParlato;
		this.deGradoScritto = deGradoScritto;
		this.deLingua = deLingua;
		this.flgCertificazione = flgCertificazione;
		this.flgMadrelingua = flgMadrelingua;
		this.idSapLingua = idSapLingua;
	}

	/**
	 * Gets the deGradoLetto value for this SapLingua.
	 * 
	 * @return deGradoLetto
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin getDeGradoLetto() {
		return deGradoLetto;
	}

	/**
	 * Sets the deGradoLetto value for this SapLingua.
	 * 
	 * @param deGradoLetto
	 */
	public void setDeGradoLetto(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoLetto) {
		this.deGradoLetto = deGradoLetto;
	}

	/**
	 * Gets the deGradoParlato value for this SapLingua.
	 * 
	 * @return deGradoParlato
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin getDeGradoParlato() {
		return deGradoParlato;
	}

	/**
	 * Sets the deGradoParlato value for this SapLingua.
	 * 
	 * @param deGradoParlato
	 */
	public void setDeGradoParlato(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoParlato) {
		this.deGradoParlato = deGradoParlato;
	}

	/**
	 * Gets the deGradoScritto value for this SapLingua.
	 * 
	 * @return deGradoScritto
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin getDeGradoScritto() {
		return deGradoScritto;
	}

	/**
	 * Sets the deGradoScritto value for this SapLingua.
	 * 
	 * @param deGradoScritto
	 */
	public void setDeGradoScritto(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoLin deGradoScritto) {
		this.deGradoScritto = deGradoScritto;
	}

	/**
	 * Gets the deLingua value for this SapLingua.
	 * 
	 * @return deLingua
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeLingua getDeLingua() {
		return deLingua;
	}

	/**
	 * Sets the deLingua value for this SapLingua.
	 * 
	 * @param deLingua
	 */
	public void setDeLingua(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeLingua deLingua) {
		this.deLingua = deLingua;
	}

	/**
	 * Gets the flgCertificazione value for this SapLingua.
	 * 
	 * @return flgCertificazione
	 */
	public java.lang.Boolean getFlgCertificazione() {
		return flgCertificazione;
	}

	/**
	 * Sets the flgCertificazione value for this SapLingua.
	 * 
	 * @param flgCertificazione
	 */
	public void setFlgCertificazione(java.lang.Boolean flgCertificazione) {
		this.flgCertificazione = flgCertificazione;
	}

	/**
	 * Gets the flgMadrelingua value for this SapLingua.
	 * 
	 * @return flgMadrelingua
	 */
	public java.lang.Boolean getFlgMadrelingua() {
		return flgMadrelingua;
	}

	/**
	 * Sets the flgMadrelingua value for this SapLingua.
	 * 
	 * @param flgMadrelingua
	 */
	public void setFlgMadrelingua(java.lang.Boolean flgMadrelingua) {
		this.flgMadrelingua = flgMadrelingua;
	}

	/**
	 * Gets the idSapLingua value for this SapLingua.
	 * 
	 * @return idSapLingua
	 */
	public java.lang.Integer getIdSapLingua() {
		return idSapLingua;
	}

	/**
	 * Sets the idSapLingua value for this SapLingua.
	 * 
	 * @param idSapLingua
	 */
	public void setIdSapLingua(java.lang.Integer idSapLingua) {
		this.idSapLingua = idSapLingua;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapLingua))
			return false;
		SapLingua other = (SapLingua) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj)
				&& ((this.deGradoLetto == null && other.getDeGradoLetto() == null)
						|| (this.deGradoLetto != null && this.deGradoLetto.equals(other.getDeGradoLetto())))
				&& ((this.deGradoParlato == null && other.getDeGradoParlato() == null)
						|| (this.deGradoParlato != null && this.deGradoParlato.equals(other.getDeGradoParlato())))
				&& ((this.deGradoScritto == null && other.getDeGradoScritto() == null)
						|| (this.deGradoScritto != null && this.deGradoScritto.equals(other.getDeGradoScritto())))
				&& ((this.deLingua == null && other.getDeLingua() == null)
						|| (this.deLingua != null && this.deLingua.equals(other.getDeLingua())))
				&& ((this.flgCertificazione == null && other.getFlgCertificazione() == null)
						|| (this.flgCertificazione != null
								&& this.flgCertificazione.equals(other.getFlgCertificazione())))
				&& ((this.flgMadrelingua == null && other.getFlgMadrelingua() == null)
						|| (this.flgMadrelingua != null && this.flgMadrelingua.equals(other.getFlgMadrelingua())))
				&& ((this.idSapLingua == null && other.getIdSapLingua() == null)
						|| (this.idSapLingua != null && this.idSapLingua.equals(other.getIdSapLingua())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = super.hashCode();
		if (getDeGradoLetto() != null) {
			_hashCode += getDeGradoLetto().hashCode();
		}
		if (getDeGradoParlato() != null) {
			_hashCode += getDeGradoParlato().hashCode();
		}
		if (getDeGradoScritto() != null) {
			_hashCode += getDeGradoScritto().hashCode();
		}
		if (getDeLingua() != null) {
			_hashCode += getDeLingua().hashCode();
		}
		if (getFlgCertificazione() != null) {
			_hashCode += getFlgCertificazione().hashCode();
		}
		if (getFlgMadrelingua() != null) {
			_hashCode += getFlgMadrelingua().hashCode();
		}
		if (getIdSapLingua() != null) {
			_hashCode += getIdSapLingua().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapLingua.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapLingua"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deGradoLetto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deGradoLetto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deGradoLin"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deGradoParlato");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deGradoParlato"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deGradoLin"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deGradoScritto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deGradoScritto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deGradoLin"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deLingua");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deLingua"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deLingua"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgCertificazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgCertificazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgMadrelingua");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgMadrelingua"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapLingua");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapLingua"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

}
