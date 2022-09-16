/**
 * UploadDocFirmatoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class UploadDocFirmatoResponse implements java.io.Serializable {
	private it.eng.sil.coop.webservices.firmagrafometrica.XML out_xml_output;

	public UploadDocFirmatoResponse() {
	}

	public UploadDocFirmatoResponse(it.eng.sil.coop.webservices.firmagrafometrica.XML out_xml_output) {
		this.out_xml_output = out_xml_output;
	}

	/**
	 * Gets the out_xml_output value for this UploadDocFirmatoResponse.
	 * 
	 * @return out_xml_output
	 */
	public it.eng.sil.coop.webservices.firmagrafometrica.XML getOut_xml_output() {
		return out_xml_output;
	}

	/**
	 * Sets the out_xml_output value for this UploadDocFirmatoResponse.
	 * 
	 * @param out_xml_output
	 */
	public void setOut_xml_output(it.eng.sil.coop.webservices.firmagrafometrica.XML out_xml_output) {
		this.out_xml_output = out_xml_output;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof UploadDocFirmatoResponse))
			return false;
		UploadDocFirmatoResponse other = (UploadDocFirmatoResponse) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.out_xml_output == null && other.getOut_xml_output() == null)
				|| (this.out_xml_output != null && this.out_xml_output.equals(other.getOut_xml_output())));
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
		if (getOut_xml_output() != null) {
			_hashCode += getOut_xml_output().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			UploadDocFirmatoResponse.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				">uploadDocFirmatoResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("out_xml_output");
		elemField.setXmlName(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"out_xml_output"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "XML"));
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
