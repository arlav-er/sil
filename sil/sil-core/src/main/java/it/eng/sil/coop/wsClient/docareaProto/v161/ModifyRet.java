/**
 * ModifyRet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto.v161;

public class ModifyRet implements java.io.Serializable {
	private java.lang.String strLibraryName;

	private long lngDocID;

	private long lngErrNumber;

	private java.lang.String strErrString;

	private java.lang.String strErrorFilePath;

	public ModifyRet() {
	}

	public ModifyRet(java.lang.String strLibraryName, long lngDocID, long lngErrNumber, java.lang.String strErrString,
			java.lang.String strErrorFilePath) {
		this.strLibraryName = strLibraryName;
		this.lngDocID = lngDocID;
		this.lngErrNumber = lngErrNumber;
		this.strErrString = strErrString;
		this.strErrorFilePath = strErrorFilePath;
	}

	/**
	 * Gets the strLibraryName value for this ModifyRet.
	 * 
	 * @return strLibraryName
	 */
	public java.lang.String getStrLibraryName() {
		return strLibraryName;
	}

	/**
	 * Sets the strLibraryName value for this ModifyRet.
	 * 
	 * @param strLibraryName
	 */
	public void setStrLibraryName(java.lang.String strLibraryName) {
		this.strLibraryName = strLibraryName;
	}

	/**
	 * Gets the lngDocID value for this ModifyRet.
	 * 
	 * @return lngDocID
	 */
	public long getLngDocID() {
		return lngDocID;
	}

	/**
	 * Sets the lngDocID value for this ModifyRet.
	 * 
	 * @param lngDocID
	 */
	public void setLngDocID(long lngDocID) {
		this.lngDocID = lngDocID;
	}

	/**
	 * Gets the lngErrNumber value for this ModifyRet.
	 * 
	 * @return lngErrNumber
	 */
	public long getLngErrNumber() {
		return lngErrNumber;
	}

	/**
	 * Sets the lngErrNumber value for this ModifyRet.
	 * 
	 * @param lngErrNumber
	 */
	public void setLngErrNumber(long lngErrNumber) {
		this.lngErrNumber = lngErrNumber;
	}

	/**
	 * Gets the strErrString value for this ModifyRet.
	 * 
	 * @return strErrString
	 */
	public java.lang.String getStrErrString() {
		return strErrString;
	}

	/**
	 * Sets the strErrString value for this ModifyRet.
	 * 
	 * @param strErrString
	 */
	public void setStrErrString(java.lang.String strErrString) {
		this.strErrString = strErrString;
	}

	/**
	 * Gets the strErrorFilePath value for this ModifyRet.
	 * 
	 * @return strErrorFilePath
	 */
	public java.lang.String getStrErrorFilePath() {
		return strErrorFilePath;
	}

	/**
	 * Sets the strErrorFilePath value for this ModifyRet.
	 * 
	 * @param strErrorFilePath
	 */
	public void setStrErrorFilePath(java.lang.String strErrorFilePath) {
		this.strErrorFilePath = strErrorFilePath;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ModifyRet))
			return false;
		ModifyRet other = (ModifyRet) obj;
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
				&& ((this.strLibraryName == null && other.getStrLibraryName() == null)
						|| (this.strLibraryName != null && this.strLibraryName.equals(other.getStrLibraryName())))
				&& this.lngDocID == other.getLngDocID() && this.lngErrNumber == other.getLngErrNumber()
				&& ((this.strErrString == null && other.getStrErrString() == null)
						|| (this.strErrString != null && this.strErrString.equals(other.getStrErrString())))
				&& ((this.strErrorFilePath == null && other.getStrErrorFilePath() == null)
						|| (this.strErrorFilePath != null
								&& this.strErrorFilePath.equals(other.getStrErrorFilePath())));
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
		if (getStrLibraryName() != null) {
			_hashCode += getStrLibraryName().hashCode();
		}
		_hashCode += new Long(getLngDocID()).hashCode();
		_hashCode += new Long(getLngErrNumber()).hashCode();
		if (getStrErrString() != null) {
			_hashCode += getStrErrString().hashCode();
		}
		if (getStrErrorFilePath() != null) {
			_hashCode += getStrErrorFilePath().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ModifyRet.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "ModifyRet"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strLibraryName");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "StrLibraryName"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lngDocID");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "LngDocID"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lngErrNumber");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "LngErrNumber"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strErrString");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "StrErrString"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strErrorFilePath");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "StrErrorFilePath"));
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
