/**
 * Delete.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto.v161;

public class Delete implements java.io.Serializable {
	private java.lang.String strLibraryName;

	private java.lang.String lngDocID;

	private java.lang.String strUserName;

	private java.lang.String strDST;

	private java.lang.String intDeleteType;

	public Delete() {
	}

	public Delete(java.lang.String strLibraryName, java.lang.String lngDocID, java.lang.String strUserName,
			java.lang.String strDST, java.lang.String intDeleteType) {
		this.strLibraryName = strLibraryName;
		this.lngDocID = lngDocID;
		this.strUserName = strUserName;
		this.strDST = strDST;
		this.intDeleteType = intDeleteType;
	}

	/**
	 * Gets the strLibraryName value for this Delete.
	 * 
	 * @return strLibraryName
	 */
	public java.lang.String getStrLibraryName() {
		return strLibraryName;
	}

	/**
	 * Sets the strLibraryName value for this Delete.
	 * 
	 * @param strLibraryName
	 */
	public void setStrLibraryName(java.lang.String strLibraryName) {
		this.strLibraryName = strLibraryName;
	}

	/**
	 * Gets the lngDocID value for this Delete.
	 * 
	 * @return lngDocID
	 */
	public java.lang.String getLngDocID() {
		return lngDocID;
	}

	/**
	 * Sets the lngDocID value for this Delete.
	 * 
	 * @param lngDocID
	 */
	public void setLngDocID(java.lang.String lngDocID) {
		this.lngDocID = lngDocID;
	}

	/**
	 * Gets the strUserName value for this Delete.
	 * 
	 * @return strUserName
	 */
	public java.lang.String getStrUserName() {
		return strUserName;
	}

	/**
	 * Sets the strUserName value for this Delete.
	 * 
	 * @param strUserName
	 */
	public void setStrUserName(java.lang.String strUserName) {
		this.strUserName = strUserName;
	}

	/**
	 * Gets the strDST value for this Delete.
	 * 
	 * @return strDST
	 */
	public java.lang.String getStrDST() {
		return strDST;
	}

	/**
	 * Sets the strDST value for this Delete.
	 * 
	 * @param strDST
	 */
	public void setStrDST(java.lang.String strDST) {
		this.strDST = strDST;
	}

	/**
	 * Gets the intDeleteType value for this Delete.
	 * 
	 * @return intDeleteType
	 */
	public java.lang.String getIntDeleteType() {
		return intDeleteType;
	}

	/**
	 * Sets the intDeleteType value for this Delete.
	 * 
	 * @param intDeleteType
	 */
	public void setIntDeleteType(java.lang.String intDeleteType) {
		this.intDeleteType = intDeleteType;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Delete))
			return false;
		Delete other = (Delete) obj;
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
				&& ((this.lngDocID == null && other.getLngDocID() == null)
						|| (this.lngDocID != null && this.lngDocID.equals(other.getLngDocID())))
				&& ((this.strUserName == null && other.getStrUserName() == null)
						|| (this.strUserName != null && this.strUserName.equals(other.getStrUserName())))
				&& ((this.strDST == null && other.getStrDST() == null)
						|| (this.strDST != null && this.strDST.equals(other.getStrDST())))
				&& ((this.intDeleteType == null && other.getIntDeleteType() == null)
						|| (this.intDeleteType != null && this.intDeleteType.equals(other.getIntDeleteType())));
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
		if (getLngDocID() != null) {
			_hashCode += getLngDocID().hashCode();
		}
		if (getStrUserName() != null) {
			_hashCode += getStrUserName().hashCode();
		}
		if (getStrDST() != null) {
			_hashCode += getStrDST().hashCode();
		}
		if (getIntDeleteType() != null) {
			_hashCode += getIntDeleteType().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Delete.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">Delete"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strLibraryName");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "strLibraryName"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lngDocID");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "lngDocID"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strUserName");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "strUserName"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strDST");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "strDST"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("intDeleteType");
		elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "intDeleteType"));
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
