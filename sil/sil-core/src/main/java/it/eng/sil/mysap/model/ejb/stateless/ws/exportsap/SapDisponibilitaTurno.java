/**
 * SapDisponibilitaTurno.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaTurno
		extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTurno deTurno;

	private java.lang.Integer idSapDisponibilitaTurno;

	public SapDisponibilitaTurno() {
	}

	public SapDisponibilitaTurno(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTurno deTurno,
			java.lang.Integer idSapDisponibilitaTurno) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deTurno = deTurno;
		this.idSapDisponibilitaTurno = idSapDisponibilitaTurno;
	}

	/**
	 * Gets the deTurno value for this SapDisponibilitaTurno.
	 * 
	 * @return deTurno
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTurno getDeTurno() {
		return deTurno;
	}

	/**
	 * Sets the deTurno value for this SapDisponibilitaTurno.
	 * 
	 * @param deTurno
	 */
	public void setDeTurno(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTurno deTurno) {
		this.deTurno = deTurno;
	}

	/**
	 * Gets the idSapDisponibilitaTurno value for this SapDisponibilitaTurno.
	 * 
	 * @return idSapDisponibilitaTurno
	 */
	public java.lang.Integer getIdSapDisponibilitaTurno() {
		return idSapDisponibilitaTurno;
	}

	/**
	 * Sets the idSapDisponibilitaTurno value for this SapDisponibilitaTurno.
	 * 
	 * @param idSapDisponibilitaTurno
	 */
	public void setIdSapDisponibilitaTurno(java.lang.Integer idSapDisponibilitaTurno) {
		this.idSapDisponibilitaTurno = idSapDisponibilitaTurno;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaTurno))
			return false;
		SapDisponibilitaTurno other = (SapDisponibilitaTurno) obj;
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
				&& ((this.deTurno == null && other.getDeTurno() == null)
						|| (this.deTurno != null && this.deTurno.equals(other.getDeTurno())))
				&& ((this.idSapDisponibilitaTurno == null && other.getIdSapDisponibilitaTurno() == null)
						|| (this.idSapDisponibilitaTurno != null
								&& this.idSapDisponibilitaTurno.equals(other.getIdSapDisponibilitaTurno())));
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
		if (getDeTurno() != null) {
			_hashCode += getDeTurno().hashCode();
		}
		if (getIdSapDisponibilitaTurno() != null) {
			_hashCode += getIdSapDisponibilitaTurno().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaTurno.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaTurno"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deTurno");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deTurno"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deTurno"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapDisponibilitaTurno");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapDisponibilitaTurno"));
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
