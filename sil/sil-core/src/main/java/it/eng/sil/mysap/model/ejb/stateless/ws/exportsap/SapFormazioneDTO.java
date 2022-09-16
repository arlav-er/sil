/**
 * SapFormazioneDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapFormazioneDTO implements java.io.Serializable {
	private java.lang.String codCorso;

	private java.lang.String codCorsoDesc;

	private java.lang.Boolean flgCompletato;

	private java.lang.Integer numAnnoConseguimento;

	private java.lang.Integer orderField;

	private java.lang.String strNomeIstituto;

	private java.lang.String strTematiche;

	private java.lang.String strTitoloCorso;

	public SapFormazioneDTO() {
	}

	public SapFormazioneDTO(java.lang.String codCorso, java.lang.String codCorsoDesc, java.lang.Boolean flgCompletato,
			java.lang.Integer numAnnoConseguimento, java.lang.Integer orderField, java.lang.String strNomeIstituto,
			java.lang.String strTematiche, java.lang.String strTitoloCorso) {
		this.codCorso = codCorso;
		this.codCorsoDesc = codCorsoDesc;
		this.flgCompletato = flgCompletato;
		this.numAnnoConseguimento = numAnnoConseguimento;
		this.orderField = orderField;
		this.strNomeIstituto = strNomeIstituto;
		this.strTematiche = strTematiche;
		this.strTitoloCorso = strTitoloCorso;
	}

	/**
	 * Gets the codCorso value for this SapFormazioneDTO.
	 * 
	 * @return codCorso
	 */
	public java.lang.String getCodCorso() {
		return codCorso;
	}

	/**
	 * Sets the codCorso value for this SapFormazioneDTO.
	 * 
	 * @param codCorso
	 */
	public void setCodCorso(java.lang.String codCorso) {
		this.codCorso = codCorso;
	}

	/**
	 * Gets the codCorsoDesc value for this SapFormazioneDTO.
	 * 
	 * @return codCorsoDesc
	 */
	public java.lang.String getCodCorsoDesc() {
		return codCorsoDesc;
	}

	/**
	 * Sets the codCorsoDesc value for this SapFormazioneDTO.
	 * 
	 * @param codCorsoDesc
	 */
	public void setCodCorsoDesc(java.lang.String codCorsoDesc) {
		this.codCorsoDesc = codCorsoDesc;
	}

	/**
	 * Gets the flgCompletato value for this SapFormazioneDTO.
	 * 
	 * @return flgCompletato
	 */
	public java.lang.Boolean getFlgCompletato() {
		return flgCompletato;
	}

	/**
	 * Sets the flgCompletato value for this SapFormazioneDTO.
	 * 
	 * @param flgCompletato
	 */
	public void setFlgCompletato(java.lang.Boolean flgCompletato) {
		this.flgCompletato = flgCompletato;
	}

	/**
	 * Gets the numAnnoConseguimento value for this SapFormazioneDTO.
	 * 
	 * @return numAnnoConseguimento
	 */
	public java.lang.Integer getNumAnnoConseguimento() {
		return numAnnoConseguimento;
	}

	/**
	 * Sets the numAnnoConseguimento value for this SapFormazioneDTO.
	 * 
	 * @param numAnnoConseguimento
	 */
	public void setNumAnnoConseguimento(java.lang.Integer numAnnoConseguimento) {
		this.numAnnoConseguimento = numAnnoConseguimento;
	}

	/**
	 * Gets the orderField value for this SapFormazioneDTO.
	 * 
	 * @return orderField
	 */
	public java.lang.Integer getOrderField() {
		return orderField;
	}

	/**
	 * Sets the orderField value for this SapFormazioneDTO.
	 * 
	 * @param orderField
	 */
	public void setOrderField(java.lang.Integer orderField) {
		this.orderField = orderField;
	}

	/**
	 * Gets the strNomeIstituto value for this SapFormazioneDTO.
	 * 
	 * @return strNomeIstituto
	 */
	public java.lang.String getStrNomeIstituto() {
		return strNomeIstituto;
	}

	/**
	 * Sets the strNomeIstituto value for this SapFormazioneDTO.
	 * 
	 * @param strNomeIstituto
	 */
	public void setStrNomeIstituto(java.lang.String strNomeIstituto) {
		this.strNomeIstituto = strNomeIstituto;
	}

	/**
	 * Gets the strTematiche value for this SapFormazioneDTO.
	 * 
	 * @return strTematiche
	 */
	public java.lang.String getStrTematiche() {
		return strTematiche;
	}

	/**
	 * Sets the strTematiche value for this SapFormazioneDTO.
	 * 
	 * @param strTematiche
	 */
	public void setStrTematiche(java.lang.String strTematiche) {
		this.strTematiche = strTematiche;
	}

	/**
	 * Gets the strTitoloCorso value for this SapFormazioneDTO.
	 * 
	 * @return strTitoloCorso
	 */
	public java.lang.String getStrTitoloCorso() {
		return strTitoloCorso;
	}

	/**
	 * Sets the strTitoloCorso value for this SapFormazioneDTO.
	 * 
	 * @param strTitoloCorso
	 */
	public void setStrTitoloCorso(java.lang.String strTitoloCorso) {
		this.strTitoloCorso = strTitoloCorso;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapFormazioneDTO))
			return false;
		SapFormazioneDTO other = (SapFormazioneDTO) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.codCorso == null && other.getCodCorso() == null)
						|| (this.codCorso != null && this.codCorso.equals(other.getCodCorso())))
				&& ((this.codCorsoDesc == null && other.getCodCorsoDesc() == null)
						|| (this.codCorsoDesc != null && this.codCorsoDesc.equals(other.getCodCorsoDesc())))
				&& ((this.flgCompletato == null && other.getFlgCompletato() == null)
						|| (this.flgCompletato != null && this.flgCompletato.equals(other.getFlgCompletato())))
				&& ((this.numAnnoConseguimento == null && other.getNumAnnoConseguimento() == null)
						|| (this.numAnnoConseguimento != null
								&& this.numAnnoConseguimento.equals(other.getNumAnnoConseguimento())))
				&& ((this.orderField == null && other.getOrderField() == null)
						|| (this.orderField != null && this.orderField.equals(other.getOrderField())))
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
		int _hashCode = 1;
		if (getCodCorso() != null) {
			_hashCode += getCodCorso().hashCode();
		}
		if (getCodCorsoDesc() != null) {
			_hashCode += getCodCorsoDesc().hashCode();
		}
		if (getFlgCompletato() != null) {
			_hashCode += getFlgCompletato().hashCode();
		}
		if (getNumAnnoConseguimento() != null) {
			_hashCode += getNumAnnoConseguimento().hashCode();
		}
		if (getOrderField() != null) {
			_hashCode += getOrderField().hashCode();
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
			SapFormazioneDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapFormazioneDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codCorso");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codCorso"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codCorsoDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codCorsoDesc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
		elemField.setFieldName("numAnnoConseguimento");
		elemField.setXmlName(new javax.xml.namespace.QName("", "numAnnoConseguimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("orderField");
		elemField.setXmlName(new javax.xml.namespace.QName("", "orderField"));
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
