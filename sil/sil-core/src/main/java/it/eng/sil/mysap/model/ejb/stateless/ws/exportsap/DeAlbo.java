/**
 * DeAlbo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeAlbo extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codAlbo;

	private java.lang.String descrizione;

	private java.lang.Boolean flagMin;

	private java.lang.String id;

	public DeAlbo() {
	}

	public DeAlbo(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codAlbo,
			java.lang.String descrizione, java.lang.Boolean flagMin, java.lang.String id) {
		super(dtFineVal, dtInizioVal);
		this.codAlbo = codAlbo;
		this.descrizione = descrizione;
		this.flagMin = flagMin;
		this.id = id;
	}

	/**
	 * Gets the codAlbo value for this DeAlbo.
	 * 
	 * @return codAlbo
	 */
	public java.lang.String getCodAlbo() {
		return codAlbo;
	}

	/**
	 * Sets the codAlbo value for this DeAlbo.
	 * 
	 * @param codAlbo
	 */
	public void setCodAlbo(java.lang.String codAlbo) {
		this.codAlbo = codAlbo;
	}

	/**
	 * Gets the descrizione value for this DeAlbo.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeAlbo.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the flagMin value for this DeAlbo.
	 * 
	 * @return flagMin
	 */
	public java.lang.Boolean getFlagMin() {
		return flagMin;
	}

	/**
	 * Sets the flagMin value for this DeAlbo.
	 * 
	 * @param flagMin
	 */
	public void setFlagMin(java.lang.Boolean flagMin) {
		this.flagMin = flagMin;
	}

	/**
	 * Gets the id value for this DeAlbo.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeAlbo.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeAlbo))
			return false;
		DeAlbo other = (DeAlbo) obj;
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
				&& ((this.codAlbo == null && other.getCodAlbo() == null)
						|| (this.codAlbo != null && this.codAlbo.equals(other.getCodAlbo())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.flagMin == null && other.getFlagMin() == null)
						|| (this.flagMin != null && this.flagMin.equals(other.getFlagMin())))
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
		if (getCodAlbo() != null) {
			_hashCode += getCodAlbo().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getFlagMin() != null) {
			_hashCode += getFlagMin().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeAlbo.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deAlbo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codAlbo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codAlbo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
		elemField.setFieldName("flagMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flagMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
