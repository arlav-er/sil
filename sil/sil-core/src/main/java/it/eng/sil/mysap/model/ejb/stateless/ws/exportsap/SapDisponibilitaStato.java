/**
 * SapDisponibilitaStato.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaStato
		extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune;

	private java.lang.Integer idSapDisponibilitaStato;

	public SapDisponibilitaStato() {
	}

	public SapDisponibilitaStato(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune,
			java.lang.Integer idSapDisponibilitaStato) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deComune = deComune;
		this.idSapDisponibilitaStato = idSapDisponibilitaStato;
	}

	/**
	 * Gets the deComune value for this SapDisponibilitaStato.
	 * 
	 * @return deComune
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune getDeComune() {
		return deComune;
	}

	/**
	 * Sets the deComune value for this SapDisponibilitaStato.
	 * 
	 * @param deComune
	 */
	public void setDeComune(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune) {
		this.deComune = deComune;
	}

	/**
	 * Gets the idSapDisponibilitaStato value for this SapDisponibilitaStato.
	 * 
	 * @return idSapDisponibilitaStato
	 */
	public java.lang.Integer getIdSapDisponibilitaStato() {
		return idSapDisponibilitaStato;
	}

	/**
	 * Sets the idSapDisponibilitaStato value for this SapDisponibilitaStato.
	 * 
	 * @param idSapDisponibilitaStato
	 */
	public void setIdSapDisponibilitaStato(java.lang.Integer idSapDisponibilitaStato) {
		this.idSapDisponibilitaStato = idSapDisponibilitaStato;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaStato))
			return false;
		SapDisponibilitaStato other = (SapDisponibilitaStato) obj;
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
				&& ((this.idSapDisponibilitaStato == null && other.getIdSapDisponibilitaStato() == null)
						|| (this.idSapDisponibilitaStato != null
								&& this.idSapDisponibilitaStato.equals(other.getIdSapDisponibilitaStato())));
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
		if (getIdSapDisponibilitaStato() != null) {
			_hashCode += getIdSapDisponibilitaStato().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaStato.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaStato"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deComune");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deComune"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deComune"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapDisponibilitaStato");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapDisponibilitaStato"));
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
