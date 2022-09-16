/**
 * SapDisponibilitaTurnoDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaTurnoDTO implements java.io.Serializable {
	private java.lang.String codTurno;

	private java.lang.String codTurnoDesc;

	public SapDisponibilitaTurnoDTO() {
	}

	public SapDisponibilitaTurnoDTO(java.lang.String codTurno, java.lang.String codTurnoDesc) {
		this.codTurno = codTurno;
		this.codTurnoDesc = codTurnoDesc;
	}

	/**
	 * Gets the codTurno value for this SapDisponibilitaTurnoDTO.
	 * 
	 * @return codTurno
	 */
	public java.lang.String getCodTurno() {
		return codTurno;
	}

	/**
	 * Sets the codTurno value for this SapDisponibilitaTurnoDTO.
	 * 
	 * @param codTurno
	 */
	public void setCodTurno(java.lang.String codTurno) {
		this.codTurno = codTurno;
	}

	/**
	 * Gets the codTurnoDesc value for this SapDisponibilitaTurnoDTO.
	 * 
	 * @return codTurnoDesc
	 */
	public java.lang.String getCodTurnoDesc() {
		return codTurnoDesc;
	}

	/**
	 * Sets the codTurnoDesc value for this SapDisponibilitaTurnoDTO.
	 * 
	 * @param codTurnoDesc
	 */
	public void setCodTurnoDesc(java.lang.String codTurnoDesc) {
		this.codTurnoDesc = codTurnoDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaTurnoDTO))
			return false;
		SapDisponibilitaTurnoDTO other = (SapDisponibilitaTurnoDTO) obj;
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
				&& ((this.codTurno == null && other.getCodTurno() == null)
						|| (this.codTurno != null && this.codTurno.equals(other.getCodTurno())))
				&& ((this.codTurnoDesc == null && other.getCodTurnoDesc() == null)
						|| (this.codTurnoDesc != null && this.codTurnoDesc.equals(other.getCodTurnoDesc())));
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
		if (getCodTurno() != null) {
			_hashCode += getCodTurno().hashCode();
		}
		if (getCodTurnoDesc() != null) {
			_hashCode += getCodTurnoDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaTurnoDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaTurnoDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codTurno");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codTurno"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codTurnoDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codTurnoDesc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
