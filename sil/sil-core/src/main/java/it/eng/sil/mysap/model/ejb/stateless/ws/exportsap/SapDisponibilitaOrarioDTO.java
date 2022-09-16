/**
 * SapDisponibilitaOrarioDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaOrarioDTO implements java.io.Serializable {
	private java.lang.String codOrario;

	private java.lang.String codOrarioDesc;

	public SapDisponibilitaOrarioDTO() {
	}

	public SapDisponibilitaOrarioDTO(java.lang.String codOrario, java.lang.String codOrarioDesc) {
		this.codOrario = codOrario;
		this.codOrarioDesc = codOrarioDesc;
	}

	/**
	 * Gets the codOrario value for this SapDisponibilitaOrarioDTO.
	 * 
	 * @return codOrario
	 */
	public java.lang.String getCodOrario() {
		return codOrario;
	}

	/**
	 * Sets the codOrario value for this SapDisponibilitaOrarioDTO.
	 * 
	 * @param codOrario
	 */
	public void setCodOrario(java.lang.String codOrario) {
		this.codOrario = codOrario;
	}

	/**
	 * Gets the codOrarioDesc value for this SapDisponibilitaOrarioDTO.
	 * 
	 * @return codOrarioDesc
	 */
	public java.lang.String getCodOrarioDesc() {
		return codOrarioDesc;
	}

	/**
	 * Sets the codOrarioDesc value for this SapDisponibilitaOrarioDTO.
	 * 
	 * @param codOrarioDesc
	 */
	public void setCodOrarioDesc(java.lang.String codOrarioDesc) {
		this.codOrarioDesc = codOrarioDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaOrarioDTO))
			return false;
		SapDisponibilitaOrarioDTO other = (SapDisponibilitaOrarioDTO) obj;
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
				&& ((this.codOrario == null && other.getCodOrario() == null)
						|| (this.codOrario != null && this.codOrario.equals(other.getCodOrario())))
				&& ((this.codOrarioDesc == null && other.getCodOrarioDesc() == null)
						|| (this.codOrarioDesc != null && this.codOrarioDesc.equals(other.getCodOrarioDesc())));
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
		if (getCodOrario() != null) {
			_hashCode += getCodOrario().hashCode();
		}
		if (getCodOrarioDesc() != null) {
			_hashCode += getCodOrarioDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaOrarioDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaOrarioDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codOrario"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codOrarioDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codOrarioDesc"));
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
