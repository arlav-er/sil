/**
 * CodiceFiscale.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0;

public class CodiceFiscale implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
	private static final long serialVersionUID = -5527699185728495709L;

	private java.lang.String _value;

	public CodiceFiscale() {
	}

	// Simple Types must have a String constructor
	public CodiceFiscale(java.lang.String _value) {
		this._value = _value;
	}

	// Simple Types must have a toString for serializing the value
	public java.lang.String toString() {
		return _value;
	}

	/**
	 * Gets the codiceFiscaleClassicoValue value for this CodiceFiscale.
	 * 
	 * @return codiceFiscaleClassicoValue
	 */
	public java.lang.String getCodiceFiscaleClassicoValue() {
		return new java.lang.String(_value);
	}

	/**
	 * Sets the _value value for this CodiceFiscale.
	 * 
	 * @param _value
	 */
	public void setCodiceFiscaleClassicoValue(java.lang.String _value) {
		this._value = _value == null ? null : _value.toString();
	}

	/**
	 * Gets the codiceFiscaleProvvisorioValue value for this CodiceFiscale.
	 * 
	 * @return codiceFiscaleProvvisorioValue
	 */
	public java.lang.String getCodiceFiscaleProvvisorioValue() {
		return new java.lang.String(_value);
	}

	/**
	 * Sets the _value value for this CodiceFiscale.
	 * 
	 * @param _value
	 */
	public void setCodiceFiscaleProvvisorioValue(java.lang.String _value) {
		this._value = _value == null ? null : _value.toString();
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CodiceFiscale))
			return false;
		CodiceFiscale other = (CodiceFiscale) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.toString().equals(obj.toString());
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
		if (this._value != null) {
			_hashCode += this._value.hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			CodiceFiscale.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "CodiceFiscale"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscaleClassicoValue");
		elemField.setXmlName(new javax.xml.namespace.QName("", "CodiceFiscaleClassicoValue"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscaleProvvisorioValue");
		elemField.setXmlName(new javax.xml.namespace.QName("", "CodiceFiscaleProvvisorioValue"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
		return new org.apache.axis.encoding.ser.SimpleSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.SimpleDeserializer(_javaType, _xmlType, typeDesc);
	}

}
