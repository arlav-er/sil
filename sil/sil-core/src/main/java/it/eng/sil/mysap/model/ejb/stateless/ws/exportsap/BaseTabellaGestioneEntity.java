/**
 * BaseTabellaGestioneEntity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class BaseTabellaGestioneEntity extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseEntity
		implements java.io.Serializable {
	private java.util.Calendar dtmIns;

	private java.util.Calendar dtmMod;

	private java.lang.Integer idPrincipalIns;

	private java.lang.Integer idPrincipalMod;

	public BaseTabellaGestioneEntity() {
	}

	public BaseTabellaGestioneEntity(java.util.Calendar dtmIns, java.util.Calendar dtmMod,
			java.lang.Integer idPrincipalIns, java.lang.Integer idPrincipalMod) {
		this.dtmIns = dtmIns;
		this.dtmMod = dtmMod;
		this.idPrincipalIns = idPrincipalIns;
		this.idPrincipalMod = idPrincipalMod;
	}

	/**
	 * Gets the dtmIns value for this BaseTabellaGestioneEntity.
	 * 
	 * @return dtmIns
	 */
	public java.util.Calendar getDtmIns() {
		return dtmIns;
	}

	/**
	 * Sets the dtmIns value for this BaseTabellaGestioneEntity.
	 * 
	 * @param dtmIns
	 */
	public void setDtmIns(java.util.Calendar dtmIns) {
		this.dtmIns = dtmIns;
	}

	/**
	 * Gets the dtmMod value for this BaseTabellaGestioneEntity.
	 * 
	 * @return dtmMod
	 */
	public java.util.Calendar getDtmMod() {
		return dtmMod;
	}

	/**
	 * Sets the dtmMod value for this BaseTabellaGestioneEntity.
	 * 
	 * @param dtmMod
	 */
	public void setDtmMod(java.util.Calendar dtmMod) {
		this.dtmMod = dtmMod;
	}

	/**
	 * Gets the idPrincipalIns value for this BaseTabellaGestioneEntity.
	 * 
	 * @return idPrincipalIns
	 */
	public java.lang.Integer getIdPrincipalIns() {
		return idPrincipalIns;
	}

	/**
	 * Sets the idPrincipalIns value for this BaseTabellaGestioneEntity.
	 * 
	 * @param idPrincipalIns
	 */
	public void setIdPrincipalIns(java.lang.Integer idPrincipalIns) {
		this.idPrincipalIns = idPrincipalIns;
	}

	/**
	 * Gets the idPrincipalMod value for this BaseTabellaGestioneEntity.
	 * 
	 * @return idPrincipalMod
	 */
	public java.lang.Integer getIdPrincipalMod() {
		return idPrincipalMod;
	}

	/**
	 * Sets the idPrincipalMod value for this BaseTabellaGestioneEntity.
	 * 
	 * @param idPrincipalMod
	 */
	public void setIdPrincipalMod(java.lang.Integer idPrincipalMod) {
		this.idPrincipalMod = idPrincipalMod;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof BaseTabellaGestioneEntity))
			return false;
		BaseTabellaGestioneEntity other = (BaseTabellaGestioneEntity) obj;
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
				&& ((this.dtmIns == null && other.getDtmIns() == null)
						|| (this.dtmIns != null && this.dtmIns.equals(other.getDtmIns())))
				&& ((this.dtmMod == null && other.getDtmMod() == null)
						|| (this.dtmMod != null && this.dtmMod.equals(other.getDtmMod())))
				&& ((this.idPrincipalIns == null && other.getIdPrincipalIns() == null)
						|| (this.idPrincipalIns != null && this.idPrincipalIns.equals(other.getIdPrincipalIns())))
				&& ((this.idPrincipalMod == null && other.getIdPrincipalMod() == null)
						|| (this.idPrincipalMod != null && this.idPrincipalMod.equals(other.getIdPrincipalMod())));
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
		if (getDtmIns() != null) {
			_hashCode += getDtmIns().hashCode();
		}
		if (getDtmMod() != null) {
			_hashCode += getDtmMod().hashCode();
		}
		if (getIdPrincipalIns() != null) {
			_hashCode += getIdPrincipalIns().hashCode();
		}
		if (getIdPrincipalMod() != null) {
			_hashCode += getIdPrincipalMod().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			BaseTabellaGestioneEntity.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"baseTabellaGestioneEntity"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtmIns");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dtmIns"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtmMod");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dtmMod"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idPrincipalIns");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idPrincipalIns"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idPrincipalMod");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idPrincipalMod"));
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
