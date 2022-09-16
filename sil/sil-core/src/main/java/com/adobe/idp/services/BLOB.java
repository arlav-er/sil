/**
 * BLOB.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public class BLOB implements java.io.Serializable {
	private java.lang.String contentType;

	private byte[] binaryData;

	private java.lang.String attachmentID;

	private java.lang.String remoteURL;

	public BLOB() {
	}

	public BLOB(java.lang.String contentType, byte[] binaryData, java.lang.String attachmentID,
			java.lang.String remoteURL) {
		this.contentType = contentType;
		this.binaryData = binaryData;
		this.attachmentID = attachmentID;
		this.remoteURL = remoteURL;
	}

	/**
	 * Gets the contentType value for this BLOB.
	 * 
	 * @return contentType
	 */
	public java.lang.String getContentType() {
		return contentType;
	}

	/**
	 * Sets the contentType value for this BLOB.
	 * 
	 * @param contentType
	 */
	public void setContentType(java.lang.String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets the binaryData value for this BLOB.
	 * 
	 * @return binaryData
	 */
	public byte[] getBinaryData() {
		return binaryData;
	}

	/**
	 * Sets the binaryData value for this BLOB.
	 * 
	 * @param binaryData
	 */
	public void setBinaryData(byte[] binaryData) {
		this.binaryData = binaryData;
	}

	/**
	 * Gets the attachmentID value for this BLOB.
	 * 
	 * @return attachmentID
	 */
	public java.lang.String getAttachmentID() {
		return attachmentID;
	}

	/**
	 * Sets the attachmentID value for this BLOB.
	 * 
	 * @param attachmentID
	 */
	public void setAttachmentID(java.lang.String attachmentID) {
		this.attachmentID = attachmentID;
	}

	/**
	 * Gets the remoteURL value for this BLOB.
	 * 
	 * @return remoteURL
	 */
	public java.lang.String getRemoteURL() {
		return remoteURL;
	}

	/**
	 * Sets the remoteURL value for this BLOB.
	 * 
	 * @param remoteURL
	 */
	public void setRemoteURL(java.lang.String remoteURL) {
		this.remoteURL = remoteURL;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof BLOB))
			return false;
		BLOB other = (BLOB) obj;
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
				&& ((this.contentType == null && other.getContentType() == null)
						|| (this.contentType != null && this.contentType.equals(other.getContentType())))
				&& ((this.binaryData == null && other.getBinaryData() == null)
						|| (this.binaryData != null && java.util.Arrays.equals(this.binaryData, other.getBinaryData())))
				&& ((this.attachmentID == null && other.getAttachmentID() == null)
						|| (this.attachmentID != null && this.attachmentID.equals(other.getAttachmentID())))
				&& ((this.remoteURL == null && other.getRemoteURL() == null)
						|| (this.remoteURL != null && this.remoteURL.equals(other.getRemoteURL())));
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
		if (getContentType() != null) {
			_hashCode += getContentType().hashCode();
		}
		if (getBinaryData() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getBinaryData()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getBinaryData(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getAttachmentID() != null) {
			_hashCode += getAttachmentID().hashCode();
		}
		if (getRemoteURL() != null) {
			_hashCode += getRemoteURL().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(BLOB.class,
			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://adobe.com/idp/services", "BLOB"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("contentType");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adobe.com/idp/services", "contentType"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("binaryData");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adobe.com/idp/services", "binaryData"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("attachmentID");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adobe.com/idp/services", "attachmentID"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("remoteURL");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adobe.com/idp/services", "remoteURL"));
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
