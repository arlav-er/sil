/**
 * DeGradoLin.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeGradoLin extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codGradoLin;

	private java.lang.String descrizione;

	private java.lang.String id;

	private java.lang.Integer numOrdine;

	public DeGradoLin() {
	}

	public DeGradoLin(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codGradoLin,
			java.lang.String descrizione, java.lang.String id, java.lang.Integer numOrdine) {
		super(dtFineVal, dtInizioVal);
		this.codGradoLin = codGradoLin;
		this.descrizione = descrizione;
		this.id = id;
		this.numOrdine = numOrdine;
	}

	/**
	 * Gets the codGradoLin value for this DeGradoLin.
	 * 
	 * @return codGradoLin
	 */
	public java.lang.String getCodGradoLin() {
		return codGradoLin;
	}

	/**
	 * Sets the codGradoLin value for this DeGradoLin.
	 * 
	 * @param codGradoLin
	 */
	public void setCodGradoLin(java.lang.String codGradoLin) {
		this.codGradoLin = codGradoLin;
	}

	/**
	 * Gets the descrizione value for this DeGradoLin.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeGradoLin.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeGradoLin.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeGradoLin.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the numOrdine value for this DeGradoLin.
	 * 
	 * @return numOrdine
	 */
	public java.lang.Integer getNumOrdine() {
		return numOrdine;
	}

	/**
	 * Sets the numOrdine value for this DeGradoLin.
	 * 
	 * @param numOrdine
	 */
	public void setNumOrdine(java.lang.Integer numOrdine) {
		this.numOrdine = numOrdine;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeGradoLin))
			return false;
		DeGradoLin other = (DeGradoLin) obj;
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
				&& ((this.codGradoLin == null && other.getCodGradoLin() == null)
						|| (this.codGradoLin != null && this.codGradoLin.equals(other.getCodGradoLin())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.numOrdine == null && other.getNumOrdine() == null)
						|| (this.numOrdine != null && this.numOrdine.equals(other.getNumOrdine())));
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
		if (getCodGradoLin() != null) {
			_hashCode += getCodGradoLin().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getNumOrdine() != null) {
			_hashCode += getNumOrdine().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeGradoLin.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deGradoLin"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codGradoLin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codGradoLin"));
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
		elemField.setFieldName("numOrdine");
		elemField.setXmlName(new javax.xml.namespace.QName("", "numOrdine"));
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
