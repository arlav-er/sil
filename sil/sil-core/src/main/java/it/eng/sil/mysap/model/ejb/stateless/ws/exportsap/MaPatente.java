/**
 * MaPatente.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class MaPatente extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaMappaturaEntity
		implements java.io.Serializable {
	private java.lang.String codPatente;

	private java.lang.String codPatenteMin;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente patente;

	public MaPatente() {
	}

	public MaPatente(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codPatente,
			java.lang.String codPatenteMin, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente patente) {
		super(dtFineVal, dtInizioVal);
		this.codPatente = codPatente;
		this.codPatenteMin = codPatenteMin;
		this.patente = patente;
	}

	/**
	 * Gets the codPatente value for this MaPatente.
	 * 
	 * @return codPatente
	 */
	public java.lang.String getCodPatente() {
		return codPatente;
	}

	/**
	 * Sets the codPatente value for this MaPatente.
	 * 
	 * @param codPatente
	 */
	public void setCodPatente(java.lang.String codPatente) {
		this.codPatente = codPatente;
	}

	/**
	 * Gets the codPatenteMin value for this MaPatente.
	 * 
	 * @return codPatenteMin
	 */
	public java.lang.String getCodPatenteMin() {
		return codPatenteMin;
	}

	/**
	 * Sets the codPatenteMin value for this MaPatente.
	 * 
	 * @param codPatenteMin
	 */
	public void setCodPatenteMin(java.lang.String codPatenteMin) {
		this.codPatenteMin = codPatenteMin;
	}

	/**
	 * Gets the patente value for this MaPatente.
	 * 
	 * @return patente
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente getPatente() {
		return patente;
	}

	/**
	 * Sets the patente value for this MaPatente.
	 * 
	 * @param patente
	 */
	public void setPatente(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DePatente patente) {
		this.patente = patente;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof MaPatente))
			return false;
		MaPatente other = (MaPatente) obj;
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
				&& ((this.codPatente == null && other.getCodPatente() == null)
						|| (this.codPatente != null && this.codPatente.equals(other.getCodPatente())))
				&& ((this.codPatenteMin == null && other.getCodPatenteMin() == null)
						|| (this.codPatenteMin != null && this.codPatenteMin.equals(other.getCodPatenteMin())))
				&& ((this.patente == null && other.getPatente() == null)
						|| (this.patente != null && this.patente.equals(other.getPatente())));
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
		if (getCodPatente() != null) {
			_hashCode += getCodPatente().hashCode();
		}
		if (getCodPatenteMin() != null) {
			_hashCode += getCodPatenteMin().hashCode();
		}
		if (getPatente() != null) {
			_hashCode += getPatente().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			MaPatente.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"maPatente"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPatente");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codPatente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPatenteMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codPatenteMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("patente");
		elemField.setXmlName(new javax.xml.namespace.QName("", "patente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"dePatente"));
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
