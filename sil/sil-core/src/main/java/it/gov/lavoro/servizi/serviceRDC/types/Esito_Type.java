/**
 * Esito_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.serviceRDC.types;

public class Esito_Type implements java.io.Serializable {
	private java.lang.String codEsito;

	private java.lang.String messaggioErrore;

	public Esito_Type() {
	}

	public Esito_Type(java.lang.String codEsito, java.lang.String messaggioErrore) {
		this.codEsito = codEsito;
		this.messaggioErrore = messaggioErrore;
	}

	/**
	 * Gets the codEsito value for this Esito_Type.
	 * 
	 * @return codEsito
	 */
	public java.lang.String getCodEsito() {
		return codEsito;
	}

	/**
	 * Sets the codEsito value for this Esito_Type.
	 * 
	 * @param codEsito
	 */
	public void setCodEsito(java.lang.String codEsito) {
		this.codEsito = codEsito;
	}

	/**
	 * Gets the messaggioErrore value for this Esito_Type.
	 * 
	 * @return messaggioErrore
	 */
	public java.lang.String getMessaggioErrore() {
		return messaggioErrore;
	}

	/**
	 * Sets the messaggioErrore value for this Esito_Type.
	 * 
	 * @param messaggioErrore
	 */
	public void setMessaggioErrore(java.lang.String messaggioErrore) {
		this.messaggioErrore = messaggioErrore;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Esito_Type))
			return false;
		Esito_Type other = (Esito_Type) obj;
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
				&& ((this.codEsito == null && other.getCodEsito() == null)
						|| (this.codEsito != null && this.codEsito.equals(other.getCodEsito())))
				&& ((this.messaggioErrore == null && other.getMessaggioErrore() == null)
						|| (this.messaggioErrore != null && this.messaggioErrore.equals(other.getMessaggioErrore())));
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
		if (getCodEsito() != null) {
			_hashCode += getCodEsito().hashCode();
		}
		if (getMessaggioErrore() != null) {
			_hashCode += getMessaggioErrore().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Esito_Type.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "esito_Type"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codEsito");
		elemField
				.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "codEsito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("messaggioErrore");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "messaggioErrore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
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
