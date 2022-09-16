/**
 * XmlResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.seta.ws;

public class XmlResult implements java.io.Serializable {
	private boolean check;

	private java.lang.String code;

	private java.lang.String result;

	public XmlResult() {
	}

	public XmlResult(boolean check, java.lang.String code, java.lang.String result) {
		this.check = check;
		this.code = code;
		this.result = result;
	}

	/**
	 * Gets the check value for this XmlResult.
	 * 
	 * @return check
	 */
	public boolean isCheck() {
		return check;
	}

	/**
	 * Sets the check value for this XmlResult.
	 * 
	 * @param check
	 */
	public void setCheck(boolean check) {
		this.check = check;
	}

	/**
	 * Gets the code value for this XmlResult.
	 * 
	 * @return code
	 */
	public java.lang.String getCode() {
		return code;
	}

	/**
	 * Sets the code value for this XmlResult.
	 * 
	 * @param code
	 */
	public void setCode(java.lang.String code) {
		this.code = code;
	}

	/**
	 * Gets the result value for this XmlResult.
	 * 
	 * @return result
	 */
	public java.lang.String getResult() {
		return result;
	}

	/**
	 * Sets the result value for this XmlResult.
	 * 
	 * @param result
	 */
	public void setResult(java.lang.String result) {
		this.result = result;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof XmlResult))
			return false;
		XmlResult other = (XmlResult) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.check == other.isCheck()
				&& ((this.code == null && other.getCode() == null)
						|| (this.code != null && this.code.equals(other.getCode())))
				&& ((this.result == null && other.getResult() == null)
						|| (this.result != null && this.result.equals(other.getResult())));
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
		_hashCode += (isCheck() ? Boolean.TRUE : Boolean.FALSE).hashCode();
		if (getCode() != null) {
			_hashCode += getCode().hashCode();
		}
		if (getResult() != null) {
			_hashCode += getResult().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			XmlResult.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.seta.com/", "xmlResult"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("check");
		elemField.setXmlName(new javax.xml.namespace.QName("", "check"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("code");
		elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("result");
		elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
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
