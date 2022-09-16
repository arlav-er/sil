/**
 * SapPatente.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPatente extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente dePatente;

	private java.lang.Integer idSapPatente;

	public SapPatente() {
	}

	public SapPatente(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente dePatente,
			java.lang.Integer idSapPatente) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.dePatente = dePatente;
		this.idSapPatente = idSapPatente;
	}

	/**
	 * Gets the dePatente value for this SapPatente.
	 * 
	 * @return dePatente
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente getDePatente() {
		return dePatente;
	}

	/**
	 * Sets the dePatente value for this SapPatente.
	 * 
	 * @param dePatente
	 */
	public void setDePatente(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente dePatente) {
		this.dePatente = dePatente;
	}

	/**
	 * Gets the idSapPatente value for this SapPatente.
	 * 
	 * @return idSapPatente
	 */
	public java.lang.Integer getIdSapPatente() {
		return idSapPatente;
	}

	/**
	 * Sets the idSapPatente value for this SapPatente.
	 * 
	 * @param idSapPatente
	 */
	public void setIdSapPatente(java.lang.Integer idSapPatente) {
		this.idSapPatente = idSapPatente;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPatente))
			return false;
		SapPatente other = (SapPatente) obj;
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
				&& ((this.dePatente == null && other.getDePatente() == null)
						|| (this.dePatente != null && this.dePatente.equals(other.getDePatente())))
				&& ((this.idSapPatente == null && other.getIdSapPatente() == null)
						|| (this.idSapPatente != null && this.idSapPatente.equals(other.getIdSapPatente())));
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
		if (getDePatente() != null) {
			_hashCode += getDePatente().hashCode();
		}
		if (getIdSapPatente() != null) {
			_hashCode += getIdSapPatente().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPatente.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatente"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dePatente");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dePatente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"dePatente"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapPatente");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapPatente"));
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
