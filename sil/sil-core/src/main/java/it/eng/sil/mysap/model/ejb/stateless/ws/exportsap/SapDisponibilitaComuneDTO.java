/**
 * SapDisponibilitaComuneDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaComuneDTO implements java.io.Serializable {
	private java.lang.String codComune;

	private java.lang.String codComuneDesc;

	public SapDisponibilitaComuneDTO() {
	}

	public SapDisponibilitaComuneDTO(java.lang.String codComune, java.lang.String codComuneDesc) {
		this.codComune = codComune;
		this.codComuneDesc = codComuneDesc;
	}

	/**
	 * Gets the codComune value for this SapDisponibilitaComuneDTO.
	 * 
	 * @return codComune
	 */
	public java.lang.String getCodComune() {
		return codComune;
	}

	/**
	 * Sets the codComune value for this SapDisponibilitaComuneDTO.
	 * 
	 * @param codComune
	 */
	public void setCodComune(java.lang.String codComune) {
		this.codComune = codComune;
	}

	/**
	 * Gets the codComuneDesc value for this SapDisponibilitaComuneDTO.
	 * 
	 * @return codComuneDesc
	 */
	public java.lang.String getCodComuneDesc() {
		return codComuneDesc;
	}

	/**
	 * Sets the codComuneDesc value for this SapDisponibilitaComuneDTO.
	 * 
	 * @param codComuneDesc
	 */
	public void setCodComuneDesc(java.lang.String codComuneDesc) {
		this.codComuneDesc = codComuneDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaComuneDTO))
			return false;
		SapDisponibilitaComuneDTO other = (SapDisponibilitaComuneDTO) obj;
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
				&& ((this.codComune == null && other.getCodComune() == null)
						|| (this.codComune != null && this.codComune.equals(other.getCodComune())))
				&& ((this.codComuneDesc == null && other.getCodComuneDesc() == null)
						|| (this.codComuneDesc != null && this.codComuneDesc.equals(other.getCodComuneDesc())));
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
		if (getCodComune() != null) {
			_hashCode += getCodComune().hashCode();
		}
		if (getCodComuneDesc() != null) {
			_hashCode += getCodComuneDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaComuneDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaComuneDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComune");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codComune"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComuneDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codComuneDesc"));
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
