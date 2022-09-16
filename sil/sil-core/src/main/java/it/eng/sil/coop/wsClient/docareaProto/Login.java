/**
 * Login.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

public class Login implements java.io.Serializable {
	private java.lang.String strCodEnte;

	private java.lang.String strUserName;

	private java.lang.String strPassword;

	public Login() {
	}

	public Login(java.lang.String strCodEnte, java.lang.String strUserName, java.lang.String strPassword) {
		this.strCodEnte = strCodEnte;
		this.strUserName = strUserName;
		this.strPassword = strPassword;
	}

	/**
	 * Gets the strCodEnte value for this Login.
	 * 
	 * @return strCodEnte
	 */
	public java.lang.String getStrCodEnte() {
		return strCodEnte;
	}

	/**
	 * Sets the strCodEnte value for this Login.
	 * 
	 * @param strCodEnte
	 */
	public void setStrCodEnte(java.lang.String strCodEnte) {
		this.strCodEnte = strCodEnte;
	}

	/**
	 * Gets the strUserName value for this Login.
	 * 
	 * @return strUserName
	 */
	public java.lang.String getStrUserName() {
		return strUserName;
	}

	/**
	 * Sets the strUserName value for this Login.
	 * 
	 * @param strUserName
	 */
	public void setStrUserName(java.lang.String strUserName) {
		this.strUserName = strUserName;
	}

	/**
	 * Gets the strPassword value for this Login.
	 * 
	 * @return strPassword
	 */
	public java.lang.String getStrPassword() {
		return strPassword;
	}

	/**
	 * Sets the strPassword value for this Login.
	 * 
	 * @param strPassword
	 */
	public void setStrPassword(java.lang.String strPassword) {
		this.strPassword = strPassword;
	}

	public String toString() {
		return new StringBuffer("Login: ").append("strCodEnte=").append(this.strCodEnte).append(", strUserName=")
				.append(this.strUserName).append(", strPassword=").append(this.strPassword).toString();
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Login))
			return false;
		Login other = (Login) obj;
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
				&& ((this.strCodEnte == null && other.getStrCodEnte() == null)
						|| (this.strCodEnte != null && this.strCodEnte.equals(other.getStrCodEnte())))
				&& ((this.strUserName == null && other.getStrUserName() == null)
						|| (this.strUserName != null && this.strUserName.equals(other.getStrUserName())))
				&& ((this.strPassword == null && other.getStrPassword() == null)
						|| (this.strPassword != null && this.strPassword.equals(other.getStrPassword())));
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
		if (getStrCodEnte() != null) {
			_hashCode += getStrCodEnte().hashCode();
		}
		if (getStrUserName() != null) {
			_hashCode += getStrUserName().hashCode();
		}
		if (getStrPassword() != null) {
			_hashCode += getStrPassword().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(Login.class,
			true);

	// configurazione namespace della busta soap
	private static String targetNamespace;
	static {
		targetNamespace = DocAreaWSConfig.getTargetNamespace();
	}

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(targetNamespace, ">Login"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strCodEnte");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespace, "strCodEnte"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strUserName");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespace, "strUserName"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strPassword");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespace, "strPassword"));
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
