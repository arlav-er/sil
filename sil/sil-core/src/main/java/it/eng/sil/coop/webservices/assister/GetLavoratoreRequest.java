/**
 * Request.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.assister;

public class GetLavoratoreRequest implements java.io.Serializable {
	private java.lang.String cf;

	private java.lang.String password;

	private java.lang.String username;

	public GetLavoratoreRequest() {
	}

	public GetLavoratoreRequest(java.lang.String cf, java.lang.String password, java.lang.String username) {
		this.cf = cf;
		this.password = password;
		this.username = username;
	}

	/**
	 * Gets the cf value for this Request.
	 *
	 * @return cf
	 */
	public java.lang.String getCf() {
		return cf;
	}

	/**
	 * Sets the cf value for this Request.
	 *
	 * @param cf
	 */
	public void setCf(java.lang.String cf) {
		this.cf = cf;
	}

	/**
	 * Gets the password value for this Request.
	 *
	 * @return password
	 */
	public java.lang.String getPassword() {
		return password;
	}

	/**
	 * Sets the password value for this Request.
	 *
	 * @param password
	 */
	public void setPassword(java.lang.String password) {
		this.password = password;
	}

	/**
	 * Gets the username value for this Request.
	 *
	 * @return username
	 */
	public java.lang.String getUsername() {
		return username;
	}

	/**
	 * Sets the username value for this Request.
	 *
	 * @param username
	 */
	public void setUsername(java.lang.String username) {
		this.username = username;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GetLavoratoreRequest))
			return false;
		GetLavoratoreRequest other = (GetLavoratoreRequest) obj;
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
				&& ((this.cf == null && other.getCf() == null) || (this.cf != null && this.cf.equals(other.getCf())))
				&& ((this.password == null && other.getPassword() == null)
						|| (this.password != null && this.password.equals(other.getPassword())))
				&& ((this.username == null && other.getUsername() == null)
						|| (this.username != null && this.username.equals(other.getUsername())));
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
		if (getCf() != null) {
			_hashCode += getCf().hashCode();
		}
		if (getPassword() != null) {
			_hashCode += getPassword().hashCode();
		}
		if (getUsername() != null) {
			_hashCode += getUsername().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			GetLavoratoreRequest.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "Request"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cf");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "cf"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("data");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "data"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("password");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "password"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("username");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "username"));
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
