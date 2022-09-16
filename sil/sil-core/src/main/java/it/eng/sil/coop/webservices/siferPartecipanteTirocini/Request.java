/**
 * Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferPartecipanteTirocini;

public class Request implements java.io.Serializable {
	private java.lang.String utente;

	private org.apache.axis.types.Token password;

	private java.lang.String metodo;

	private java.lang.String dati;

	public Request() {
	}

	public Request(java.lang.String utente, org.apache.axis.types.Token password, java.lang.String metodo,
			java.lang.String dati) {
		this.utente = utente;
		this.password = password;
		this.metodo = metodo;
		this.dati = dati;
	}

	/**
	 * Gets the utente value for this Request.
	 * 
	 * @return utente
	 */
	public java.lang.String getUtente() {
		return utente;
	}

	/**
	 * Sets the utente value for this Request.
	 * 
	 * @param utente
	 */
	public void setUtente(java.lang.String utente) {
		this.utente = utente;
	}

	/**
	 * Gets the password value for this Request.
	 * 
	 * @return password
	 */
	public org.apache.axis.types.Token getPassword() {
		return password;
	}

	/**
	 * Sets the password value for this Request.
	 * 
	 * @param password
	 */
	public void setPassword(org.apache.axis.types.Token password) {
		this.password = password;
	}

	/**
	 * Gets the metodo value for this Request.
	 * 
	 * @return metodo
	 */
	public java.lang.String getMetodo() {
		return metodo;
	}

	/**
	 * Sets the metodo value for this Request.
	 * 
	 * @param metodo
	 */
	public void setMetodo(java.lang.String metodo) {
		this.metodo = metodo;
	}

	/**
	 * Gets the dati value for this Request.
	 * 
	 * @return dati
	 */
	public java.lang.String getDati() {
		return dati;
	}

	/**
	 * Sets the dati value for this Request.
	 * 
	 * @param dati
	 */
	public void setDati(java.lang.String dati) {
		this.dati = dati;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Request))
			return false;
		Request other = (Request) obj;
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
				&& ((this.utente == null && other.getUtente() == null)
						|| (this.utente != null && this.utente.equals(other.getUtente())))
				&& ((this.password == null && other.getPassword() == null)
						|| (this.password != null && this.password.equals(other.getPassword())))
				&& ((this.metodo == null && other.getMetodo() == null)
						|| (this.metodo != null && this.metodo.equals(other.getMetodo())))
				&& ((this.dati == null && other.getDati() == null)
						|| (this.dati != null && this.dati.equals(other.getDati())));
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
		if (getUtente() != null) {
			_hashCode += getUtente().hashCode();
		}
		if (getPassword() != null) {
			_hashCode += getPassword().hashCode();
		}
		if (getMetodo() != null) {
			_hashCode += getMetodo().hashCode();
		}
		if (getDati() != null) {
			_hashCode += getDati().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Request.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("https://sifer.regione.emilia-romagna.it/WebService", ">request"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("utente");
		elemField.setXmlName(
				new javax.xml.namespace.QName("https://sifer.regione.emilia-romagna.it/WebService", "utente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("password");
		elemField.setXmlName(
				new javax.xml.namespace.QName("https://sifer.regione.emilia-romagna.it/WebService", "password"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("metodo");
		elemField.setXmlName(
				new javax.xml.namespace.QName("https://sifer.regione.emilia-romagna.it/WebService", "metodo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dati");
		elemField.setXmlName(
				new javax.xml.namespace.QName("https://sifer.regione.emilia-romagna.it/WebService", "dati"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
