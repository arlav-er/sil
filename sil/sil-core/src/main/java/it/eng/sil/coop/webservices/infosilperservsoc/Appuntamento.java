/**
 * Appuntamento.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

public class Appuntamento implements java.io.Serializable {
	private java.util.Date dataAppuntamento;

	private java.lang.String dettaglio;

	public Appuntamento() {
	}

	public Appuntamento(java.util.Date dataAppuntamento, java.lang.String dettaglio) {
		this.dataAppuntamento = dataAppuntamento;
		this.dettaglio = dettaglio;
	}

	/**
	 * Gets the dataAppuntamento value for this Appuntamento.
	 * 
	 * @return dataAppuntamento
	 */
	public java.util.Date getDataAppuntamento() {
		return dataAppuntamento;
	}

	/**
	 * Sets the dataAppuntamento value for this Appuntamento.
	 * 
	 * @param dataAppuntamento
	 */
	public void setDataAppuntamento(java.util.Date dataAppuntamento) {
		this.dataAppuntamento = dataAppuntamento;
	}

	/**
	 * Gets the dettaglio value for this Appuntamento.
	 * 
	 * @return dettaglio
	 */
	public java.lang.String getDettaglio() {
		return dettaglio;
	}

	/**
	 * Sets the dettaglio value for this Appuntamento.
	 * 
	 * @param dettaglio
	 */
	public void setDettaglio(java.lang.String dettaglio) {
		this.dettaglio = dettaglio;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Appuntamento))
			return false;
		Appuntamento other = (Appuntamento) obj;
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
				&& ((this.dataAppuntamento == null && other.getDataAppuntamento() == null)
						|| (this.dataAppuntamento != null && this.dataAppuntamento.equals(other.getDataAppuntamento())))
				&& ((this.dettaglio == null && other.getDettaglio() == null)
						|| (this.dettaglio != null && this.dettaglio.equals(other.getDettaglio())));
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
		if (getDataAppuntamento() != null) {
			_hashCode += getDataAppuntamento().hashCode();
		}
		if (getDettaglio() != null) {
			_hashCode += getDettaglio().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Appuntamento.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "Appuntamento"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataAppuntamento");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataAppuntamento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dettaglio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dettaglio"));
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
