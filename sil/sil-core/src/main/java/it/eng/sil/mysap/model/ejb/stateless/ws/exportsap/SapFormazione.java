/**
 * SapFormazione.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapFormazione extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCorso deCorso;

	private java.lang.Boolean flgCompletato;

	private java.lang.Integer idSapFormazione;

	private java.lang.Integer numAnnoConseguimento;

	private java.lang.String strNomeIstituto;

	private java.lang.String strTematiche;

	private java.lang.String strTitoloCorso;

	public SapFormazione() {
	}

	public SapFormazione(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCorso deCorso,
			java.lang.Boolean flgCompletato, java.lang.Integer idSapFormazione, java.lang.Integer numAnnoConseguimento,
			java.lang.String strNomeIstituto, java.lang.String strTematiche, java.lang.String strTitoloCorso) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deCorso = deCorso;
		this.flgCompletato = flgCompletato;
		this.idSapFormazione = idSapFormazione;
		this.numAnnoConseguimento = numAnnoConseguimento;
		this.strNomeIstituto = strNomeIstituto;
		this.strTematiche = strTematiche;
		this.strTitoloCorso = strTitoloCorso;
	}

	/**
	 * Gets the deCorso value for this SapFormazione.
	 * 
	 * @return deCorso
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCorso getDeCorso() {
		return deCorso;
	}

	/**
	 * Sets the deCorso value for this SapFormazione.
	 * 
	 * @param deCorso
	 */
	public void setDeCorso(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCorso deCorso) {
		this.deCorso = deCorso;
	}

	/**
	 * Gets the flgCompletato value for this SapFormazione.
	 * 
	 * @return flgCompletato
	 */
	public java.lang.Boolean getFlgCompletato() {
		return flgCompletato;
	}

	/**
	 * Sets the flgCompletato value for this SapFormazione.
	 * 
	 * @param flgCompletato
	 */
	public void setFlgCompletato(java.lang.Boolean flgCompletato) {
		this.flgCompletato = flgCompletato;
	}

	/**
	 * Gets the idSapFormazione value for this SapFormazione.
	 * 
	 * @return idSapFormazione
	 */
	public java.lang.Integer getIdSapFormazione() {
		return idSapFormazione;
	}

	/**
	 * Sets the idSapFormazione value for this SapFormazione.
	 * 
	 * @param idSapFormazione
	 */
	public void setIdSapFormazione(java.lang.Integer idSapFormazione) {
		this.idSapFormazione = idSapFormazione;
	}

	/**
	 * Gets the numAnnoConseguimento value for this SapFormazione.
	 * 
	 * @return numAnnoConseguimento
	 */
	public java.lang.Integer getNumAnnoConseguimento() {
		return numAnnoConseguimento;
	}

	/**
	 * Sets the numAnnoConseguimento value for this SapFormazione.
	 * 
	 * @param numAnnoConseguimento
	 */
	public void setNumAnnoConseguimento(java.lang.Integer numAnnoConseguimento) {
		this.numAnnoConseguimento = numAnnoConseguimento;
	}

	/**
	 * Gets the strNomeIstituto value for this SapFormazione.
	 * 
	 * @return strNomeIstituto
	 */
	public java.lang.String getStrNomeIstituto() {
		return strNomeIstituto;
	}

	/**
	 * Sets the strNomeIstituto value for this SapFormazione.
	 * 
	 * @param strNomeIstituto
	 */
	public void setStrNomeIstituto(java.lang.String strNomeIstituto) {
		this.strNomeIstituto = strNomeIstituto;
	}

	/**
	 * Gets the strTematiche value for this SapFormazione.
	 * 
	 * @return strTematiche
	 */
	public java.lang.String getStrTematiche() {
		return strTematiche;
	}

	/**
	 * Sets the strTematiche value for this SapFormazione.
	 * 
	 * @param strTematiche
	 */
	public void setStrTematiche(java.lang.String strTematiche) {
		this.strTematiche = strTematiche;
	}

	/**
	 * Gets the strTitoloCorso value for this SapFormazione.
	 * 
	 * @return strTitoloCorso
	 */
	public java.lang.String getStrTitoloCorso() {
		return strTitoloCorso;
	}

	/**
	 * Sets the strTitoloCorso value for this SapFormazione.
	 * 
	 * @param strTitoloCorso
	 */
	public void setStrTitoloCorso(java.lang.String strTitoloCorso) {
		this.strTitoloCorso = strTitoloCorso;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapFormazione))
			return false;
		SapFormazione other = (SapFormazione) obj;
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
				&& ((this.deCorso == null && other.getDeCorso() == null)
						|| (this.deCorso != null && this.deCorso.equals(other.getDeCorso())))
				&& ((this.flgCompletato == null && other.getFlgCompletato() == null)
						|| (this.flgCompletato != null && this.flgCompletato.equals(other.getFlgCompletato())))
				&& ((this.idSapFormazione == null && other.getIdSapFormazione() == null)
						|| (this.idSapFormazione != null && this.idSapFormazione.equals(other.getIdSapFormazione())))
				&& ((this.numAnnoConseguimento == null && other.getNumAnnoConseguimento() == null)
						|| (this.numAnnoConseguimento != null
								&& this.numAnnoConseguimento.equals(other.getNumAnnoConseguimento())))
				&& ((this.strNomeIstituto == null && other.getStrNomeIstituto() == null)
						|| (this.strNomeIstituto != null && this.strNomeIstituto.equals(other.getStrNomeIstituto())))
				&& ((this.strTematiche == null && other.getStrTematiche() == null)
						|| (this.strTematiche != null && this.strTematiche.equals(other.getStrTematiche())))
				&& ((this.strTitoloCorso == null && other.getStrTitoloCorso() == null)
						|| (this.strTitoloCorso != null && this.strTitoloCorso.equals(other.getStrTitoloCorso())));
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
		if (getDeCorso() != null) {
			_hashCode += getDeCorso().hashCode();
		}
		if (getFlgCompletato() != null) {
			_hashCode += getFlgCompletato().hashCode();
		}
		if (getIdSapFormazione() != null) {
			_hashCode += getIdSapFormazione().hashCode();
		}
		if (getNumAnnoConseguimento() != null) {
			_hashCode += getNumAnnoConseguimento().hashCode();
		}
		if (getStrNomeIstituto() != null) {
			_hashCode += getStrNomeIstituto().hashCode();
		}
		if (getStrTematiche() != null) {
			_hashCode += getStrTematiche().hashCode();
		}
		if (getStrTitoloCorso() != null) {
			_hashCode += getStrTitoloCorso().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapFormazione.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapFormazione"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deCorso");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deCorso"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deCorso"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgCompletato");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgCompletato"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapFormazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapFormazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("numAnnoConseguimento");
		elemField.setXmlName(new javax.xml.namespace.QName("", "numAnnoConseguimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strNomeIstituto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strNomeIstituto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strTematiche");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strTematiche"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strTitoloCorso");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strTitoloCorso"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
