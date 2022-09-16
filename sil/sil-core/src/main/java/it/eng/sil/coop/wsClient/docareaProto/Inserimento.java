/**
 * Inserimento.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

public class Inserimento implements java.io.Serializable {
	private java.lang.String strUserName;

	private java.lang.String strDST;

	public Inserimento() {
	}

	public Inserimento(java.lang.String strUserName, java.lang.String strDST) {
		this.strUserName = strUserName;
		this.strDST = strDST;
	}

	/**
	 * Gets the strUserName value for this Inserimento.
	 * 
	 * @return strUserName
	 */
	public java.lang.String getStrUserName() {
		return strUserName;
	}

	/**
	 * Sets the strUserName value for this Inserimento.
	 * 
	 * @param strUserName
	 */
	public void setStrUserName(java.lang.String strUserName) {
		this.strUserName = strUserName;
	}

	/**
	 * Gets the strDST value for this Inserimento.
	 * 
	 * @return strDST
	 */
	public java.lang.String getStrDST() {
		return strDST;
	}

	/**
	 * Sets the strDST value for this Inserimento.
	 * 
	 * @param strDST
	 */
	public void setStrDST(java.lang.String strDST) {
		this.strDST = strDST;
	}

	public String toString() {
		return new StringBuffer("Inserimento: ").append(", strUserName=").append(this.strUserName).append(", strDST=")
				.append(this.strDST).toString();
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Inserimento))
			return false;
		Inserimento other = (Inserimento) obj;
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
				&& ((this.strUserName == null && other.getStrUserName() == null)
						|| (this.strUserName != null && this.strUserName.equals(other.getStrUserName())))
				&& ((this.strDST == null && other.getStrDST() == null)
						|| (this.strDST != null && this.strDST.equals(other.getStrDST())));
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
		if (getStrUserName() != null) {
			_hashCode += getStrUserName().hashCode();
		}
		if (getStrDST() != null) {
			_hashCode += getStrDST().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Inserimento.class, true);

	// configurazione namespace della busta soap
	private static String targetNamespace = DocAreaWSConfig.getTargetNamespace();

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(targetNamespace, ">Inserimento"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strUserName");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespace, "strUserName"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strDST");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespace, "strDST"));
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
