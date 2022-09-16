/**
 * TipoEvento.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0;

public class TipoEvento implements java.io.Serializable {
	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	// Constructor
	protected TipoEvento(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _I = "I";
	public static final java.lang.String _C = "C";
	public static final java.lang.String _S = "S";
	public static final java.lang.String _R = "R";
	public static final java.lang.String _A = "A";
	public static final TipoEvento I = new TipoEvento(_I);
	public static final TipoEvento C = new TipoEvento(_C);
	public static final TipoEvento S = new TipoEvento(_S);
	public static final TipoEvento R = new TipoEvento(_R);
	public static final TipoEvento A = new TipoEvento(_A);

	public java.lang.String getValue() {
		return _value_;
	}

	public static TipoEvento fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
		TipoEvento enumeration = (TipoEvento) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static TipoEvento fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
			TipoEvento.class);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://anpal.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "TipoEvento"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
