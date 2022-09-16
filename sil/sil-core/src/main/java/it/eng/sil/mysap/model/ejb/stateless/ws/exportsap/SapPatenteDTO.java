/**
 * SapPatenteDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPatenteDTO implements java.io.Serializable {
	private java.lang.String codPatente;

	private java.lang.String codPatenteDesc;

	public SapPatenteDTO() {
	}

	public SapPatenteDTO(java.lang.String codPatente, java.lang.String codPatenteDesc) {
		this.codPatente = codPatente;
		this.codPatenteDesc = codPatenteDesc;
	}

	/**
	 * Gets the codPatente value for this SapPatenteDTO.
	 * 
	 * @return codPatente
	 */
	public java.lang.String getCodPatente() {
		return codPatente;
	}

	/**
	 * Sets the codPatente value for this SapPatenteDTO.
	 * 
	 * @param codPatente
	 */
	public void setCodPatente(java.lang.String codPatente) {
		this.codPatente = codPatente;
	}

	/**
	 * Gets the codPatenteDesc value for this SapPatenteDTO.
	 * 
	 * @return codPatenteDesc
	 */
	public java.lang.String getCodPatenteDesc() {
		return codPatenteDesc;
	}

	/**
	 * Sets the codPatenteDesc value for this SapPatenteDTO.
	 * 
	 * @param codPatenteDesc
	 */
	public void setCodPatenteDesc(java.lang.String codPatenteDesc) {
		this.codPatenteDesc = codPatenteDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPatenteDTO))
			return false;
		SapPatenteDTO other = (SapPatenteDTO) obj;
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
				&& ((this.codPatente == null && other.getCodPatente() == null)
						|| (this.codPatente != null && this.codPatente.equals(other.getCodPatente())))
				&& ((this.codPatenteDesc == null && other.getCodPatenteDesc() == null)
						|| (this.codPatenteDesc != null && this.codPatenteDesc.equals(other.getCodPatenteDesc())));
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
		if (getCodPatente() != null) {
			_hashCode += getCodPatente().hashCode();
		}
		if (getCodPatenteDesc() != null) {
			_hashCode += getCodPatenteDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPatenteDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatenteDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPatente");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codPatente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPatenteDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codPatenteDesc"));
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
