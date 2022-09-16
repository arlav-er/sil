/**
 * DeGruppo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeGruppo extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codGruppo;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoGruppo deTipoGruppo;

	private java.lang.String descrizione;

	private java.lang.String id;

	public DeGruppo() {
	}

	public DeGruppo(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codGruppo,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoGruppo deTipoGruppo, java.lang.String descrizione,
			java.lang.String id) {
		super(dtFineVal, dtInizioVal);
		this.codGruppo = codGruppo;
		this.deTipoGruppo = deTipoGruppo;
		this.descrizione = descrizione;
		this.id = id;
	}

	/**
	 * Gets the codGruppo value for this DeGruppo.
	 * 
	 * @return codGruppo
	 */
	public java.lang.String getCodGruppo() {
		return codGruppo;
	}

	/**
	 * Sets the codGruppo value for this DeGruppo.
	 * 
	 * @param codGruppo
	 */
	public void setCodGruppo(java.lang.String codGruppo) {
		this.codGruppo = codGruppo;
	}

	/**
	 * Gets the deTipoGruppo value for this DeGruppo.
	 * 
	 * @return deTipoGruppo
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoGruppo getDeTipoGruppo() {
		return deTipoGruppo;
	}

	/**
	 * Sets the deTipoGruppo value for this DeGruppo.
	 * 
	 * @param deTipoGruppo
	 */
	public void setDeTipoGruppo(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTipoGruppo deTipoGruppo) {
		this.deTipoGruppo = deTipoGruppo;
	}

	/**
	 * Gets the descrizione value for this DeGruppo.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeGruppo.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeGruppo.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeGruppo.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeGruppo))
			return false;
		DeGruppo other = (DeGruppo) obj;
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
				&& ((this.codGruppo == null && other.getCodGruppo() == null)
						|| (this.codGruppo != null && this.codGruppo.equals(other.getCodGruppo())))
				&& ((this.deTipoGruppo == null && other.getDeTipoGruppo() == null)
						|| (this.deTipoGruppo != null && this.deTipoGruppo.equals(other.getDeTipoGruppo())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())));
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
		if (getCodGruppo() != null) {
			_hashCode += getCodGruppo().hashCode();
		}
		if (getDeTipoGruppo() != null) {
			_hashCode += getDeTipoGruppo().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeGruppo.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deGruppo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codGruppo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codGruppo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deTipoGruppo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deTipoGruppo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deTipoGruppo"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descrizione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descrizione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
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
