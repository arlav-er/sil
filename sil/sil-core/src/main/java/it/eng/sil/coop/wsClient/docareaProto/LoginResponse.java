/**
 * LoginResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

public class LoginResponse implements java.io.Serializable {
	private it.eng.sil.coop.wsClient.docareaProto.LoginRet loginResult;

	public LoginResponse() {
	}

	public LoginResponse(it.eng.sil.coop.wsClient.docareaProto.LoginRet loginResult) {
		this.loginResult = loginResult;
	}

	/**
	 * Gets the loginResult value for this LoginResponse.
	 * 
	 * @return loginResult
	 */
	public it.eng.sil.coop.wsClient.docareaProto.LoginRet getLoginResult() {
		return loginResult;
	}

	/**
	 * Sets the loginResult value for this LoginResponse.
	 * 
	 * @param loginResult
	 */
	public void setLoginResult(it.eng.sil.coop.wsClient.docareaProto.LoginRet loginResult) {
		this.loginResult = loginResult;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof LoginResponse))
			return false;
		LoginResponse other = (LoginResponse) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.loginResult == null && other.getLoginResult() == null)
				|| (this.loginResult != null && this.loginResult.equals(other.getLoginResult())));
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
		if (getLoginResult() != null) {
			_hashCode += getLoginResult().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			LoginResponse.class, true);

	// configurazione namespace della busta soap
	private static String targetNamespace;
	// configurazione namespace del tipo di ritorno della busta soap
	private static String targetNamespaceResponse;
	static {
		targetNamespace = DocAreaWSConfig.getTargetNamespace();
		targetNamespaceResponse = DocAreaWSConfig.getTargetNamespaceResponseType();
	}

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(targetNamespace, ">LoginResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("loginResult");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespace, "LoginResult"));
		elemField.setXmlType(new javax.xml.namespace.QName(targetNamespaceResponse, "LoginRet"));
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
