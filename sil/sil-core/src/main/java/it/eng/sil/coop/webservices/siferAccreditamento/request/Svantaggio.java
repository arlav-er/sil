/**
 * Svantaggio.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferAccreditamento.request;

public class Svantaggio implements java.io.Serializable {
	private java.lang.Integer tipo_svantaggio;

	public Svantaggio() {
	}

	public Svantaggio(java.lang.Integer tipo_svantaggio) {
		this.tipo_svantaggio = tipo_svantaggio;
	}

	/**
	 * Gets the tipo_svantaggio value for this Svantaggio.
	 * 
	 * @return tipo_svantaggio
	 */
	public java.lang.Integer getTipo_svantaggio() {
		return tipo_svantaggio;
	}

	/**
	 * Sets the tipo_svantaggio value for this Svantaggio.
	 * 
	 * @param tipo_svantaggio
	 */
	public void setTipo_svantaggio(java.lang.Integer tipo_svantaggio) {
		this.tipo_svantaggio = tipo_svantaggio;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Svantaggio))
			return false;
		Svantaggio other = (Svantaggio) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.tipo_svantaggio == null && other.getTipo_svantaggio() == null)
				|| (this.tipo_svantaggio != null && this.tipo_svantaggio.equals(other.getTipo_svantaggio())));
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
		if (getTipo_svantaggio() != null) {
			_hashCode += getTipo_svantaggio().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Svantaggio.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"Svantaggio"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipo_svantaggio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "tipo_svantaggio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
