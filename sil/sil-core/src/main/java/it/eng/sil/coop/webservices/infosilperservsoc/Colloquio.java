/**
 * Colloquio.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

public class Colloquio implements java.io.Serializable {
	private java.util.Date dataColloquio;

	private java.lang.String servizio;

	public Colloquio() {
	}

	public Colloquio(java.util.Date dataColloquio, java.lang.String servizio) {
		this.dataColloquio = dataColloquio;
		this.servizio = servizio;
	}

	/**
	 * Gets the dataColloquio value for this Colloquio.
	 * 
	 * @return dataColloquio
	 */
	public java.util.Date getDataColloquio() {
		return dataColloquio;
	}

	/**
	 * Sets the dataColloquio value for this Colloquio.
	 * 
	 * @param dataColloquio
	 */
	public void setDataColloquio(java.util.Date dataColloquio) {
		this.dataColloquio = dataColloquio;
	}

	/**
	 * Gets the servizio value for this Colloquio.
	 * 
	 * @return servizio
	 */
	public java.lang.String getServizio() {
		return servizio;
	}

	/**
	 * Sets the servizio value for this Colloquio.
	 * 
	 * @param servizio
	 */
	public void setServizio(java.lang.String servizio) {
		this.servizio = servizio;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Colloquio))
			return false;
		Colloquio other = (Colloquio) obj;
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
				&& ((this.dataColloquio == null && other.getDataColloquio() == null)
						|| (this.dataColloquio != null && this.dataColloquio.equals(other.getDataColloquio())))
				&& ((this.servizio == null && other.getServizio() == null)
						|| (this.servizio != null && this.servizio.equals(other.getServizio())));
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
		if (getDataColloquio() != null) {
			_hashCode += getDataColloquio().hashCode();
		}
		if (getServizio() != null) {
			_hashCode += getServizio().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Colloquio.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "Colloquio"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataColloquio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataColloquio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("servizio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "servizio"));
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
