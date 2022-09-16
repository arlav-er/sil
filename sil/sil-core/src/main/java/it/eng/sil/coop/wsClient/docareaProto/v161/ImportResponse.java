/**
 * ImportResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto.v161;

public class ImportResponse implements java.io.Serializable {
	private ImportRet importResult;

	public ImportResponse() {
	}

	public ImportResponse(ImportRet importResult) {
		this.importResult = importResult;
	}

	/**
	 * Gets the importResult value for this ImportResponse.
	 * 
	 * @return importResult
	 */
	public ImportRet getImportResult() {
		return importResult;
	}

	/**
	 * Sets the importResult value for this ImportResponse.
	 * 
	 * @param importResult
	 */
	public void setImportResult(ImportRet importResult) {
		this.importResult = importResult;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ImportResponse))
			return false;
		ImportResponse other = (ImportResponse) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.importResult == null && other.getImportResult() == null)
				|| (this.importResult != null && this.importResult.equals(other.getImportResult())));
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
		if (getImportResult() != null) {
			_hashCode += getImportResult().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ImportResponse.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">ImportResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("importResult");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "ImportResult"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "ImportRet"));
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
