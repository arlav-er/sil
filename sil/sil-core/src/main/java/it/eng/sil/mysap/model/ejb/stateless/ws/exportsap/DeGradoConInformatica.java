/**
 * DeGradoConInformatica.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeGradoConInformatica extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.Integer codGradoConInformatica;

	private java.lang.String descrizione;

	private java.lang.Integer id;

	public DeGradoConInformatica() {
	}

	public DeGradoConInformatica(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal,
			java.lang.Integer codGradoConInformatica, java.lang.String descrizione, java.lang.Integer id) {
		super(dtFineVal, dtInizioVal);
		this.codGradoConInformatica = codGradoConInformatica;
		this.descrizione = descrizione;
		this.id = id;
	}

	/**
	 * Gets the codGradoConInformatica value for this DeGradoConInformatica.
	 * 
	 * @return codGradoConInformatica
	 */
	public java.lang.Integer getCodGradoConInformatica() {
		return codGradoConInformatica;
	}

	/**
	 * Sets the codGradoConInformatica value for this DeGradoConInformatica.
	 * 
	 * @param codGradoConInformatica
	 */
	public void setCodGradoConInformatica(java.lang.Integer codGradoConInformatica) {
		this.codGradoConInformatica = codGradoConInformatica;
	}

	/**
	 * Gets the descrizione value for this DeGradoConInformatica.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeGradoConInformatica.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeGradoConInformatica.
	 * 
	 * @return id
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeGradoConInformatica.
	 * 
	 * @param id
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeGradoConInformatica))
			return false;
		DeGradoConInformatica other = (DeGradoConInformatica) obj;
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
				&& ((this.codGradoConInformatica == null && other.getCodGradoConInformatica() == null)
						|| (this.codGradoConInformatica != null
								&& this.codGradoConInformatica.equals(other.getCodGradoConInformatica())))
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
		if (getCodGradoConInformatica() != null) {
			_hashCode += getCodGradoConInformatica().hashCode();
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
			DeGradoConInformatica.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deGradoConInformatica"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codGradoConInformatica");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codGradoConInformatica"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
