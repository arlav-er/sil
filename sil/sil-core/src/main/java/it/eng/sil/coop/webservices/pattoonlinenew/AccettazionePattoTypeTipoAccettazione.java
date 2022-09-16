/**
 * AccettazionePattoTypeTipoAccettazione.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.pattoonlinenew;

public class AccettazionePattoTypeTipoAccettazione implements java.io.Serializable {
	private java.lang.String _value_;
	private static java.util.HashMap _table_ = new java.util.HashMap();

	// Constructor
	protected AccettazionePattoTypeTipoAccettazione(java.lang.String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final java.lang.String _OTP = "OTP";
	public static final java.lang.String _SPID = "SPID";
	public static final java.lang.String _CIE = "CIE";
	public static final java.lang.String _RV = "RV";
	public static final java.lang.String _SMS = "SMS";
	public static final AccettazionePattoTypeTipoAccettazione OTP = new AccettazionePattoTypeTipoAccettazione(_OTP);
	public static final AccettazionePattoTypeTipoAccettazione SPID = new AccettazionePattoTypeTipoAccettazione(_SPID);
	public static final AccettazionePattoTypeTipoAccettazione CIE = new AccettazionePattoTypeTipoAccettazione(_CIE);
	public static final AccettazionePattoTypeTipoAccettazione RV = new AccettazionePattoTypeTipoAccettazione(_RV);
	public static final AccettazionePattoTypeTipoAccettazione SMS = new AccettazionePattoTypeTipoAccettazione(_SMS);

	public java.lang.String getValue() {
		return _value_;
	}

	public static AccettazionePattoTypeTipoAccettazione fromValue(java.lang.String value)
			throws java.lang.IllegalArgumentException {
		AccettazionePattoTypeTipoAccettazione enumeration = (AccettazionePattoTypeTipoAccettazione) _table_.get(value);
		if (enumeration == null)
			throw new java.lang.IllegalArgumentException();
		return enumeration;
	}

	public static AccettazionePattoTypeTipoAccettazione fromString(java.lang.String value)
			throws java.lang.IllegalArgumentException {
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
			AccettazionePattoTypeTipoAccettazione.class);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://pattoonlinenew.webservices.coop.sil.eng.it",
				">accettazionePattoType>tipoAccettazione"));
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
