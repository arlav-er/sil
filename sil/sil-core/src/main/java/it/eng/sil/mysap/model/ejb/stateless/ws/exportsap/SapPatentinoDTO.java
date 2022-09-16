/**
 * SapPatentinoDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPatentinoDTO implements java.io.Serializable {
	private java.lang.String codPatentino;

	private java.lang.String codPatentinoDesc;

	public SapPatentinoDTO() {
	}

	public SapPatentinoDTO(java.lang.String codPatentino, java.lang.String codPatentinoDesc) {
		this.codPatentino = codPatentino;
		this.codPatentinoDesc = codPatentinoDesc;
	}

	/**
	 * Gets the codPatentino value for this SapPatentinoDTO.
	 * 
	 * @return codPatentino
	 */
	public java.lang.String getCodPatentino() {
		return codPatentino;
	}

	/**
	 * Sets the codPatentino value for this SapPatentinoDTO.
	 * 
	 * @param codPatentino
	 */
	public void setCodPatentino(java.lang.String codPatentino) {
		this.codPatentino = codPatentino;
	}

	/**
	 * Gets the codPatentinoDesc value for this SapPatentinoDTO.
	 * 
	 * @return codPatentinoDesc
	 */
	public java.lang.String getCodPatentinoDesc() {
		return codPatentinoDesc;
	}

	/**
	 * Sets the codPatentinoDesc value for this SapPatentinoDTO.
	 * 
	 * @param codPatentinoDesc
	 */
	public void setCodPatentinoDesc(java.lang.String codPatentinoDesc) {
		this.codPatentinoDesc = codPatentinoDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPatentinoDTO))
			return false;
		SapPatentinoDTO other = (SapPatentinoDTO) obj;
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
				&& ((this.codPatentino == null && other.getCodPatentino() == null)
						|| (this.codPatentino != null && this.codPatentino.equals(other.getCodPatentino())))
				&& ((this.codPatentinoDesc == null && other.getCodPatentinoDesc() == null)
						|| (this.codPatentinoDesc != null
								&& this.codPatentinoDesc.equals(other.getCodPatentinoDesc())));
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
		if (getCodPatentino() != null) {
			_hashCode += getCodPatentino().hashCode();
		}
		if (getCodPatentinoDesc() != null) {
			_hashCode += getCodPatentinoDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPatentinoDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatentinoDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPatentino");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codPatentino"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPatentinoDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codPatentinoDesc"));
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
