/**
 * DeTitolo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeTitolo extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codTitolo;

	private java.lang.String descrizione;

	private java.lang.String descrizioneParlante;

	private java.lang.Boolean flagLaurea;

	private java.lang.String id;

	private int occurencyRicerca;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo padre;

	public DeTitolo() {
	}

	public DeTitolo(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codTitolo,
			java.lang.String descrizione, java.lang.String descrizioneParlante, java.lang.Boolean flagLaurea,
			java.lang.String id, int occurencyRicerca,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo padre) {
		super(dtFineVal, dtInizioVal);
		this.codTitolo = codTitolo;
		this.descrizione = descrizione;
		this.descrizioneParlante = descrizioneParlante;
		this.flagLaurea = flagLaurea;
		this.id = id;
		this.occurencyRicerca = occurencyRicerca;
		this.padre = padre;
	}

	/**
	 * Gets the codTitolo value for this DeTitolo.
	 * 
	 * @return codTitolo
	 */
	public java.lang.String getCodTitolo() {
		return codTitolo;
	}

	/**
	 * Sets the codTitolo value for this DeTitolo.
	 * 
	 * @param codTitolo
	 */
	public void setCodTitolo(java.lang.String codTitolo) {
		this.codTitolo = codTitolo;
	}

	/**
	 * Gets the descrizione value for this DeTitolo.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeTitolo.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the descrizioneParlante value for this DeTitolo.
	 * 
	 * @return descrizioneParlante
	 */
	public java.lang.String getDescrizioneParlante() {
		return descrizioneParlante;
	}

	/**
	 * Sets the descrizioneParlante value for this DeTitolo.
	 * 
	 * @param descrizioneParlante
	 */
	public void setDescrizioneParlante(java.lang.String descrizioneParlante) {
		this.descrizioneParlante = descrizioneParlante;
	}

	/**
	 * Gets the flagLaurea value for this DeTitolo.
	 * 
	 * @return flagLaurea
	 */
	public java.lang.Boolean getFlagLaurea() {
		return flagLaurea;
	}

	/**
	 * Sets the flagLaurea value for this DeTitolo.
	 * 
	 * @param flagLaurea
	 */
	public void setFlagLaurea(java.lang.Boolean flagLaurea) {
		this.flagLaurea = flagLaurea;
	}

	/**
	 * Gets the id value for this DeTitolo.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeTitolo.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the occurencyRicerca value for this DeTitolo.
	 * 
	 * @return occurencyRicerca
	 */
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	/**
	 * Sets the occurencyRicerca value for this DeTitolo.
	 * 
	 * @param occurencyRicerca
	 */
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	/**
	 * Gets the padre value for this DeTitolo.
	 * 
	 * @return padre
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo getPadre() {
		return padre;
	}

	/**
	 * Sets the padre value for this DeTitolo.
	 * 
	 * @param padre
	 */
	public void setPadre(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo padre) {
		this.padre = padre;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeTitolo))
			return false;
		DeTitolo other = (DeTitolo) obj;
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
				&& ((this.codTitolo == null && other.getCodTitolo() == null)
						|| (this.codTitolo != null && this.codTitolo.equals(other.getCodTitolo())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.descrizioneParlante == null && other.getDescrizioneParlante() == null)
						|| (this.descrizioneParlante != null
								&& this.descrizioneParlante.equals(other.getDescrizioneParlante())))
				&& ((this.flagLaurea == null && other.getFlagLaurea() == null)
						|| (this.flagLaurea != null && this.flagLaurea.equals(other.getFlagLaurea())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& this.occurencyRicerca == other.getOccurencyRicerca()
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
		if (getCodTitolo() != null) {
			_hashCode += getCodTitolo().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getDescrizioneParlante() != null) {
			_hashCode += getDescrizioneParlante().hashCode();
		}
		if (getFlagLaurea() != null) {
			_hashCode += getFlagLaurea().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		_hashCode += getOccurencyRicerca();
		if (getPadre() != null) {
			_hashCode += getPadre().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeTitolo.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deTitolo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codTitolo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codTitolo"));
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
		elemField.setFieldName("descrizioneParlante");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descrizioneParlante"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flagLaurea");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flagLaurea"));
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
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("padre");
		elemField.setXmlName(new javax.xml.namespace.QName("", "padre"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deTitolo"));
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
