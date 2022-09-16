/**
 * ISEEType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class ISEEType implements java.io.Serializable {
	private java.math.BigDecimal valore;

	private java.util.Date datainizio;

	private java.lang.Integer numannoreddito;

	public ISEEType() {
	}

	public ISEEType(java.math.BigDecimal valore, java.util.Date datainizio, java.lang.Integer numannoreddito) {
		this.valore = valore;
		this.datainizio = datainizio;
		this.numannoreddito = numannoreddito;
	}

	/**
	 * Gets the valore value for this ISEEType.
	 * 
	 * @return valore
	 */
	public java.math.BigDecimal getValore() {
		return valore;
	}

	/**
	 * Sets the valore value for this ISEEType.
	 * 
	 * @param valore
	 */
	public void setValore(java.math.BigDecimal valore) {
		this.valore = valore;
	}

	/**
	 * Gets the datainizio value for this ISEEType.
	 * 
	 * @return datainizio
	 */
	public java.util.Date getDatainizio() {
		return datainizio;
	}

	/**
	 * Sets the datainizio value for this ISEEType.
	 * 
	 * @param datainizio
	 */
	public void setDatainizio(java.util.Date datainizio) {
		this.datainizio = datainizio;
	}

	/**
	 * Gets the numannoreddito value for this ISEEType.
	 * 
	 * @return numannoreddito
	 */
	public java.lang.Integer getNumannoreddito() {
		return numannoreddito;
	}

	/**
	 * Sets the numannoreddito value for this ISEEType.
	 * 
	 * @param numannoreddito
	 */
	public void setNumannoreddito(java.lang.Integer numannoreddito) {
		this.numannoreddito = numannoreddito;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ISEEType))
			return false;
		ISEEType other = (ISEEType) obj;
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
				&& ((this.valore == null && other.getValore() == null)
						|| (this.valore != null && this.valore.equals(other.getValore())))
				&& ((this.datainizio == null && other.getDatainizio() == null)
						|| (this.datainizio != null && this.datainizio.equals(other.getDatainizio())))
				&& ((this.numannoreddito == null && other.getNumannoreddito() == null)
						|| (this.numannoreddito != null && this.numannoreddito.equals(other.getNumannoreddito())));
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
		if (getValore() != null) {
			_hashCode += getValore().hashCode();
		}
		if (getDatainizio() != null) {
			_hashCode += getDatainizio().hashCode();
		}
		if (getNumannoreddito() != null) {
			_hashCode += getNumannoreddito().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ISEEType.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "ISEEType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("valore");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "valore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datainizio");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "datainizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("numannoreddito");
		elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"numannoreddito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
