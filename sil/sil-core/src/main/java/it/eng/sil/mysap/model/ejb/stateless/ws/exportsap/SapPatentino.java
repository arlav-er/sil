/**
 * SapPatentino.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPatentino extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatentino dePatentino;

	private java.lang.Integer idSapPatentino;

	public SapPatentino() {
	}

	public SapPatentino(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatentino dePatentino,
			java.lang.Integer idSapPatentino) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.dePatentino = dePatentino;
		this.idSapPatentino = idSapPatentino;
	}

	/**
	 * Gets the dePatentino value for this SapPatentino.
	 * 
	 * @return dePatentino
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatentino getDePatentino() {
		return dePatentino;
	}

	/**
	 * Sets the dePatentino value for this SapPatentino.
	 * 
	 * @param dePatentino
	 */
	public void setDePatentino(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatentino dePatentino) {
		this.dePatentino = dePatentino;
	}

	/**
	 * Gets the idSapPatentino value for this SapPatentino.
	 * 
	 * @return idSapPatentino
	 */
	public java.lang.Integer getIdSapPatentino() {
		return idSapPatentino;
	}

	/**
	 * Sets the idSapPatentino value for this SapPatentino.
	 * 
	 * @param idSapPatentino
	 */
	public void setIdSapPatentino(java.lang.Integer idSapPatentino) {
		this.idSapPatentino = idSapPatentino;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPatentino))
			return false;
		SapPatentino other = (SapPatentino) obj;
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
				&& ((this.dePatentino == null && other.getDePatentino() == null)
						|| (this.dePatentino != null && this.dePatentino.equals(other.getDePatentino())))
				&& ((this.idSapPatentino == null && other.getIdSapPatentino() == null)
						|| (this.idSapPatentino != null && this.idSapPatentino.equals(other.getIdSapPatentino())));
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
		if (getDePatentino() != null) {
			_hashCode += getDePatentino().hashCode();
		}
		if (getIdSapPatentino() != null) {
			_hashCode += getIdSapPatentino().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPatentino.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatentino"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dePatentino");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dePatentino"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"dePatentino"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapPatentino");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapPatentino"));
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
