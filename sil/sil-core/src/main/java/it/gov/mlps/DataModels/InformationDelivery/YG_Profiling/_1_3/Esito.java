/**
 * Esito.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_3;

public class Esito implements java.io.Serializable {
	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	// Constructor
	protected Esito(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _E01 = "E01";
	public static final java.lang.String _E02 = "E02";
	public static final java.lang.String _E03 = "E03";
	public static final java.lang.String _E04 = "E04";
	public static final java.lang.String _E05 = "E05";
	public static final java.lang.String _E06 = "E06";
	public static final java.lang.String _E07 = "E07";
	public static final java.lang.String _E08 = "E08";
	public static final java.lang.String _E09 = "E09";
	public static final java.lang.String _E10 = "E10";
	public static final Esito E01 = new Esito(_E01);
	public static final Esito E02 = new Esito(_E02);
	public static final Esito E03 = new Esito(_E03);
	public static final Esito E04 = new Esito(_E04);
	public static final Esito E05 = new Esito(_E05);
	public static final Esito E06 = new Esito(_E06);
	public static final Esito E07 = new Esito(_E07);
	public static final Esito E08 = new Esito(_E08);
	public static final Esito E09 = new Esito(_E09);
	public static final Esito E10 = new Esito(_E10);

	public java.lang.String getValue() {
		return _value_;
	}

	public static Esito fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
		Esito enumeration = (Esito) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static Esito fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
			Esito.class);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.3", "esito"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
