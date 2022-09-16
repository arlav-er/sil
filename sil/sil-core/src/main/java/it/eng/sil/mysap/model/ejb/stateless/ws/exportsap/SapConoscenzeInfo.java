/**
 * SapConoscenzeInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapConoscenzeInfo extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeDettaglioConInformatica deDettaglioConInformatica;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoConInformatica deGradoConInformatica;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoConInformatica deTipoConInformatica;

	private java.lang.Integer idSapConoscenzeInfo;

	private java.lang.String strDescrizione;

	public SapConoscenzeInfo() {
	}

	public SapConoscenzeInfo(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeDettaglioConInformatica deDettaglioConInformatica,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoConInformatica deGradoConInformatica,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoConInformatica deTipoConInformatica,
			java.lang.Integer idSapConoscenzeInfo, java.lang.String strDescrizione) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deDettaglioConInformatica = deDettaglioConInformatica;
		this.deGradoConInformatica = deGradoConInformatica;
		this.deTipoConInformatica = deTipoConInformatica;
		this.idSapConoscenzeInfo = idSapConoscenzeInfo;
		this.strDescrizione = strDescrizione;
	}

	/**
	 * Gets the deDettaglioConInformatica value for this SapConoscenzeInfo.
	 * 
	 * @return deDettaglioConInformatica
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeDettaglioConInformatica getDeDettaglioConInformatica() {
		return deDettaglioConInformatica;
	}

	/**
	 * Sets the deDettaglioConInformatica value for this SapConoscenzeInfo.
	 * 
	 * @param deDettaglioConInformatica
	 */
	public void setDeDettaglioConInformatica(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeDettaglioConInformatica deDettaglioConInformatica) {
		this.deDettaglioConInformatica = deDettaglioConInformatica;
	}

	/**
	 * Gets the deGradoConInformatica value for this SapConoscenzeInfo.
	 * 
	 * @return deGradoConInformatica
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoConInformatica getDeGradoConInformatica() {
		return deGradoConInformatica;
	}

	/**
	 * Sets the deGradoConInformatica value for this SapConoscenzeInfo.
	 * 
	 * @param deGradoConInformatica
	 */
	public void setDeGradoConInformatica(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGradoConInformatica deGradoConInformatica) {
		this.deGradoConInformatica = deGradoConInformatica;
	}

	/**
	 * Gets the deTipoConInformatica value for this SapConoscenzeInfo.
	 * 
	 * @return deTipoConInformatica
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoConInformatica getDeTipoConInformatica() {
		return deTipoConInformatica;
	}

	/**
	 * Sets the deTipoConInformatica value for this SapConoscenzeInfo.
	 * 
	 * @param deTipoConInformatica
	 */
	public void setDeTipoConInformatica(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoConInformatica deTipoConInformatica) {
		this.deTipoConInformatica = deTipoConInformatica;
	}

	/**
	 * Gets the idSapConoscenzeInfo value for this SapConoscenzeInfo.
	 * 
	 * @return idSapConoscenzeInfo
	 */
	public java.lang.Integer getIdSapConoscenzeInfo() {
		return idSapConoscenzeInfo;
	}

	/**
	 * Sets the idSapConoscenzeInfo value for this SapConoscenzeInfo.
	 * 
	 * @param idSapConoscenzeInfo
	 */
	public void setIdSapConoscenzeInfo(java.lang.Integer idSapConoscenzeInfo) {
		this.idSapConoscenzeInfo = idSapConoscenzeInfo;
	}

	/**
	 * Gets the strDescrizione value for this SapConoscenzeInfo.
	 * 
	 * @return strDescrizione
	 */
	public java.lang.String getStrDescrizione() {
		return strDescrizione;
	}

	/**
	 * Sets the strDescrizione value for this SapConoscenzeInfo.
	 * 
	 * @param strDescrizione
	 */
	public void setStrDescrizione(java.lang.String strDescrizione) {
		this.strDescrizione = strDescrizione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapConoscenzeInfo))
			return false;
		SapConoscenzeInfo other = (SapConoscenzeInfo) obj;
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
				&& ((this.deDettaglioConInformatica == null && other.getDeDettaglioConInformatica() == null)
						|| (this.deDettaglioConInformatica != null
								&& this.deDettaglioConInformatica.equals(other.getDeDettaglioConInformatica())))
				&& ((this.deGradoConInformatica == null && other.getDeGradoConInformatica() == null)
						|| (this.deGradoConInformatica != null
								&& this.deGradoConInformatica.equals(other.getDeGradoConInformatica())))
				&& ((this.deTipoConInformatica == null && other.getDeTipoConInformatica() == null)
						|| (this.deTipoConInformatica != null
								&& this.deTipoConInformatica.equals(other.getDeTipoConInformatica())))
				&& ((this.idSapConoscenzeInfo == null && other.getIdSapConoscenzeInfo() == null)
						|| (this.idSapConoscenzeInfo != null
								&& this.idSapConoscenzeInfo.equals(other.getIdSapConoscenzeInfo())))
				&& ((this.strDescrizione == null && other.getStrDescrizione() == null)
						|| (this.strDescrizione != null && this.strDescrizione.equals(other.getStrDescrizione())));
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
		if (getDeDettaglioConInformatica() != null) {
			_hashCode += getDeDettaglioConInformatica().hashCode();
		}
		if (getDeGradoConInformatica() != null) {
			_hashCode += getDeGradoConInformatica().hashCode();
		}
		if (getDeTipoConInformatica() != null) {
			_hashCode += getDeTipoConInformatica().hashCode();
		}
		if (getIdSapConoscenzeInfo() != null) {
			_hashCode += getIdSapConoscenzeInfo().hashCode();
		}
		if (getStrDescrizione() != null) {
			_hashCode += getStrDescrizione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapConoscenzeInfo.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapConoscenzeInfo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deDettaglioConInformatica");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deDettaglioConInformatica"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deDettaglioConInformatica"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deGradoConInformatica");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deGradoConInformatica"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deGradoConInformatica"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deTipoConInformatica");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deTipoConInformatica"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deTipoConInformatica"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapConoscenzeInfo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapConoscenzeInfo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strDescrizione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strDescrizione"));
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
