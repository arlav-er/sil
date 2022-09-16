/**
 * UploadDocFirmato.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class UploadDocFirmato implements java.io.Serializable {
	private it.eng.sil.coop.webservices.firmagrafometrica.BLOB in_document_pdf;

	private java.lang.String in_string_pdfname;

	private java.lang.String in_string_xmlparams;

	public UploadDocFirmato() {
	}

	public UploadDocFirmato(it.eng.sil.coop.webservices.firmagrafometrica.BLOB in_document_pdf,
			java.lang.String in_string_pdfname, java.lang.String in_string_xmlparams) {
		this.in_document_pdf = in_document_pdf;
		this.in_string_pdfname = in_string_pdfname;
		this.in_string_xmlparams = in_string_xmlparams;
	}

	/**
	 * Gets the in_document_pdf value for this UploadDocFirmato.
	 * 
	 * @return in_document_pdf
	 */
	public it.eng.sil.coop.webservices.firmagrafometrica.BLOB getIn_document_pdf() {
		return in_document_pdf;
	}

	/**
	 * Sets the in_document_pdf value for this UploadDocFirmato.
	 * 
	 * @param in_document_pdf
	 */
	public void setIn_document_pdf(it.eng.sil.coop.webservices.firmagrafometrica.BLOB in_document_pdf) {
		this.in_document_pdf = in_document_pdf;
	}

	/**
	 * Gets the in_string_pdfname value for this UploadDocFirmato.
	 * 
	 * @return in_string_pdfname
	 */
	public java.lang.String getIn_string_pdfname() {
		return in_string_pdfname;
	}

	/**
	 * Sets the in_string_pdfname value for this UploadDocFirmato.
	 * 
	 * @param in_string_pdfname
	 */
	public void setIn_string_pdfname(java.lang.String in_string_pdfname) {
		this.in_string_pdfname = in_string_pdfname;
	}

	/**
	 * Gets the in_string_xmlparams value for this UploadDocFirmato.
	 * 
	 * @return in_string_xmlparams
	 */
	public java.lang.String getIn_string_xmlparams() {
		return in_string_xmlparams;
	}

	/**
	 * Sets the in_string_xmlparams value for this UploadDocFirmato.
	 * 
	 * @param in_string_xmlparams
	 */
	public void setIn_string_xmlparams(java.lang.String in_string_xmlparams) {
		this.in_string_xmlparams = in_string_xmlparams;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof UploadDocFirmato))
			return false;
		UploadDocFirmato other = (UploadDocFirmato) obj;
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
				&& ((this.in_document_pdf == null && other.getIn_document_pdf() == null)
						|| (this.in_document_pdf != null && this.in_document_pdf.equals(other.getIn_document_pdf())))
				&& ((this.in_string_pdfname == null && other.getIn_string_pdfname() == null)
						|| (this.in_string_pdfname != null
								&& this.in_string_pdfname.equals(other.getIn_string_pdfname())))
				&& ((this.in_string_xmlparams == null && other.getIn_string_xmlparams() == null)
						|| (this.in_string_xmlparams != null
								&& this.in_string_xmlparams.equals(other.getIn_string_xmlparams())));
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
		if (getIn_document_pdf() != null) {
			_hashCode += getIn_document_pdf().hashCode();
		}
		if (getIn_string_pdfname() != null) {
			_hashCode += getIn_string_pdfname().hashCode();
		}
		if (getIn_string_xmlparams() != null) {
			_hashCode += getIn_string_xmlparams().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			UploadDocFirmato.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				">uploadDocFirmato"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("in_document_pdf");
		elemField.setXmlName(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"in_document_pdf"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "BLOB"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("in_string_pdfname");
		elemField.setXmlName(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"in_string_pdfname"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("in_string_xmlparams");
		elemField.setXmlName(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"in_string_xmlparams"));
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
