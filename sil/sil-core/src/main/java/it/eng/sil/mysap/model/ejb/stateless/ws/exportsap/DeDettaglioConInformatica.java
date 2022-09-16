/**
 * DeDettaglioConInformatica.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeDettaglioConInformatica extends
		it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity implements java.io.Serializable {
	private java.lang.String codDettaglioConInformatica;

	private java.lang.String descrizione;

	private java.lang.String id;

	public DeDettaglioConInformatica() {
	}

	public DeDettaglioConInformatica(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal,
			java.lang.String codDettaglioConInformatica, java.lang.String descrizione, java.lang.String id) {
		super(dtFineVal, dtInizioVal);
		this.codDettaglioConInformatica = codDettaglioConInformatica;
		this.descrizione = descrizione;
		this.id = id;
	}

	/**
	 * Gets the codDettaglioConInformatica value for this DeDettaglioConInformatica.
	 * 
	 * @return codDettaglioConInformatica
	 */
	public java.lang.String getCodDettaglioConInformatica() {
		return codDettaglioConInformatica;
	}

	/**
	 * Sets the codDettaglioConInformatica value for this DeDettaglioConInformatica.
	 * 
	 * @param codDettaglioConInformatica
	 */
	public void setCodDettaglioConInformatica(java.lang.String codDettaglioConInformatica) {
		this.codDettaglioConInformatica = codDettaglioConInformatica;
	}

	/**
	 * Gets the descrizione value for this DeDettaglioConInformatica.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeDettaglioConInformatica.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeDettaglioConInformatica.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeDettaglioConInformatica.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeDettaglioConInformatica))
			return false;
		DeDettaglioConInformatica other = (DeDettaglioConInformatica) obj;
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
				&& ((this.codDettaglioConInformatica == null && other.getCodDettaglioConInformatica() == null)
						|| (this.codDettaglioConInformatica != null
								&& this.codDettaglioConInformatica.equals(other.getCodDettaglioConInformatica())))
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
		if (getCodDettaglioConInformatica() != null) {
			_hashCode += getCodDettaglioConInformatica().hashCode();
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
			DeDettaglioConInformatica.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deDettaglioConInformatica"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codDettaglioConInformatica");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codDettaglioConInformatica"));
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
