/**
 * Risposta.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class Risposta implements java.io.Serializable {
	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Esito esito;

	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Servizio[] servizi;

	public Risposta() {
	}

	public Risposta(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Esito esito,
			it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Servizio[] servizi) {
		this.esito = esito;
		this.servizi = servizi;
	}

	/**
	 * Gets the esito value for this Risposta.
	 * 
	 * @return esito
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Esito getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this Risposta.
	 * 
	 * @param esito
	 */
	public void setEsito(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Esito esito) {
		this.esito = esito;
	}

	/**
	 * Gets the servizi value for this Risposta.
	 * 
	 * @return servizi
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Servizio[] getServizi() {
		return servizi;
	}

	/**
	 * Sets the servizi value for this Risposta.
	 * 
	 * @param servizi
	 */
	public void setServizi(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Servizio[] servizi) {
		this.servizi = servizi;
	}

	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Servizio getServizi(int i) {
		return this.servizi[i];
	}

	public void setServizi(int i, it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.Servizio _value) {
		this.servizi[i] = _value;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Risposta))
			return false;
		Risposta other = (Risposta) obj;
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
				&& ((this.servizi == null && other.getServizi() == null)
						|| (this.servizi != null && java.util.Arrays.equals(this.servizi, other.getServizi())));
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
		if (getServizi() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getServizi()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getServizi(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Risposta.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "risposta"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName("", "esito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "esito"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("servizi");
		elemField.setXmlName(new javax.xml.namespace.QName("", "servizi"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "servizio"));
		elemField.setNillable(false);
		elemField.setMaxOccursUnbounded(true);
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
