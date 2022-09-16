/**
 * SapDisponibilitaOrario.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaOrario
		extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario deOrario;

	private java.lang.Integer idSapDisponibilitaOrario;

	public SapDisponibilitaOrario() {
	}

	public SapDisponibilitaOrario(java.util.Calendar dtmIns, java.util.Calendar dtmMod,
			java.lang.Integer idPrincipalIns, java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario deOrario,
			java.lang.Integer idSapDisponibilitaOrario) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deOrario = deOrario;
		this.idSapDisponibilitaOrario = idSapDisponibilitaOrario;
	}

	/**
	 * Gets the deOrario value for this SapDisponibilitaOrario.
	 * 
	 * @return deOrario
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario getDeOrario() {
		return deOrario;
	}

	/**
	 * Sets the deOrario value for this SapDisponibilitaOrario.
	 * 
	 * @param deOrario
	 */
	public void setDeOrario(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario deOrario) {
		this.deOrario = deOrario;
	}

	/**
	 * Gets the idSapDisponibilitaOrario value for this SapDisponibilitaOrario.
	 * 
	 * @return idSapDisponibilitaOrario
	 */
	public java.lang.Integer getIdSapDisponibilitaOrario() {
		return idSapDisponibilitaOrario;
	}

	/**
	 * Sets the idSapDisponibilitaOrario value for this SapDisponibilitaOrario.
	 * 
	 * @param idSapDisponibilitaOrario
	 */
	public void setIdSapDisponibilitaOrario(java.lang.Integer idSapDisponibilitaOrario) {
		this.idSapDisponibilitaOrario = idSapDisponibilitaOrario;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaOrario))
			return false;
		SapDisponibilitaOrario other = (SapDisponibilitaOrario) obj;
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
				&& ((this.deOrario == null && other.getDeOrario() == null)
						|| (this.deOrario != null && this.deOrario.equals(other.getDeOrario())))
				&& ((this.idSapDisponibilitaOrario == null && other.getIdSapDisponibilitaOrario() == null)
						|| (this.idSapDisponibilitaOrario != null
								&& this.idSapDisponibilitaOrario.equals(other.getIdSapDisponibilitaOrario())));
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
		if (getDeOrario() != null) {
			_hashCode += getDeOrario().hashCode();
		}
		if (getIdSapDisponibilitaOrario() != null) {
			_hashCode += getIdSapDisponibilitaOrario().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaOrario.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaOrario"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deOrario"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deOrario"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapDisponibilitaOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapDisponibilitaOrario"));
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
