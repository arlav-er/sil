/**
 * Esito.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class Esito implements java.io.Serializable {
	private long errNum;

	private java.lang.String errDescription;

	public Esito() {
	}

	public Esito(long errNum, java.lang.String errDescription) {
		this.errNum = errNum;
		this.errDescription = errDescription;
	}

	/**
	 * Gets the errNum value for this Esito.
	 * 
	 * @return errNum
	 */
	public long getErrNum() {
		return errNum;
	}

	/**
	 * Sets the errNum value for this Esito.
	 * 
	 * @param errNum
	 */
	public void setErrNum(long errNum) {
		this.errNum = errNum;
	}

	/**
	 * Gets the errDescription value for this Esito.
	 * 
	 * @return errDescription
	 */
	public java.lang.String getErrDescription() {
		return errDescription;
	}

	/**
	 * Sets the errDescription value for this Esito.
	 * 
	 * @param errDescription
	 */
	public void setErrDescription(java.lang.String errDescription) {
		this.errDescription = errDescription;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Esito))
			return false;
		Esito other = (Esito) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.errNum == other.getErrNum()
				&& ((this.errDescription == null && other.getErrDescription() == null)
						|| (this.errDescription != null && this.errDescription.equals(other.getErrDescription())));
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
		_hashCode += new Long(getErrNum()).hashCode();
		if (getErrDescription() != null) {
			_hashCode += getErrDescription().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(Esito.class,
			true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "esito"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("errNum");
		elemField.setXmlName(new javax.xml.namespace.QName("", "errNum"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("errDescription");
		elemField.setXmlName(new javax.xml.namespace.QName("", "errDescription"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
