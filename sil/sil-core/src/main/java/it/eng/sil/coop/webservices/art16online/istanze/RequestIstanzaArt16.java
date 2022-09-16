/**
 * RequestIstanzaArt16.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class RequestIstanzaArt16 implements java.io.Serializable {
	private int numero; // attribute

	private int anno; // attribute

	public RequestIstanzaArt16() {
	}

	public RequestIstanzaArt16(int numero, int anno) {
		this.numero = numero;
		this.anno = anno;
	}

	/**
	 * Gets the numero value for this RequestIstanzaArt16.
	 * 
	 * @return numero
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * Sets the numero value for this RequestIstanzaArt16.
	 * 
	 * @param numero
	 */
	public void setNumero(int numero) {
		this.numero = numero;
	}

	/**
	 * Gets the anno value for this RequestIstanzaArt16.
	 * 
	 * @return anno
	 */
	public int getAnno() {
		return anno;
	}

	/**
	 * Sets the anno value for this RequestIstanzaArt16.
	 * 
	 * @param anno
	 */
	public void setAnno(int anno) {
		this.anno = anno;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof RequestIstanzaArt16))
			return false;
		RequestIstanzaArt16 other = (RequestIstanzaArt16) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.numero == other.getNumero() && this.anno == other.getAnno();
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
		_hashCode += getNumero();
		_hashCode += getAnno();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			RequestIstanzaArt16.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"RequestIstanzaArt16"));
		org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("numero");
		attrField.setXmlName(new javax.xml.namespace.QName("", "numero"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		typeDesc.addFieldDesc(attrField);
		attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("anno");
		attrField.setXmlName(new javax.xml.namespace.QName("", "anno"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		typeDesc.addFieldDesc(attrField);
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
