/**
 * DATE.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public class DATE implements java.io.Serializable {
	private java.util.Calendar date;

	private java.util.Calendar calendar;

	public DATE() {
	}

	public DATE(java.util.Calendar date, java.util.Calendar calendar) {
		this.date = date;
		this.calendar = calendar;
	}

	/**
	 * Gets the date value for this DATE.
	 * 
	 * @return date
	 */
	public java.util.Calendar getDate() {
		return date;
	}

	/**
	 * Sets the date value for this DATE.
	 * 
	 * @param date
	 */
	public void setDate(java.util.Calendar date) {
		this.date = date;
	}

	/**
	 * Gets the calendar value for this DATE.
	 * 
	 * @return calendar
	 */
	public java.util.Calendar getCalendar() {
		return calendar;
	}

	/**
	 * Sets the calendar value for this DATE.
	 * 
	 * @param calendar
	 */
	public void setCalendar(java.util.Calendar calendar) {
		this.calendar = calendar;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DATE))
			return false;
		DATE other = (DATE) obj;
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
				&& ((this.date == null && other.getDate() == null)
						|| (this.date != null && this.date.equals(other.getDate())))
				&& ((this.calendar == null && other.getCalendar() == null)
						|| (this.calendar != null && this.calendar.equals(other.getCalendar())));
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
		if (getDate() != null) {
			_hashCode += getDate().hashCode();
		}
		if (getCalendar() != null) {
			_hashCode += getCalendar().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(DATE.class,
			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://adobe.com/idp/services", "DATE"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("date");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adobe.com/idp/services", "date"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("calendar");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adobe.com/idp/services", "calendar"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
