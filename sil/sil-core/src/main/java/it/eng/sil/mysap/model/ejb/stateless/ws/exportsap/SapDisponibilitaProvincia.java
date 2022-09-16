/**
 * SapDisponibilitaProvincia.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaProvincia
		extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia;

	private java.lang.Integer idSapDisponibilitaProvincia;

	public SapDisponibilitaProvincia() {
	}

	public SapDisponibilitaProvincia(java.util.Calendar dtmIns, java.util.Calendar dtmMod,
			java.lang.Integer idPrincipalIns, java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia,
			java.lang.Integer idSapDisponibilitaProvincia) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deProvincia = deProvincia;
		this.idSapDisponibilitaProvincia = idSapDisponibilitaProvincia;
	}

	/**
	 * Gets the deProvincia value for this SapDisponibilitaProvincia.
	 * 
	 * @return deProvincia
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia getDeProvincia() {
		return deProvincia;
	}

	/**
	 * Sets the deProvincia value for this SapDisponibilitaProvincia.
	 * 
	 * @param deProvincia
	 */
	public void setDeProvincia(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	/**
	 * Gets the idSapDisponibilitaProvincia value for this SapDisponibilitaProvincia.
	 * 
	 * @return idSapDisponibilitaProvincia
	 */
	public java.lang.Integer getIdSapDisponibilitaProvincia() {
		return idSapDisponibilitaProvincia;
	}

	/**
	 * Sets the idSapDisponibilitaProvincia value for this SapDisponibilitaProvincia.
	 * 
	 * @param idSapDisponibilitaProvincia
	 */
	public void setIdSapDisponibilitaProvincia(java.lang.Integer idSapDisponibilitaProvincia) {
		this.idSapDisponibilitaProvincia = idSapDisponibilitaProvincia;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaProvincia))
			return false;
		SapDisponibilitaProvincia other = (SapDisponibilitaProvincia) obj;
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
				&& ((this.deProvincia == null && other.getDeProvincia() == null)
						|| (this.deProvincia != null && this.deProvincia.equals(other.getDeProvincia())))
				&& ((this.idSapDisponibilitaProvincia == null && other.getIdSapDisponibilitaProvincia() == null)
						|| (this.idSapDisponibilitaProvincia != null
								&& this.idSapDisponibilitaProvincia.equals(other.getIdSapDisponibilitaProvincia())));
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
		if (getDeProvincia() != null) {
			_hashCode += getDeProvincia().hashCode();
		}
		if (getIdSapDisponibilitaProvincia() != null) {
			_hashCode += getIdSapDisponibilitaProvincia().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaProvincia.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaProvincia"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deProvincia");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deProvincia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deProvincia"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapDisponibilitaProvincia");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapDisponibilitaProvincia"));
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
