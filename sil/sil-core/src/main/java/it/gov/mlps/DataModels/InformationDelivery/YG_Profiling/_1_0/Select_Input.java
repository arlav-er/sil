/**
 * Select_Input.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0;

public class Select_Input implements java.io.Serializable {
	private java.lang.String cf;

	private java.lang.String provincia;

	public Select_Input() {
	}

	public Select_Input(java.lang.String cf, java.lang.String provincia) {
		this.cf = cf;
		this.provincia = provincia;
	}

	/**
	 * Gets the cf value for this Select_Input.
	 * 
	 * @return cf
	 */
	public java.lang.String getCf() {
		return cf;
	}

	/**
	 * Sets the cf value for this Select_Input.
	 * 
	 * @param cf
	 */
	public void setCf(java.lang.String cf) {
		this.cf = cf;
	}

	/**
	 * Gets the provincia value for this Select_Input.
	 * 
	 * @return provincia
	 */
	public java.lang.String getProvincia() {
		return provincia;
	}

	/**
	 * Sets the provincia value for this Select_Input.
	 * 
	 * @param provincia
	 */
	public void setProvincia(java.lang.String provincia) {
		this.provincia = provincia;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Select_Input))
			return false;
		Select_Input other = (Select_Input) obj;
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
				&& ((this.cf == null && other.getCf() == null) || (this.cf != null && this.cf.equals(other.getCf())))
				&& ((this.provincia == null && other.getProvincia() == null)
						|| (this.provincia != null && this.provincia.equals(other.getProvincia())));
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
		if (getCf() != null) {
			_hashCode += getCf().hashCode();
		}
		if (getProvincia() != null) {
			_hashCode += getProvincia().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Select_Input.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "Select_Input"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cf");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "cf"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("provincia");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "provincia"));
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
