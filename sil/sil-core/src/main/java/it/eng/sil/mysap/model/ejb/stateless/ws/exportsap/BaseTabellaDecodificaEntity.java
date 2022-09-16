/**
 * BaseTabellaDecodificaEntity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public abstract class BaseTabellaDecodificaEntity extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseEntity
		implements java.io.Serializable {
	private java.util.Calendar dtFineVal;

	private java.util.Calendar dtInizioVal;

	public BaseTabellaDecodificaEntity() {
	}

	public BaseTabellaDecodificaEntity(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal) {
		this.dtFineVal = dtFineVal;
		this.dtInizioVal = dtInizioVal;
	}

	/**
	 * Gets the dtFineVal value for this BaseTabellaDecodificaEntity.
	 * 
	 * @return dtFineVal
	 */
	public java.util.Calendar getDtFineVal() {
		return dtFineVal;
	}

	/**
	 * Sets the dtFineVal value for this BaseTabellaDecodificaEntity.
	 * 
	 * @param dtFineVal
	 */
	public void setDtFineVal(java.util.Calendar dtFineVal) {
		this.dtFineVal = dtFineVal;
	}

	/**
	 * Gets the dtInizioVal value for this BaseTabellaDecodificaEntity.
	 * 
	 * @return dtInizioVal
	 */
	public java.util.Calendar getDtInizioVal() {
		return dtInizioVal;
	}

	/**
	 * Sets the dtInizioVal value for this BaseTabellaDecodificaEntity.
	 * 
	 * @param dtInizioVal
	 */
	public void setDtInizioVal(java.util.Calendar dtInizioVal) {
		this.dtInizioVal = dtInizioVal;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof BaseTabellaDecodificaEntity))
			return false;
		BaseTabellaDecodificaEntity other = (BaseTabellaDecodificaEntity) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj)
				&& ((this.dtFineVal == null && other.getDtFineVal() == null)
						|| (this.dtFineVal != null && this.dtFineVal.equals(other.getDtFineVal())))
				&& ((this.dtInizioVal == null && other.getDtInizioVal() == null)
						|| (this.dtInizioVal != null && this.dtInizioVal.equals(other.getDtInizioVal())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = super.hashCode();
		if (getDtFineVal() != null) {
			_hashCode += getDtFineVal().hashCode();
		}
		if (getDtInizioVal() != null) {
			_hashCode += getDtInizioVal().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			BaseTabellaDecodificaEntity.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"baseTabellaDecodificaEntity"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtFineVal");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dtFineVal"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtInizioVal");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dtInizioVal"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
