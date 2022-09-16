/**
 * SapPropModLavoro.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPropModLavoro extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeModalitaLavoro deModalitaLavoro;

	private java.lang.Integer idSapPropModLavoro;

	public SapPropModLavoro() {
	}

	public SapPropModLavoro(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeModalitaLavoro deModalitaLavoro,
			java.lang.Integer idSapPropModLavoro) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deModalitaLavoro = deModalitaLavoro;
		this.idSapPropModLavoro = idSapPropModLavoro;
	}

	/**
	 * Gets the deModalitaLavoro value for this SapPropModLavoro.
	 * 
	 * @return deModalitaLavoro
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeModalitaLavoro getDeModalitaLavoro() {
		return deModalitaLavoro;
	}

	/**
	 * Sets the deModalitaLavoro value for this SapPropModLavoro.
	 * 
	 * @param deModalitaLavoro
	 */
	public void setDeModalitaLavoro(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeModalitaLavoro deModalitaLavoro) {
		this.deModalitaLavoro = deModalitaLavoro;
	}

	/**
	 * Gets the idSapPropModLavoro value for this SapPropModLavoro.
	 * 
	 * @return idSapPropModLavoro
	 */
	public java.lang.Integer getIdSapPropModLavoro() {
		return idSapPropModLavoro;
	}

	/**
	 * Sets the idSapPropModLavoro value for this SapPropModLavoro.
	 * 
	 * @param idSapPropModLavoro
	 */
	public void setIdSapPropModLavoro(java.lang.Integer idSapPropModLavoro) {
		this.idSapPropModLavoro = idSapPropModLavoro;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPropModLavoro))
			return false;
		SapPropModLavoro other = (SapPropModLavoro) obj;
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
				&& ((this.deModalitaLavoro == null && other.getDeModalitaLavoro() == null)
						|| (this.deModalitaLavoro != null && this.deModalitaLavoro.equals(other.getDeModalitaLavoro())))
				&& ((this.idSapPropModLavoro == null && other.getIdSapPropModLavoro() == null)
						|| (this.idSapPropModLavoro != null
								&& this.idSapPropModLavoro.equals(other.getIdSapPropModLavoro())));
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
		if (getDeModalitaLavoro() != null) {
			_hashCode += getDeModalitaLavoro().hashCode();
		}
		if (getIdSapPropModLavoro() != null) {
			_hashCode += getIdSapPropModLavoro().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPropModLavoro.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropModLavoro"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deModalitaLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deModalitaLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deModalitaLavoro"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapPropModLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapPropModLavoro"));
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
