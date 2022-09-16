/**
 * SetValueMapping.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class SetValueMapping implements java.io.Serializable {
	private java.lang.String locationExpr;

	private java.lang.String valueExpr;

	public SetValueMapping() {
	}

	public SetValueMapping(java.lang.String locationExpr, java.lang.String valueExpr) {
		this.locationExpr = locationExpr;
		this.valueExpr = valueExpr;
	}

	/**
	 * Gets the locationExpr value for this SetValueMapping.
	 * 
	 * @return locationExpr
	 */
	public java.lang.String getLocationExpr() {
		return locationExpr;
	}

	/**
	 * Sets the locationExpr value for this SetValueMapping.
	 * 
	 * @param locationExpr
	 */
	public void setLocationExpr(java.lang.String locationExpr) {
		this.locationExpr = locationExpr;
	}

	/**
	 * Gets the valueExpr value for this SetValueMapping.
	 * 
	 * @return valueExpr
	 */
	public java.lang.String getValueExpr() {
		return valueExpr;
	}

	/**
	 * Sets the valueExpr value for this SetValueMapping.
	 * 
	 * @param valueExpr
	 */
	public void setValueExpr(java.lang.String valueExpr) {
		this.valueExpr = valueExpr;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SetValueMapping))
			return false;
		SetValueMapping other = (SetValueMapping) obj;
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
				&& ((this.locationExpr == null && other.getLocationExpr() == null)
						|| (this.locationExpr != null && this.locationExpr.equals(other.getLocationExpr())))
				&& ((this.valueExpr == null && other.getValueExpr() == null)
						|| (this.valueExpr != null && this.valueExpr.equals(other.getValueExpr())));
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
		if (getLocationExpr() != null) {
			_hashCode += getLocationExpr().hashCode();
		}
		if (getValueExpr() != null) {
			_hashCode += getValueExpr().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SetValueMapping.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"SetValueMapping"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("locationExpr");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "locationExpr"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("valueExpr");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "valueExpr"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
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
