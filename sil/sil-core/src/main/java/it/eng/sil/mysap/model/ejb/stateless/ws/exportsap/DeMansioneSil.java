/**
 * DeMansioneSil.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeMansioneSil extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codMansioneSil;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione deMansione;

	private java.lang.String descrizione;

	private java.lang.String id;

	public DeMansioneSil() {
	}

	public DeMansioneSil(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codMansioneSil,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione deMansione, java.lang.String descrizione,
			java.lang.String id) {
		super(dtFineVal, dtInizioVal);
		this.codMansioneSil = codMansioneSil;
		this.deMansione = deMansione;
		this.descrizione = descrizione;
		this.id = id;
	}

	/**
	 * Gets the codMansioneSil value for this DeMansioneSil.
	 * 
	 * @return codMansioneSil
	 */
	public java.lang.String getCodMansioneSil() {
		return codMansioneSil;
	}

	/**
	 * Sets the codMansioneSil value for this DeMansioneSil.
	 * 
	 * @param codMansioneSil
	 */
	public void setCodMansioneSil(java.lang.String codMansioneSil) {
		this.codMansioneSil = codMansioneSil;
	}

	/**
	 * Gets the deMansione value for this DeMansioneSil.
	 * 
	 * @return deMansione
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione getDeMansione() {
		return deMansione;
	}

	/**
	 * Sets the deMansione value for this DeMansioneSil.
	 * 
	 * @param deMansione
	 */
	public void setDeMansione(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione deMansione) {
		this.deMansione = deMansione;
	}

	/**
	 * Gets the descrizione value for this DeMansioneSil.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeMansioneSil.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeMansioneSil.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeMansioneSil.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeMansioneSil))
			return false;
		DeMansioneSil other = (DeMansioneSil) obj;
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
				&& ((this.codMansioneSil == null && other.getCodMansioneSil() == null)
						|| (this.codMansioneSil != null && this.codMansioneSil.equals(other.getCodMansioneSil())))
				&& ((this.deMansione == null && other.getDeMansione() == null)
						|| (this.deMansione != null && this.deMansione.equals(other.getDeMansione())))
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
		if (getCodMansioneSil() != null) {
			_hashCode += getCodMansioneSil().hashCode();
		}
		if (getDeMansione() != null) {
			_hashCode += getDeMansione().hashCode();
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
			DeMansioneSil.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansioneSil"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMansioneSil");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMansioneSil"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deMansione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deMansione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansione"));
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
