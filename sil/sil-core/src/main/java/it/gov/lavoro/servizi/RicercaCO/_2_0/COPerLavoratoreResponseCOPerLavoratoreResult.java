/**
 * COPerLavoratoreResponseCOPerLavoratoreResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public class COPerLavoratoreResponseCOPerLavoratoreResult implements java.io.Serializable {
	private it.gov.lavoro.servizi.RicercaCO._2_0.COLavoratore_Type COLavoratore;

	private it.gov.lavoro.servizi.RicercaCO._2_0.Esito_Type esito;

	public COPerLavoratoreResponseCOPerLavoratoreResult() {
	}

	public COPerLavoratoreResponseCOPerLavoratoreResult(
			it.gov.lavoro.servizi.RicercaCO._2_0.COLavoratore_Type COLavoratore,
			it.gov.lavoro.servizi.RicercaCO._2_0.Esito_Type esito) {
		this.COLavoratore = COLavoratore;
		this.esito = esito;
	}

	/**
	 * Gets the COLavoratore value for this COPerLavoratoreResponseCOPerLavoratoreResult.
	 * 
	 * @return COLavoratore
	 */
	public it.gov.lavoro.servizi.RicercaCO._2_0.COLavoratore_Type getCOLavoratore() {
		return COLavoratore;
	}

	/**
	 * Sets the COLavoratore value for this COPerLavoratoreResponseCOPerLavoratoreResult.
	 * 
	 * @param COLavoratore
	 */
	public void setCOLavoratore(it.gov.lavoro.servizi.RicercaCO._2_0.COLavoratore_Type COLavoratore) {
		this.COLavoratore = COLavoratore;
	}

	/**
	 * Gets the esito value for this COPerLavoratoreResponseCOPerLavoratoreResult.
	 * 
	 * @return esito
	 */
	public it.gov.lavoro.servizi.RicercaCO._2_0.Esito_Type getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this COPerLavoratoreResponseCOPerLavoratoreResult.
	 * 
	 * @param esito
	 */
	public void setEsito(it.gov.lavoro.servizi.RicercaCO._2_0.Esito_Type esito) {
		this.esito = esito;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof COPerLavoratoreResponseCOPerLavoratoreResult))
			return false;
		COPerLavoratoreResponseCOPerLavoratoreResult other = (COPerLavoratoreResponseCOPerLavoratoreResult) obj;
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
				&& ((this.COLavoratore == null && other.getCOLavoratore() == null)
						|| (this.COLavoratore != null && this.COLavoratore.equals(other.getCOLavoratore())))
				&& ((this.esito == null && other.getEsito() == null)
						|| (this.esito != null && this.esito.equals(other.getEsito())));
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
		if (getCOLavoratore() != null) {
			_hashCode += getCOLavoratore().hashCode();
		}
		if (getEsito() != null) {
			_hashCode += getEsito().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			COPerLavoratoreResponseCOPerLavoratoreResult.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				">>COPerLavoratoreResponse>COPerLavoratoreResult"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("COLavoratore");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "COLavoratore"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "COLavoratore_Type"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Esito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0", "Esito_Type"));
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
