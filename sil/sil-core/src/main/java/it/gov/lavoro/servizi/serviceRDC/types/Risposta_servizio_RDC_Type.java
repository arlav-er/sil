/**
 * Risposta_servizio_RDC_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.serviceRDC.types;

public class Risposta_servizio_RDC_Type implements java.io.Serializable {
	private it.gov.lavoro.servizi.serviceRDC.types.Beneficiario_Type[] beneficiari;

	private it.gov.lavoro.servizi.serviceRDC.types.Esito_Type esito;

	public Risposta_servizio_RDC_Type() {
	}

	public Risposta_servizio_RDC_Type(it.gov.lavoro.servizi.serviceRDC.types.Beneficiario_Type[] beneficiari,
			it.gov.lavoro.servizi.serviceRDC.types.Esito_Type esito) {
		this.beneficiari = beneficiari;
		this.esito = esito;
	}

	/**
	 * Gets the beneficiari value for this Risposta_servizio_RDC_Type.
	 * 
	 * @return beneficiari
	 */
	public it.gov.lavoro.servizi.serviceRDC.types.Beneficiario_Type[] getBeneficiari() {
		return beneficiari;
	}

	/**
	 * Sets the beneficiari value for this Risposta_servizio_RDC_Type.
	 * 
	 * @param beneficiari
	 */
	public void setBeneficiari(it.gov.lavoro.servizi.serviceRDC.types.Beneficiario_Type[] beneficiari) {
		this.beneficiari = beneficiari;
	}

	/**
	 * Gets the esito value for this Risposta_servizio_RDC_Type.
	 * 
	 * @return esito
	 */
	public it.gov.lavoro.servizi.serviceRDC.types.Esito_Type getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this Risposta_servizio_RDC_Type.
	 * 
	 * @param esito
	 */
	public void setEsito(it.gov.lavoro.servizi.serviceRDC.types.Esito_Type esito) {
		this.esito = esito;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Risposta_servizio_RDC_Type))
			return false;
		Risposta_servizio_RDC_Type other = (Risposta_servizio_RDC_Type) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.beneficiari == null && other.getBeneficiari() == null)
				|| (this.beneficiari != null && java.util.Arrays.equals(this.beneficiari, other.getBeneficiari())))
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
		if (getBeneficiari() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getBeneficiari()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getBeneficiari(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getEsito() != null) {
			_hashCode += getEsito().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Risposta_servizio_RDC_Type.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types",
				"risposta_servizio_RDC_Type"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("beneficiari");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "beneficiari"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "beneficiario_Type"));
		elemField.setNillable(false);
		elemField.setItemQName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "beneficiario"));
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "esito"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC/types", "esito_Type"));
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
