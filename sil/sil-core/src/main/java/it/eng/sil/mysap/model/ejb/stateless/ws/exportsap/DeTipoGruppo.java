/**
 * DeTipoGruppo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeTipoGruppo extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codTipoGruppo;

	private java.lang.String descrizione;

	private java.lang.String id;

	private java.lang.String tabellaAssociata;

	private java.lang.String tipoGruppo;

	public DeTipoGruppo() {
	}

	public DeTipoGruppo(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codTipoGruppo,
			java.lang.String descrizione, java.lang.String id, java.lang.String tabellaAssociata,
			java.lang.String tipoGruppo) {
		super(dtFineVal, dtInizioVal);
		this.codTipoGruppo = codTipoGruppo;
		this.descrizione = descrizione;
		this.id = id;
		this.tabellaAssociata = tabellaAssociata;
		this.tipoGruppo = tipoGruppo;
	}

	/**
	 * Gets the codTipoGruppo value for this DeTipoGruppo.
	 * 
	 * @return codTipoGruppo
	 */
	public java.lang.String getCodTipoGruppo() {
		return codTipoGruppo;
	}

	/**
	 * Sets the codTipoGruppo value for this DeTipoGruppo.
	 * 
	 * @param codTipoGruppo
	 */
	public void setCodTipoGruppo(java.lang.String codTipoGruppo) {
		this.codTipoGruppo = codTipoGruppo;
	}

	/**
	 * Gets the descrizione value for this DeTipoGruppo.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeTipoGruppo.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeTipoGruppo.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeTipoGruppo.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the tabellaAssociata value for this DeTipoGruppo.
	 * 
	 * @return tabellaAssociata
	 */
	public java.lang.String getTabellaAssociata() {
		return tabellaAssociata;
	}

	/**
	 * Sets the tabellaAssociata value for this DeTipoGruppo.
	 * 
	 * @param tabellaAssociata
	 */
	public void setTabellaAssociata(java.lang.String tabellaAssociata) {
		this.tabellaAssociata = tabellaAssociata;
	}

	/**
	 * Gets the tipoGruppo value for this DeTipoGruppo.
	 * 
	 * @return tipoGruppo
	 */
	public java.lang.String getTipoGruppo() {
		return tipoGruppo;
	}

	/**
	 * Sets the tipoGruppo value for this DeTipoGruppo.
	 * 
	 * @param tipoGruppo
	 */
	public void setTipoGruppo(java.lang.String tipoGruppo) {
		this.tipoGruppo = tipoGruppo;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeTipoGruppo))
			return false;
		DeTipoGruppo other = (DeTipoGruppo) obj;
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
				&& ((this.codTipoGruppo == null && other.getCodTipoGruppo() == null)
						|| (this.codTipoGruppo != null && this.codTipoGruppo.equals(other.getCodTipoGruppo())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.tabellaAssociata == null && other.getTabellaAssociata() == null)
						|| (this.tabellaAssociata != null && this.tabellaAssociata.equals(other.getTabellaAssociata())))
				&& ((this.tipoGruppo == null && other.getTipoGruppo() == null)
						|| (this.tipoGruppo != null && this.tipoGruppo.equals(other.getTipoGruppo())));
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
		if (getCodTipoGruppo() != null) {
			_hashCode += getCodTipoGruppo().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getTabellaAssociata() != null) {
			_hashCode += getTabellaAssociata().hashCode();
		}
		if (getTipoGruppo() != null) {
			_hashCode += getTipoGruppo().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeTipoGruppo.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deTipoGruppo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codTipoGruppo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codTipoGruppo"));
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
		elemField.setFieldName("tabellaAssociata");
		elemField.setXmlName(new javax.xml.namespace.QName("", "tabellaAssociata"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipoGruppo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "tipoGruppo"));
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
