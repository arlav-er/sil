/**
 * ProtocollazioneRet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

public class ProtocollazioneRet implements java.io.Serializable {
	private long lngNumPG;

	private long lngAnnoPG;

	private java.lang.String strDataPG;

	private long lngErrNumber;

	private java.lang.String strErrString;

	public ProtocollazioneRet() {
	}

	public ProtocollazioneRet(long lngNumPG, long lngAnnoPG, java.lang.String strDataPG, long lngErrNumber,
			java.lang.String strErrString) {
		this.lngNumPG = lngNumPG;
		this.lngAnnoPG = lngAnnoPG;
		this.strDataPG = strDataPG;
		this.lngErrNumber = lngErrNumber;
		this.strErrString = strErrString;
	}

	/**
	 * Gets the lngNumPG value for this ProtocollazioneRet.
	 * 
	 * @return lngNumPG
	 */
	public long getLngNumPG() {
		return lngNumPG;
	}

	/**
	 * Sets the lngNumPG value for this ProtocollazioneRet.
	 * 
	 * @param lngNumPG
	 */
	public void setLngNumPG(long lngNumPG) {
		this.lngNumPG = lngNumPG;
	}

	/**
	 * Gets the lngAnnoPG value for this ProtocollazioneRet.
	 * 
	 * @return lngAnnoPG
	 */
	public long getLngAnnoPG() {
		return lngAnnoPG;
	}

	/**
	 * Sets the lngAnnoPG value for this ProtocollazioneRet.
	 * 
	 * @param lngAnnoPG
	 */
	public void setLngAnnoPG(long lngAnnoPG) {
		this.lngAnnoPG = lngAnnoPG;
	}

	/**
	 * Gets the strDataPG value for this ProtocollazioneRet.
	 * 
	 * @return strDataPG
	 */
	public java.lang.String getStrDataPG() {
		return strDataPG;
	}

	/**
	 * Sets the strDataPG value for this ProtocollazioneRet.
	 * 
	 * @param strDataPG
	 */
	public void setStrDataPG(java.lang.String strDataPG) {
		this.strDataPG = strDataPG;
	}

	/**
	 * Gets the lngErrNumber value for this ProtocollazioneRet.
	 * 
	 * @return lngErrNumber
	 */
	public long getLngErrNumber() {
		return lngErrNumber;
	}

	/**
	 * Sets the lngErrNumber value for this ProtocollazioneRet.
	 * 
	 * @param lngErrNumber
	 */
	public void setLngErrNumber(long lngErrNumber) {
		this.lngErrNumber = lngErrNumber;
	}

	/**
	 * Gets the strErrString value for this ProtocollazioneRet.
	 * 
	 * @return strErrString
	 */
	public java.lang.String getStrErrString() {
		return strErrString;
	}

	/**
	 * Sets the strErrString value for this ProtocollazioneRet.
	 * 
	 * @param strErrString
	 */
	public void setStrErrString(java.lang.String strErrString) {
		this.strErrString = strErrString;
	}

	public String toString() {
		return new StringBuffer("ProtocollazioneRet: ").append(", lngAnnoPG=").append(this.lngAnnoPG)
				.append(", lngNumPG=").append(this.lngNumPG).append(", strDataPG=").append(this.strDataPG)
				.append(", strErrString=").append(this.strErrString).append(", lngErrNumber=").append(this.lngErrNumber)
				.toString();
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ProtocollazioneRet))
			return false;
		ProtocollazioneRet other = (ProtocollazioneRet) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.lngNumPG == other.getLngNumPG() && this.lngAnnoPG == other.getLngAnnoPG()
				&& ((this.strDataPG == null && other.getStrDataPG() == null)
						|| (this.strDataPG != null && this.strDataPG.equals(other.getStrDataPG())))
				&& this.lngErrNumber == other.getLngErrNumber()
				&& ((this.strErrString == null && other.getStrErrString() == null)
						|| (this.strErrString != null && this.strErrString.equals(other.getStrErrString())));
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
		_hashCode += new Long(getLngNumPG()).hashCode();
		_hashCode += new Long(getLngAnnoPG()).hashCode();
		if (getStrDataPG() != null) {
			_hashCode += getStrDataPG().hashCode();
		}
		_hashCode += new Long(getLngErrNumber()).hashCode();
		if (getStrErrString() != null) {
			_hashCode += getStrErrString().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ProtocollazioneRet.class, true);
	// configurazione namespace della busta soap
	private static String targetNamespaceResponse = DocAreaWSConfig.getTargetNamespaceResponseType();

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(targetNamespaceResponse, "ProtocollazioneRet"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lngNumPG");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespaceResponse, "lngNumPG"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lngAnnoPG");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespaceResponse, "lngAnnoPG"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strDataPG");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespaceResponse, "strDataPG"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lngErrNumber");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespaceResponse, "lngErrNumber"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strErrString");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespaceResponse, "strErrString"));
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
