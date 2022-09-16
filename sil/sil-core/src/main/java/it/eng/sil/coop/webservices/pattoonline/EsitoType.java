/**
 * EsitoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.pattoonline;

public class EsitoType implements java.io.Serializable {
	private it.eng.sil.coop.webservices.pattoonline.EsitoTypeEsito esito;

	private java.lang.String descrizione;

	public EsitoType() {
	}

	public EsitoType(it.eng.sil.coop.webservices.pattoonline.EsitoTypeEsito esito, java.lang.String descrizione) {
		this.esito = esito;
		this.descrizione = descrizione;
	}

	/**
	 * Gets the esito value for this EsitoType.
	 * 
	 * @return esito
	 */
	public it.eng.sil.coop.webservices.pattoonline.EsitoTypeEsito getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this EsitoType.
	 * 
	 * @param esito
	 */
	public void setEsito(it.eng.sil.coop.webservices.pattoonline.EsitoTypeEsito esito) {
		this.esito = esito;
	}

	/**
	 * Gets the descrizione value for this EsitoType.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this EsitoType.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof EsitoType))
			return false;
		EsitoType other = (EsitoType) obj;
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
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())));
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
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			EsitoType.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "EsitoType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "Esito"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", ">EsitoType>Esito"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descrizione");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "Descrizione"));
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
