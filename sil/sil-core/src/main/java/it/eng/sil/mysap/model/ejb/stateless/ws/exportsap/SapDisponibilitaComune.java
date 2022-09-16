/**
 * SapDisponibilitaComune.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaComune
		extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune;

	private java.lang.Integer idSapDisponibilitaComune;

	public SapDisponibilitaComune() {
	}

	public SapDisponibilitaComune(java.util.Calendar dtmIns, java.util.Calendar dtmMod,
			java.lang.Integer idPrincipalIns, java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune,
			java.lang.Integer idSapDisponibilitaComune) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deComune = deComune;
		this.idSapDisponibilitaComune = idSapDisponibilitaComune;
	}

	/**
	 * Gets the deComune value for this SapDisponibilitaComune.
	 * 
	 * @return deComune
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune getDeComune() {
		return deComune;
	}

	/**
	 * Sets the deComune value for this SapDisponibilitaComune.
	 * 
	 * @param deComune
	 */
	public void setDeComune(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune) {
		this.deComune = deComune;
	}

	/**
	 * Gets the idSapDisponibilitaComune value for this SapDisponibilitaComune.
	 * 
	 * @return idSapDisponibilitaComune
	 */
	public java.lang.Integer getIdSapDisponibilitaComune() {
		return idSapDisponibilitaComune;
	}

	/**
	 * Sets the idSapDisponibilitaComune value for this SapDisponibilitaComune.
	 * 
	 * @param idSapDisponibilitaComune
	 */
	public void setIdSapDisponibilitaComune(java.lang.Integer idSapDisponibilitaComune) {
		this.idSapDisponibilitaComune = idSapDisponibilitaComune;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaComune))
			return false;
		SapDisponibilitaComune other = (SapDisponibilitaComune) obj;
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
				&& ((this.deComune == null && other.getDeComune() == null)
						|| (this.deComune != null && this.deComune.equals(other.getDeComune())))
				&& ((this.idSapDisponibilitaComune == null && other.getIdSapDisponibilitaComune() == null)
						|| (this.idSapDisponibilitaComune != null
								&& this.idSapDisponibilitaComune.equals(other.getIdSapDisponibilitaComune())));
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
		if (getDeComune() != null) {
			_hashCode += getDeComune().hashCode();
		}
		if (getIdSapDisponibilitaComune() != null) {
			_hashCode += getIdSapDisponibilitaComune().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaComune.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaComune"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deComune");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deComune"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deComune"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapDisponibilitaComune");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapDisponibilitaComune"));
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
