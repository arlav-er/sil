/**
 * DeContratto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeContratto extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codContratto;

	private java.lang.String descrizione;

	private java.lang.Boolean flagAtipico;

	private java.lang.String id;

	private int occurencyRicerca;

	public DeContratto() {
	}

	public DeContratto(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codContratto,
			java.lang.String descrizione, java.lang.Boolean flagAtipico, java.lang.String id, int occurencyRicerca) {
		super(dtFineVal, dtInizioVal);
		this.codContratto = codContratto;
		this.descrizione = descrizione;
		this.flagAtipico = flagAtipico;
		this.id = id;
		this.occurencyRicerca = occurencyRicerca;
	}

	/**
	 * Gets the codContratto value for this DeContratto.
	 * 
	 * @return codContratto
	 */
	public java.lang.String getCodContratto() {
		return codContratto;
	}

	/**
	 * Sets the codContratto value for this DeContratto.
	 * 
	 * @param codContratto
	 */
	public void setCodContratto(java.lang.String codContratto) {
		this.codContratto = codContratto;
	}

	/**
	 * Gets the descrizione value for this DeContratto.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeContratto.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the flagAtipico value for this DeContratto.
	 * 
	 * @return flagAtipico
	 */
	public java.lang.Boolean getFlagAtipico() {
		return flagAtipico;
	}

	/**
	 * Sets the flagAtipico value for this DeContratto.
	 * 
	 * @param flagAtipico
	 */
	public void setFlagAtipico(java.lang.Boolean flagAtipico) {
		this.flagAtipico = flagAtipico;
	}

	/**
	 * Gets the id value for this DeContratto.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeContratto.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the occurencyRicerca value for this DeContratto.
	 * 
	 * @return occurencyRicerca
	 */
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	/**
	 * Sets the occurencyRicerca value for this DeContratto.
	 * 
	 * @param occurencyRicerca
	 */
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeContratto))
			return false;
		DeContratto other = (DeContratto) obj;
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
				&& ((this.codContratto == null && other.getCodContratto() == null)
						|| (this.codContratto != null && this.codContratto.equals(other.getCodContratto())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.flagAtipico == null && other.getFlagAtipico() == null)
						|| (this.flagAtipico != null && this.flagAtipico.equals(other.getFlagAtipico())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& this.occurencyRicerca == other.getOccurencyRicerca();
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
		if (getCodContratto() != null) {
			_hashCode += getCodContratto().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getFlagAtipico() != null) {
			_hashCode += getFlagAtipico().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		_hashCode += getOccurencyRicerca();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeContratto.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deContratto"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codContratto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codContratto"));
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
		elemField.setFieldName("flagAtipico");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flagAtipico"));
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
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("occurencyRicerca");
		elemField.setXmlName(new javax.xml.namespace.QName("", "occurencyRicerca"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
