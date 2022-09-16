/**
 * SapAlbo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapAlbo extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAlbo deAlbo;

	private java.lang.Integer idSapAlbo;

	public SapAlbo() {
	}

	public SapAlbo(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAlbo deAlbo,
			java.lang.Integer idSapAlbo) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deAlbo = deAlbo;
		this.idSapAlbo = idSapAlbo;
	}

	/**
	 * Gets the deAlbo value for this SapAlbo.
	 * 
	 * @return deAlbo
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAlbo getDeAlbo() {
		return deAlbo;
	}

	/**
	 * Sets the deAlbo value for this SapAlbo.
	 * 
	 * @param deAlbo
	 */
	public void setDeAlbo(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAlbo deAlbo) {
		this.deAlbo = deAlbo;
	}

	/**
	 * Gets the idSapAlbo value for this SapAlbo.
	 * 
	 * @return idSapAlbo
	 */
	public java.lang.Integer getIdSapAlbo() {
		return idSapAlbo;
	}

	/**
	 * Sets the idSapAlbo value for this SapAlbo.
	 * 
	 * @param idSapAlbo
	 */
	public void setIdSapAlbo(java.lang.Integer idSapAlbo) {
		this.idSapAlbo = idSapAlbo;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapAlbo))
			return false;
		SapAlbo other = (SapAlbo) obj;
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
				&& ((this.deAlbo == null && other.getDeAlbo() == null)
						|| (this.deAlbo != null && this.deAlbo.equals(other.getDeAlbo())))
				&& ((this.idSapAlbo == null && other.getIdSapAlbo() == null)
						|| (this.idSapAlbo != null && this.idSapAlbo.equals(other.getIdSapAlbo())));
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
		if (getDeAlbo() != null) {
			_hashCode += getDeAlbo().hashCode();
		}
		if (getIdSapAlbo() != null) {
			_hashCode += getIdSapAlbo().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapAlbo.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "sapAlbo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deAlbo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deAlbo"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deAlbo"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapAlbo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapAlbo"));
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
