/**
 * ConditionEnum.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class ConditionEnum implements java.io.Serializable {
	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	// Constructor
	protected ConditionEnum(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _EQUALS = "EQUALS";
	public static final java.lang.String _GREATER_THAN = "GREATER_THAN";
	public static final java.lang.String _LESS_THAN = "LESS_THAN";
	public static final java.lang.String _NOT_EQUALS = "NOT_EQUALS";
	public static final java.lang.String _LIKE = "LIKE";
	public static final java.lang.String _GREATER_THAN_EQUALS = "GREATER_THAN_EQUALS";
	public static final java.lang.String _LESS_THAN_EQUALS = "LESS_THAN_EQUALS";
	public static final java.lang.String _BEGINS_WITH = "BEGINS_WITH";
	public static final java.lang.String _ENDS_WITH = "ENDS_WITH";
	public static final java.lang.String _CONTAINS = "CONTAINS";
	public static final ConditionEnum EQUALS = new ConditionEnum(_EQUALS);
	public static final ConditionEnum GREATER_THAN = new ConditionEnum(_GREATER_THAN);
	public static final ConditionEnum LESS_THAN = new ConditionEnum(_LESS_THAN);
	public static final ConditionEnum NOT_EQUALS = new ConditionEnum(_NOT_EQUALS);
	public static final ConditionEnum LIKE = new ConditionEnum(_LIKE);
	public static final ConditionEnum GREATER_THAN_EQUALS = new ConditionEnum(_GREATER_THAN_EQUALS);
	public static final ConditionEnum LESS_THAN_EQUALS = new ConditionEnum(_LESS_THAN_EQUALS);
	public static final ConditionEnum BEGINS_WITH = new ConditionEnum(_BEGINS_WITH);
	public static final ConditionEnum ENDS_WITH = new ConditionEnum(_ENDS_WITH);
	public static final ConditionEnum CONTAINS = new ConditionEnum(_CONTAINS);

	public java.lang.String getValue() {
		return _value_;
	}

	public static ConditionEnum fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
		ConditionEnum enumeration = (ConditionEnum) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static ConditionEnum fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
			ConditionEnum.class);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "ConditionEnum"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
