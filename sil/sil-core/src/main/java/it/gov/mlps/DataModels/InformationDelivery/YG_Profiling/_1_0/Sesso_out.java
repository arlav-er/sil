/**
 * Sesso_out.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0;

public class Sesso_out implements java.io.Serializable {
	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	// Constructor
	protected Sesso_out(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _value1 = "M";
	public static final java.lang.String _value2 = "F";
	public static final java.lang.String _value3 = "";
	public static final Sesso_out value1 = new Sesso_out(_value1);
	public static final Sesso_out value2 = new Sesso_out(_value2);
	public static final Sesso_out value3 = new Sesso_out(_value3);

	public java.lang.String getValue() {
		return _value_;
	}

	public static Sesso_out fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
		Sesso_out enumeration = (Sesso_out) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static Sesso_out fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
		return fromValue(value);
	}

	public boolean equals(java.lang.Object obj) {
		return (obj == this);
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public java.lang.String toString() {
		return _value_;
	}

	public java.lang.Object readResolve() throws java.io.ObjectStreamException {
		return fromValue(_value_);
	}

	public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.EnumSerializer(_javaType, _xmlType);
	}

	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.EnumDeserializer(_javaType, _xmlType);
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Sesso_out.class);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "sesso_out"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
