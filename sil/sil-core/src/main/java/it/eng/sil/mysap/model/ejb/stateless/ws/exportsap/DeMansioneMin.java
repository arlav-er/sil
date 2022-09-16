/**
 * DeMansioneMin.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeMansioneMin extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codMansioneMin;

	private java.lang.String descrizione;

	private java.lang.String id;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin padre;

	public DeMansioneMin() {
	}

	public DeMansioneMin(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codMansioneMin,
			java.lang.String descrizione, java.lang.String id,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin padre) {
		super(dtFineVal, dtInizioVal);
		this.codMansioneMin = codMansioneMin;
		this.descrizione = descrizione;
		this.id = id;
		this.padre = padre;
	}

	/**
	 * Gets the codMansioneMin value for this DeMansioneMin.
	 * 
	 * @return codMansioneMin
	 */
	public java.lang.String getCodMansioneMin() {
		return codMansioneMin;
	}

	/**
	 * Sets the codMansioneMin value for this DeMansioneMin.
	 * 
	 * @param codMansioneMin
	 */
	public void setCodMansioneMin(java.lang.String codMansioneMin) {
		this.codMansioneMin = codMansioneMin;
	}

	/**
	 * Gets the descrizione value for this DeMansioneMin.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeMansioneMin.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeMansioneMin.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeMansioneMin.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the padre value for this DeMansioneMin.
	 * 
	 * @return padre
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin getPadre() {
		return padre;
	}

	/**
	 * Sets the padre value for this DeMansioneMin.
	 * 
	 * @param padre
	 */
	public void setPadre(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin padre) {
		this.padre = padre;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeMansioneMin))
			return false;
		DeMansioneMin other = (DeMansioneMin) obj;
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
				&& ((this.codMansioneMin == null && other.getCodMansioneMin() == null)
						|| (this.codMansioneMin != null && this.codMansioneMin.equals(other.getCodMansioneMin())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.padre == null && other.getPadre() == null)
						|| (this.padre != null && this.padre.equals(other.getPadre())));
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
		if (getCodMansioneMin() != null) {
			_hashCode += getCodMansioneMin().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getPadre() != null) {
			_hashCode += getPadre().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeMansioneMin.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansioneMin"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMansioneMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMansioneMin"));
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
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("padre");
		elemField.setXmlName(new javax.xml.namespace.QName("", "padre"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansioneMin"));
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
