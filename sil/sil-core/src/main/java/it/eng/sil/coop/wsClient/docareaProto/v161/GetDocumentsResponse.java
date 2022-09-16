/**
 * GetDocumentsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto.v161;

public class GetDocumentsResponse implements java.io.Serializable {
	private ExportRet getDocumentsResult;

	public GetDocumentsResponse() {
	}

	public GetDocumentsResponse(ExportRet getDocumentsResult) {
		this.getDocumentsResult = getDocumentsResult;
	}

	/**
	 * Gets the getDocumentsResult value for this GetDocumentsResponse.
	 * 
	 * @return getDocumentsResult
	 */
	public ExportRet getGetDocumentsResult() {
		return getDocumentsResult;
	}

	/**
	 * Sets the getDocumentsResult value for this GetDocumentsResponse.
	 * 
	 * @param getDocumentsResult
	 */
	public void setGetDocumentsResult(ExportRet getDocumentsResult) {
		this.getDocumentsResult = getDocumentsResult;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GetDocumentsResponse))
			return false;
		GetDocumentsResponse other = (GetDocumentsResponse) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.getDocumentsResult == null && other.getGetDocumentsResult() == null)
				|| (this.getDocumentsResult != null && this.getDocumentsResult.equals(other.getGetDocumentsResult())));
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
		if (getGetDocumentsResult() != null) {
			_hashCode += getGetDocumentsResult().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			GetDocumentsResponse.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">getDocumentsResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("getDocumentsResult");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "getDocumentsResult"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "ExportRet"));
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
