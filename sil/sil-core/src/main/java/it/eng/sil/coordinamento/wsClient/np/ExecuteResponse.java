/**
 * ExecuteResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coordinamento.wsClient.np;

public class ExecuteResponse implements java.io.Serializable {
	private java.lang.String executeReturn;

	public ExecuteResponse() {
	}

	public ExecuteResponse(java.lang.String executeReturn) {
		this.executeReturn = executeReturn;
	}

	/**
	 * Gets the executeReturn value for this ExecuteResponse.
	 * 
	 * @return executeReturn
	 */
	public java.lang.String getExecuteReturn() {
		return executeReturn;
	}

	/**
	 * Sets the executeReturn value for this ExecuteResponse.
	 * 
	 * @param executeReturn
	 */
	public void setExecuteReturn(java.lang.String executeReturn) {
		this.executeReturn = executeReturn;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ExecuteResponse))
			return false;
		ExecuteResponse other = (ExecuteResponse) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.executeReturn == null && other.getExecuteReturn() == null)
				|| (this.executeReturn != null && this.executeReturn.equals(other.getExecuteReturn())));
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
		if (getExecuteReturn() != null) {
			_hashCode += getExecuteReturn().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ExecuteResponse.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", ">executeResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("executeReturn");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "executeReturn"));
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
