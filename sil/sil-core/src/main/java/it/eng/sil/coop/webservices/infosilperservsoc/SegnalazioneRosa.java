/**
 * SegnalazioneRosa.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

public class SegnalazioneRosa implements java.io.Serializable {
	private java.util.Date dataSegnalazione;

	private java.lang.String segnalazione;

	public SegnalazioneRosa() {
	}

	public SegnalazioneRosa(java.util.Date dataSegnalazione, java.lang.String segnalazione) {
		this.dataSegnalazione = dataSegnalazione;
		this.segnalazione = segnalazione;
	}

	/**
	 * Gets the dataSegnalazione value for this SegnalazioneRosa.
	 * 
	 * @return dataSegnalazione
	 */
	public java.util.Date getDataSegnalazione() {
		return dataSegnalazione;
	}

	/**
	 * Sets the dataSegnalazione value for this SegnalazioneRosa.
	 * 
	 * @param dataSegnalazione
	 */
	public void setDataSegnalazione(java.util.Date dataSegnalazione) {
		this.dataSegnalazione = dataSegnalazione;
	}

	/**
	 * Gets the segnalazione value for this SegnalazioneRosa.
	 * 
	 * @return segnalazione
	 */
	public java.lang.String getSegnalazione() {
		return segnalazione;
	}

	/**
	 * Sets the segnalazione value for this SegnalazioneRosa.
	 * 
	 * @param segnalazione
	 */
	public void setSegnalazione(java.lang.String segnalazione) {
		this.segnalazione = segnalazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SegnalazioneRosa))
			return false;
		SegnalazioneRosa other = (SegnalazioneRosa) obj;
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
				&& ((this.dataSegnalazione == null && other.getDataSegnalazione() == null)
						|| (this.dataSegnalazione != null && this.dataSegnalazione.equals(other.getDataSegnalazione())))
				&& ((this.segnalazione == null && other.getSegnalazione() == null)
						|| (this.segnalazione != null && this.segnalazione.equals(other.getSegnalazione())));
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
		if (getDataSegnalazione() != null) {
			_hashCode += getDataSegnalazione().hashCode();
		}
		if (getSegnalazione() != null) {
			_hashCode += getSegnalazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SegnalazioneRosa.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "SegnalazioneRosa"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataSegnalazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataSegnalazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("segnalazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "segnalazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
