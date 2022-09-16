/**
 * Richiesta_RDC_beneficiari_dato_CodiceFiscale.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.serviceRDC.types;

public class Richiesta_RDC_beneficiari_dato_CodiceFiscale implements java.io.Serializable {
	private java.lang.String codFiscale;

	public Richiesta_RDC_beneficiari_dato_CodiceFiscale() {
	}

	public Richiesta_RDC_beneficiari_dato_CodiceFiscale(java.lang.String codFiscale) {
		this.codFiscale = codFiscale;
	}

	/**
	 * Gets the codFiscale value for this Richiesta_RDC_beneficiari_dato_CodiceFiscale.
	 * 
	 * @return codFiscale
	 */
	public java.lang.String getCodFiscale() {
		return codFiscale;
	}

	/**
	 * Sets the codFiscale value for this Richiesta_RDC_beneficiari_dato_CodiceFiscale.
	 * 
	 * @param codFiscale
	 */
	public void setCodFiscale(java.lang.String codFiscale) {
		this.codFiscale = codFiscale;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Richiesta_RDC_beneficiari_dato_CodiceFiscale))
			return false;
		Richiesta_RDC_beneficiari_dato_CodiceFiscale other = (Richiesta_RDC_beneficiari_dato_CodiceFiscale) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.codFiscale == null && other.getCodFiscale() == null)
				|| (this.codFiscale != null && this.codFiscale.equals(other.getCodFiscale())));
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
		if (getCodFiscale() != null) {
			_hashCode += getCodFiscale().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Richiesta_RDC_beneficiari_dato_CodiceFiscale.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types",
				"richiesta_RDC_beneficiari_dato_CodiceFiscale"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codFiscale");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "codFiscale"));
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
