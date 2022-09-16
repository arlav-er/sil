/**
 * Applicazione.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0;

public class Applicazione implements java.io.Serializable {
	private static final long serialVersionUID = 571987220158226478L;

	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	public Applicazione() {
		_value_ = "NCN";
		_table_.put(_value_, this);
	}

	// Constructor
	protected Applicazione(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _NCN = "NCN";
	public static final java.lang.String _BAT = "BAT";
	public static final java.lang.String _WEB = "WEB";
	public static final Applicazione NCN = new Applicazione(_NCN);
	public static final Applicazione BAT = new Applicazione(_BAT);
	public static final Applicazione WEB = new Applicazione(_WEB);

	public java.lang.String getValue() {
		return _value_;
	}

	public static Applicazione fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
		Applicazione enumeration = (Applicazione) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static Applicazione fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
			Applicazione.class);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Applicazione"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
