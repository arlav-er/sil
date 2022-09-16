/**
 * SapPropTipoContratto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPropTipoContratto
		extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseGestioneSapPropensioneChild
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRapportoLavoro deRapportoLavoro;

	private java.lang.Integer idSapPropTipoContratto;

	public SapPropTipoContratto() {
	}

	public SapPropTipoContratto(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRapportoLavoro deRapportoLavoro,
			java.lang.Integer idSapPropTipoContratto) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deRapportoLavoro = deRapportoLavoro;
		this.idSapPropTipoContratto = idSapPropTipoContratto;
	}

	/**
	 * Gets the deRapportoLavoro value for this SapPropTipoContratto.
	 * 
	 * @return deRapportoLavoro
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRapportoLavoro getDeRapportoLavoro() {
		return deRapportoLavoro;
	}

	/**
	 * Sets the deRapportoLavoro value for this SapPropTipoContratto.
	 * 
	 * @param deRapportoLavoro
	 */
	public void setDeRapportoLavoro(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRapportoLavoro deRapportoLavoro) {
		this.deRapportoLavoro = deRapportoLavoro;
	}

	/**
	 * Gets the idSapPropTipoContratto value for this SapPropTipoContratto.
	 * 
	 * @return idSapPropTipoContratto
	 */
	public java.lang.Integer getIdSapPropTipoContratto() {
		return idSapPropTipoContratto;
	}

	/**
	 * Sets the idSapPropTipoContratto value for this SapPropTipoContratto.
	 * 
	 * @param idSapPropTipoContratto
	 */
	public void setIdSapPropTipoContratto(java.lang.Integer idSapPropTipoContratto) {
		this.idSapPropTipoContratto = idSapPropTipoContratto;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPropTipoContratto))
			return false;
		SapPropTipoContratto other = (SapPropTipoContratto) obj;
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
				&& ((this.deRapportoLavoro == null && other.getDeRapportoLavoro() == null)
						|| (this.deRapportoLavoro != null && this.deRapportoLavoro.equals(other.getDeRapportoLavoro())))
				&& ((this.idSapPropTipoContratto == null && other.getIdSapPropTipoContratto() == null)
						|| (this.idSapPropTipoContratto != null
								&& this.idSapPropTipoContratto.equals(other.getIdSapPropTipoContratto())));
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
		if (getDeRapportoLavoro() != null) {
			_hashCode += getDeRapportoLavoro().hashCode();
		}
		if (getIdSapPropTipoContratto() != null) {
			_hashCode += getIdSapPropTipoContratto().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPropTipoContratto.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropTipoContratto"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deRapportoLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deRapportoLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deRapportoLavoro"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapPropTipoContratto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapPropTipoContratto"));
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
