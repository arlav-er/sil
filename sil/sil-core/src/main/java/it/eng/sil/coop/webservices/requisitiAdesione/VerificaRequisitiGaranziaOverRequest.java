/**
 * VerificaRequisitiGaranziaOverRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.requisitiAdesione;

public class VerificaRequisitiGaranziaOverRequest implements java.io.Serializable {
	private java.lang.String codiceFiscale;

	private java.util.Calendar dataAdesione;

	private java.lang.String percettoreAmmortizzatori;

	private java.lang.String dichiarazione;

	public VerificaRequisitiGaranziaOverRequest() {
	}

	public VerificaRequisitiGaranziaOverRequest(java.lang.String codiceFiscale, java.util.Calendar dataAdesione,
			java.lang.String percettoreAmmortizzatori, java.lang.String dichiarazione) {
		this.codiceFiscale = codiceFiscale;
		this.dataAdesione = dataAdesione;
		this.percettoreAmmortizzatori = percettoreAmmortizzatori;
		this.dichiarazione = dichiarazione;
	}

	/**
	 * Gets the codiceFiscale value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @return codiceFiscale
	 */
	public java.lang.String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(java.lang.String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the dataAdesione value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @return dataAdesione
	 */
	public java.util.Calendar getDataAdesione() {
		return dataAdesione;
	}

	/**
	 * Sets the dataAdesione value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @param dataAdesione
	 */
	public void setDataAdesione(java.util.Calendar dataAdesione) {
		this.dataAdesione = dataAdesione;
	}

	/**
	 * Gets the percettoreAmmortizzatori value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @return percettoreAmmortizzatori
	 */
	public java.lang.String getPercettoreAmmortizzatori() {
		return percettoreAmmortizzatori;
	}

	/**
	 * Sets the percettoreAmmortizzatori value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @param percettoreAmmortizzatori
	 */
	public void setPercettoreAmmortizzatori(java.lang.String percettoreAmmortizzatori) {
		this.percettoreAmmortizzatori = percettoreAmmortizzatori;
	}

	/**
	 * Gets the dichiarazione value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @return dichiarazione
	 */
	public java.lang.String getDichiarazione() {
		return dichiarazione;
	}

	/**
	 * Sets the dichiarazione value for this VerificaRequisitiGaranziaOverRequest.
	 * 
	 * @param dichiarazione
	 */
	public void setDichiarazione(java.lang.String dichiarazione) {
		this.dichiarazione = dichiarazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof VerificaRequisitiGaranziaOverRequest))
			return false;
		VerificaRequisitiGaranziaOverRequest other = (VerificaRequisitiGaranziaOverRequest) obj;
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
				&& ((this.codiceFiscale == null && other.getCodiceFiscale() == null)
						|| (this.codiceFiscale != null && this.codiceFiscale.equals(other.getCodiceFiscale())))
				&& ((this.dataAdesione == null && other.getDataAdesione() == null)
						|| (this.dataAdesione != null && this.dataAdesione.equals(other.getDataAdesione())))
				&& ((this.percettoreAmmortizzatori == null && other.getPercettoreAmmortizzatori() == null)
						|| (this.percettoreAmmortizzatori != null
								&& this.percettoreAmmortizzatori.equals(other.getPercettoreAmmortizzatori())))
				&& ((this.dichiarazione == null && other.getDichiarazione() == null)
						|| (this.dichiarazione != null && this.dichiarazione.equals(other.getDichiarazione())));
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
		if (getCodiceFiscale() != null) {
			_hashCode += getCodiceFiscale().hashCode();
		}
		if (getDataAdesione() != null) {
			_hashCode += getDataAdesione().hashCode();
		}
		if (getPercettoreAmmortizzatori() != null) {
			_hashCode += getPercettoreAmmortizzatori().hashCode();
		}
		if (getDichiarazione() != null) {
			_hashCode += getDichiarazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			VerificaRequisitiGaranziaOverRequest.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://requisitiAdesione.webservices.coop.sil.eng.it",
				"VerificaRequisitiGaranziaOverRequest"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://requisitiAdesione.webservices.coop.sil.eng.it", "codiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataAdesione");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://requisitiAdesione.webservices.coop.sil.eng.it", "dataAdesione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("percettoreAmmortizzatori");
		elemField.setXmlName(new javax.xml.namespace.QName("http://requisitiAdesione.webservices.coop.sil.eng.it",
				"percettoreAmmortizzatori"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dichiarazione");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://requisitiAdesione.webservices.coop.sil.eng.it", "dichiarazione"));
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
