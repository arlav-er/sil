/**
 * SapAlboDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapAlboDTO implements java.io.Serializable {
	private java.lang.String codAlbo;

	private java.lang.String codAlboDesc;

	public SapAlboDTO() {
	}

	public SapAlboDTO(java.lang.String codAlbo, java.lang.String codAlboDesc) {
		this.codAlbo = codAlbo;
		this.codAlboDesc = codAlboDesc;
	}

	/**
	 * Gets the codAlbo value for this SapAlboDTO.
	 * 
	 * @return codAlbo
	 */
	public java.lang.String getCodAlbo() {
		return codAlbo;
	}

	/**
	 * Sets the codAlbo value for this SapAlboDTO.
	 * 
	 * @param codAlbo
	 */
	public void setCodAlbo(java.lang.String codAlbo) {
		this.codAlbo = codAlbo;
	}

	/**
	 * Gets the codAlboDesc value for this SapAlboDTO.
	 * 
	 * @return codAlboDesc
	 */
	public java.lang.String getCodAlboDesc() {
		return codAlboDesc;
	}

	/**
	 * Sets the codAlboDesc value for this SapAlboDTO.
	 * 
	 * @param codAlboDesc
	 */
	public void setCodAlboDesc(java.lang.String codAlboDesc) {
		this.codAlboDesc = codAlboDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapAlboDTO))
			return false;
		SapAlboDTO other = (SapAlboDTO) obj;
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
				&& ((this.codAlbo == null && other.getCodAlbo() == null)
						|| (this.codAlbo != null && this.codAlbo.equals(other.getCodAlbo())))
				&& ((this.codAlboDesc == null && other.getCodAlboDesc() == null)
						|| (this.codAlboDesc != null && this.codAlboDesc.equals(other.getCodAlboDesc())));
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
		if (getCodAlbo() != null) {
			_hashCode += getCodAlbo().hashCode();
		}
		if (getCodAlboDesc() != null) {
			_hashCode += getCodAlboDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapAlboDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapAlboDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codAlbo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codAlbo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codAlboDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codAlboDesc"));
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
