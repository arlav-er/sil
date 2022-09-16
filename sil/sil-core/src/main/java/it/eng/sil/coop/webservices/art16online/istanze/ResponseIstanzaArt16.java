/**
 * ResponseIstanzaArt16.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class ResponseIstanzaArt16 implements java.io.Serializable {
	private it.eng.sil.coop.webservices.art16online.istanze.EsitoType esito;

	private it.eng.sil.coop.webservices.art16online.istanze.IstanzaArt16Type istanzaArt16;

	public ResponseIstanzaArt16() {
	}

	public ResponseIstanzaArt16(it.eng.sil.coop.webservices.art16online.istanze.EsitoType esito,
			it.eng.sil.coop.webservices.art16online.istanze.IstanzaArt16Type istanzaArt16) {
		this.esito = esito;
		this.istanzaArt16 = istanzaArt16;
	}

	/**
	 * Gets the esito value for this ResponseIstanzaArt16.
	 * 
	 * @return esito
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.EsitoType getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this ResponseIstanzaArt16.
	 * 
	 * @param esito
	 */
	public void setEsito(it.eng.sil.coop.webservices.art16online.istanze.EsitoType esito) {
		this.esito = esito;
	}

	/**
	 * Gets the istanzaArt16 value for this ResponseIstanzaArt16.
	 * 
	 * @return istanzaArt16
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.IstanzaArt16Type getIstanzaArt16() {
		return istanzaArt16;
	}

	/**
	 * Sets the istanzaArt16 value for this ResponseIstanzaArt16.
	 * 
	 * @param istanzaArt16
	 */
	public void setIstanzaArt16(it.eng.sil.coop.webservices.art16online.istanze.IstanzaArt16Type istanzaArt16) {
		this.istanzaArt16 = istanzaArt16;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ResponseIstanzaArt16))
			return false;
		ResponseIstanzaArt16 other = (ResponseIstanzaArt16) obj;
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
				&& ((this.esito == null && other.getEsito() == null)
						|| (this.esito != null && this.esito.equals(other.getEsito())))
				&& ((this.istanzaArt16 == null && other.getIstanzaArt16() == null)
						|| (this.istanzaArt16 != null && this.istanzaArt16.equals(other.getIstanzaArt16())));
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
		if (getEsito() != null) {
			_hashCode += getEsito().hashCode();
		}
		if (getIstanzaArt16() != null) {
			_hashCode += getIstanzaArt16().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ResponseIstanzaArt16.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"ResponseIstanzaArt16"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "Esito"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "EsitoType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("istanzaArt16");
		elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"IstanzaArt16"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"IstanzaArt16Type"));
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
