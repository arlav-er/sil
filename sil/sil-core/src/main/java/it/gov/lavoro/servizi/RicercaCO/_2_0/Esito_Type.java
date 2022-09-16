/**
 * Esito_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public class Esito_Type implements java.io.Serializable {
	private java.lang.String codice;

	private java.lang.String messaggio;

	public Esito_Type() {
	}

	public Esito_Type(java.lang.String codice, java.lang.String messaggio) {
		this.codice = codice;
		this.messaggio = messaggio;
	}

	/**
	 * Gets the codice value for this Esito_Type.
	 * 
	 * @return codice
	 */
	public java.lang.String getCodice() {
		return codice;
	}

	/**
	 * Sets the codice value for this Esito_Type.
	 * 
	 * @param codice
	 */
	public void setCodice(java.lang.String codice) {
		this.codice = codice;
	}

	/**
	 * Gets the messaggio value for this Esito_Type.
	 * 
	 * @return messaggio
	 */
	public java.lang.String getMessaggio() {
		return messaggio;
	}

	/**
	 * Sets the messaggio value for this Esito_Type.
	 * 
	 * @param messaggio
	 */
	public void setMessaggio(java.lang.String messaggio) {
		this.messaggio = messaggio;
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
				&& ((this.codice == null && other.getCodice() == null)
						|| (this.codice != null && this.codice.equals(other.getCodice())))
				&& ((this.messaggio == null && other.getMessaggio() == null)
						|| (this.messaggio != null && this.messaggio.equals(other.getMessaggio())));
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
		if (getCodice() != null) {
			_hashCode += getCodice().hashCode();
		}
		if (getMessaggio() != null) {
			_hashCode += getMessaggio().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Esito_Type.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Esito_Type"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codice");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Codice"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("messaggio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Messaggio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
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
