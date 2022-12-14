/**
 * ExportResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto.v161;

public class ExportResponse implements java.io.Serializable {
	private ExportRet exportResult;

	public ExportResponse() {
	}

	public ExportResponse(ExportRet exportResult) {
		this.exportResult = exportResult;
	}

	/**
	 * Gets the exportResult value for this ExportResponse.
	 * 
	 * @return exportResult
	 */
	public ExportRet getExportResult() {
		return exportResult;
	}

	/**
	 * Sets the exportResult value for this ExportResponse.
	 * 
	 * @param exportResult
	 */
	public void setExportResult(ExportRet exportResult) {
		this.exportResult = exportResult;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ExportResponse))
			return false;
		ExportResponse other = (ExportResponse) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.exportResult == null && other.getExportResult() == null)
				|| (this.exportResult != null && this.exportResult.equals(other.getExportResult())));
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
		if (getExportResult() != null) {
			_hashCode += getExportResult().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ExportResponse.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">ExportResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("exportResult");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "ExportResult"));
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
