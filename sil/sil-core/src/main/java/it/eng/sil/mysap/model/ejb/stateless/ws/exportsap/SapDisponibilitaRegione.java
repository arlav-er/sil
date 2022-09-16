/**
 * SapDisponibilitaRegione.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaRegione
		extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione deRegione;

	private java.lang.Integer idSapDisponibilitaRegione;

	public SapDisponibilitaRegione() {
	}

	public SapDisponibilitaRegione(java.util.Calendar dtmIns, java.util.Calendar dtmMod,
			java.lang.Integer idPrincipalIns, java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione deRegione,
			java.lang.Integer idSapDisponibilitaRegione) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deRegione = deRegione;
		this.idSapDisponibilitaRegione = idSapDisponibilitaRegione;
	}

	/**
	 * Gets the deRegione value for this SapDisponibilitaRegione.
	 * 
	 * @return deRegione
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione getDeRegione() {
		return deRegione;
	}

	/**
	 * Sets the deRegione value for this SapDisponibilitaRegione.
	 * 
	 * @param deRegione
	 */
	public void setDeRegione(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione deRegione) {
		this.deRegione = deRegione;
	}

	/**
	 * Gets the idSapDisponibilitaRegione value for this SapDisponibilitaRegione.
	 * 
	 * @return idSapDisponibilitaRegione
	 */
	public java.lang.Integer getIdSapDisponibilitaRegione() {
		return idSapDisponibilitaRegione;
	}

	/**
	 * Sets the idSapDisponibilitaRegione value for this SapDisponibilitaRegione.
	 * 
	 * @param idSapDisponibilitaRegione
	 */
	public void setIdSapDisponibilitaRegione(java.lang.Integer idSapDisponibilitaRegione) {
		this.idSapDisponibilitaRegione = idSapDisponibilitaRegione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaRegione))
			return false;
		SapDisponibilitaRegione other = (SapDisponibilitaRegione) obj;
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
				&& ((this.deRegione == null && other.getDeRegione() == null)
						|| (this.deRegione != null && this.deRegione.equals(other.getDeRegione())))
				&& ((this.idSapDisponibilitaRegione == null && other.getIdSapDisponibilitaRegione() == null)
						|| (this.idSapDisponibilitaRegione != null
								&& this.idSapDisponibilitaRegione.equals(other.getIdSapDisponibilitaRegione())));
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
		if (getDeRegione() != null) {
			_hashCode += getDeRegione().hashCode();
		}
		if (getIdSapDisponibilitaRegione() != null) {
			_hashCode += getIdSapDisponibilitaRegione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaRegione.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaRegione"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deRegione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deRegione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deRegione"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapDisponibilitaRegione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapDisponibilitaRegione"));
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
