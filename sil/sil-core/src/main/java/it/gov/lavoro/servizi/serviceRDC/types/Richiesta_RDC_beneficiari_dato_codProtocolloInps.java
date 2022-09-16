/**
 * Richiesta_RDC_beneficiari_dato_codProtocolloInps.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.serviceRDC.types;

public class Richiesta_RDC_beneficiari_dato_codProtocolloInps implements java.io.Serializable {
	private java.lang.String codProtocolloInps;

	public Richiesta_RDC_beneficiari_dato_codProtocolloInps() {
	}

	public Richiesta_RDC_beneficiari_dato_codProtocolloInps(java.lang.String codProtocolloInps) {
		this.codProtocolloInps = codProtocolloInps;
	}

	/**
	 * Gets the codProtocolloInps value for this Richiesta_RDC_beneficiari_dato_codProtocolloInps.
	 * 
	 * @return codProtocolloInps
	 */
	public java.lang.String getCodProtocolloInps() {
		return codProtocolloInps;
	}

	/**
	 * Sets the codProtocolloInps value for this Richiesta_RDC_beneficiari_dato_codProtocolloInps.
	 * 
	 * @param codProtocolloInps
	 */
	public void setCodProtocolloInps(java.lang.String codProtocolloInps) {
		this.codProtocolloInps = codProtocolloInps;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Richiesta_RDC_beneficiari_dato_codProtocolloInps))
			return false;
		Richiesta_RDC_beneficiari_dato_codProtocolloInps other = (Richiesta_RDC_beneficiari_dato_codProtocolloInps) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.codProtocolloInps == null && other.getCodProtocolloInps() == null)
				|| (this.codProtocolloInps != null && this.codProtocolloInps.equals(other.getCodProtocolloInps())));
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
		if (getCodProtocolloInps() != null) {
			_hashCode += getCodProtocolloInps().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Richiesta_RDC_beneficiari_dato_codProtocolloInps.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types",
				"richiesta_RDC_beneficiari_dato_codProtocolloInps"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codProtocolloInps");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "codProtocolloInps"));
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
