/**
 * SapDisponibilitaProvinciaDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaProvinciaDTO implements java.io.Serializable {
	private java.lang.String codProvincia;

	private java.lang.String codProvinciaDesc;

	public SapDisponibilitaProvinciaDTO() {
	}

	public SapDisponibilitaProvinciaDTO(java.lang.String codProvincia, java.lang.String codProvinciaDesc) {
		this.codProvincia = codProvincia;
		this.codProvinciaDesc = codProvinciaDesc;
	}

	/**
	 * Gets the codProvincia value for this SapDisponibilitaProvinciaDTO.
	 * 
	 * @return codProvincia
	 */
	public java.lang.String getCodProvincia() {
		return codProvincia;
	}

	/**
	 * Sets the codProvincia value for this SapDisponibilitaProvinciaDTO.
	 * 
	 * @param codProvincia
	 */
	public void setCodProvincia(java.lang.String codProvincia) {
		this.codProvincia = codProvincia;
	}

	/**
	 * Gets the codProvinciaDesc value for this SapDisponibilitaProvinciaDTO.
	 * 
	 * @return codProvinciaDesc
	 */
	public java.lang.String getCodProvinciaDesc() {
		return codProvinciaDesc;
	}

	/**
	 * Sets the codProvinciaDesc value for this SapDisponibilitaProvinciaDTO.
	 * 
	 * @param codProvinciaDesc
	 */
	public void setCodProvinciaDesc(java.lang.String codProvinciaDesc) {
		this.codProvinciaDesc = codProvinciaDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaProvinciaDTO))
			return false;
		SapDisponibilitaProvinciaDTO other = (SapDisponibilitaProvinciaDTO) obj;
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
				&& ((this.codProvincia == null && other.getCodProvincia() == null)
						|| (this.codProvincia != null && this.codProvincia.equals(other.getCodProvincia())))
				&& ((this.codProvinciaDesc == null && other.getCodProvinciaDesc() == null)
						|| (this.codProvinciaDesc != null
								&& this.codProvinciaDesc.equals(other.getCodProvinciaDesc())));
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
		if (getCodProvincia() != null) {
			_hashCode += getCodProvincia().hashCode();
		}
		if (getCodProvinciaDesc() != null) {
			_hashCode += getCodProvinciaDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaProvinciaDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaProvinciaDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codProvincia");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codProvincia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codProvinciaDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codProvinciaDesc"));
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
