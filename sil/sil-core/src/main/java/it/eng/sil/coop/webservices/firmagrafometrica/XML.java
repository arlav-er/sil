/**
 * XML.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class XML implements java.io.Serializable {
	private java.lang.String document;

	private java.lang.String element;

	public XML() {
	}

	public XML(java.lang.String document, java.lang.String element) {
		this.document = document;
		this.element = element;
	}

	/**
	 * Gets the document value for this XML.
	 * 
	 * @return document
	 */
	public java.lang.String getDocument() {
		return document;
	}

	/**
	 * Sets the document value for this XML.
	 * 
	 * @param document
	 */
	public void setDocument(java.lang.String document) {
		this.document = document;
	}

	/**
	 * Gets the element value for this XML.
	 * 
	 * @return element
	 */
	public java.lang.String getElement() {
		return element;
	}

	/**
	 * Sets the element value for this XML.
	 * 
	 * @param element
	 */
	public void setElement(java.lang.String element) {
		this.element = element;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof XML))
			return false;
		XML other = (XML) obj;
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
				&& ((this.document == null && other.getDocument() == null)
						|| (this.document != null && this.document.equals(other.getDocument())))
				&& ((this.element == null && other.getElement() == null)
						|| (this.element != null && this.element.equals(other.getElement())));
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
		if (getDocument() != null) {
			_hashCode += getDocument().hashCode();
		}
		if (getElement() != null) {
			_hashCode += getElement().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(XML.class,
			true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "XML"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("document");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "document"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("element");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "element"));
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
