/**
 * Cod_evento.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.EventiCondizionalitaRDC.types;

public class Cod_evento implements java.io.Serializable {
	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	// Constructor
	protected Cod_evento(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _X01 = "X01";
	public static final java.lang.String _X02 = "X02";
	public static final java.lang.String _X03 = "X03";
	public static final java.lang.String _X04 = "X04";
	public static final java.lang.String _X05 = "X05";
	public static final java.lang.String _X06 = "X06";
	public static final java.lang.String _X07 = "X07";
	public static final java.lang.String _X08 = "X08";
	public static final java.lang.String _X09 = "X09";
	public static final java.lang.String _X10 = "X10";
	public static final java.lang.String _X11 = "X11";
	public static final java.lang.String _X12 = "X12";
	public static final java.lang.String _X13 = "X13";
	public static final Cod_evento X01 = new Cod_evento(_X01);
	public static final Cod_evento X02 = new Cod_evento(_X02);
	public static final Cod_evento X03 = new Cod_evento(_X03);
	public static final Cod_evento X04 = new Cod_evento(_X04);
	public static final Cod_evento X05 = new Cod_evento(_X05);
	public static final Cod_evento X06 = new Cod_evento(_X06);
	public static final Cod_evento X07 = new Cod_evento(_X07);
	public static final Cod_evento X08 = new Cod_evento(_X08);
	public static final Cod_evento X09 = new Cod_evento(_X09);
	public static final Cod_evento X10 = new Cod_evento(_X10);
	public static final Cod_evento X11 = new Cod_evento(_X11);
	public static final Cod_evento X12 = new Cod_evento(_X12);
	public static final Cod_evento X13 = new Cod_evento(_X13);

	public java.lang.String getValue() {
		return _value_;
	}

	public static Cod_evento fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
		Cod_evento enumeration = (Cod_evento) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static Cod_evento fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
			Cod_evento.class);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
				"cod_evento"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
