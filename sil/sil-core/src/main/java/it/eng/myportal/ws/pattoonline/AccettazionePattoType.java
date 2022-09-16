/**
 * AccettazionePattoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.myportal.ws.pattoonline;

public class AccettazionePattoType implements java.io.Serializable {
	private java.util.Calendar dtmAccettazione;

	private it.eng.myportal.ws.pattoonline.AccettazionePattoTypeTipoAccettazione tipoAccettazione;

	public AccettazionePattoType() {
	}

	public AccettazionePattoType(java.util.Calendar dtmAccettazione,
			it.eng.myportal.ws.pattoonline.AccettazionePattoTypeTipoAccettazione tipoAccettazione) {
		this.dtmAccettazione = dtmAccettazione;
		this.tipoAccettazione = tipoAccettazione;
	}

	/**
	 * Gets the dtmAccettazione value for this AccettazionePattoType.
	 * 
	 * @return dtmAccettazione
	 */
	public java.util.Calendar getDtmAccettazione() {
		return dtmAccettazione;
	}

	/**
	 * Sets the dtmAccettazione value for this AccettazionePattoType.
	 * 
	 * @param dtmAccettazione
	 */
	public void setDtmAccettazione(java.util.Calendar dtmAccettazione) {
		this.dtmAccettazione = dtmAccettazione;
	}

	/**
	 * Gets the tipoAccettazione value for this AccettazionePattoType.
	 * 
	 * @return tipoAccettazione
	 */
	public it.eng.myportal.ws.pattoonline.AccettazionePattoTypeTipoAccettazione getTipoAccettazione() {
		return tipoAccettazione;
	}

	/**
	 * Sets the tipoAccettazione value for this AccettazionePattoType.
	 * 
	 * @param tipoAccettazione
	 */
	public void setTipoAccettazione(
			it.eng.myportal.ws.pattoonline.AccettazionePattoTypeTipoAccettazione tipoAccettazione) {
		this.tipoAccettazione = tipoAccettazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof AccettazionePattoType))
			return false;
		AccettazionePattoType other = (AccettazionePattoType) obj;
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
				&& ((this.dtmAccettazione == null && other.getDtmAccettazione() == null)
						|| (this.dtmAccettazione != null && this.dtmAccettazione.equals(other.getDtmAccettazione())))
				&& ((this.tipoAccettazione == null && other.getTipoAccettazione() == null)
						|| (this.tipoAccettazione != null
								&& this.tipoAccettazione.equals(other.getTipoAccettazione())));
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
		if (getDtmAccettazione() != null) {
			_hashCode += getDtmAccettazione().hashCode();
		}
		if (getTipoAccettazione() != null) {
			_hashCode += getTipoAccettazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			AccettazionePattoType.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://pattoonline.ws.myportal.eng.it/", "AccettazionePattoType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtmAccettazione");
		elemField
				.setXmlName(new javax.xml.namespace.QName("http://pattoonline.ws.myportal.eng.it/", "DtmAccettazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipoAccettazione");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://pattoonline.ws.myportal.eng.it/", "TipoAccettazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://pattoonline.ws.myportal.eng.it/",
				">AccettazionePattoType>TipoAccettazione"));
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
