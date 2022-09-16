/**
 * DeComune.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeComune extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String cap;

	private java.lang.String codCom;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCpi deCpi;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia;

	private java.lang.String descrizione;

	private java.lang.String id;

	private java.lang.Integer numPopolazione;

	public DeComune() {
	}

	public DeComune(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String cap,
			java.lang.String codCom, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCpi deCpi,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia, java.lang.String descrizione,
			java.lang.String id, java.lang.Integer numPopolazione) {
		super(dtFineVal, dtInizioVal);
		this.cap = cap;
		this.codCom = codCom;
		this.deCpi = deCpi;
		this.deProvincia = deProvincia;
		this.descrizione = descrizione;
		this.id = id;
		this.numPopolazione = numPopolazione;
	}

	/**
	 * Gets the cap value for this DeComune.
	 * 
	 * @return cap
	 */
	public java.lang.String getCap() {
		return cap;
	}

	/**
	 * Sets the cap value for this DeComune.
	 * 
	 * @param cap
	 */
	public void setCap(java.lang.String cap) {
		this.cap = cap;
	}

	/**
	 * Gets the codCom value for this DeComune.
	 * 
	 * @return codCom
	 */
	public java.lang.String getCodCom() {
		return codCom;
	}

	/**
	 * Sets the codCom value for this DeComune.
	 * 
	 * @param codCom
	 */
	public void setCodCom(java.lang.String codCom) {
		this.codCom = codCom;
	}

	/**
	 * Gets the deCpi value for this DeComune.
	 * 
	 * @return deCpi
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCpi getDeCpi() {
		return deCpi;
	}

	/**
	 * Sets the deCpi value for this DeComune.
	 * 
	 * @param deCpi
	 */
	public void setDeCpi(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeCpi deCpi) {
		this.deCpi = deCpi;
	}

	/**
	 * Gets the deProvincia value for this DeComune.
	 * 
	 * @return deProvincia
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia getDeProvincia() {
		return deProvincia;
	}

	/**
	 * Sets the deProvincia value for this DeComune.
	 * 
	 * @param deProvincia
	 */
	public void setDeProvincia(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	/**
	 * Gets the descrizione value for this DeComune.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeComune.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeComune.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeComune.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the numPopolazione value for this DeComune.
	 * 
	 * @return numPopolazione
	 */
	public java.lang.Integer getNumPopolazione() {
		return numPopolazione;
	}

	/**
	 * Sets the numPopolazione value for this DeComune.
	 * 
	 * @param numPopolazione
	 */
	public void setNumPopolazione(java.lang.Integer numPopolazione) {
		this.numPopolazione = numPopolazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeComune))
			return false;
		DeComune other = (DeComune) obj;
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
				&& ((this.cap == null && other.getCap() == null)
						|| (this.cap != null && this.cap.equals(other.getCap())))
				&& ((this.codCom == null && other.getCodCom() == null)
						|| (this.codCom != null && this.codCom.equals(other.getCodCom())))
				&& ((this.deCpi == null && other.getDeCpi() == null)
						|| (this.deCpi != null && this.deCpi.equals(other.getDeCpi())))
				&& ((this.deProvincia == null && other.getDeProvincia() == null)
						|| (this.deProvincia != null && this.deProvincia.equals(other.getDeProvincia())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.numPopolazione == null && other.getNumPopolazione() == null)
						|| (this.numPopolazione != null && this.numPopolazione.equals(other.getNumPopolazione())));
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
		if (getCap() != null) {
			_hashCode += getCap().hashCode();
		}
		if (getCodCom() != null) {
			_hashCode += getCodCom().hashCode();
		}
		if (getDeCpi() != null) {
			_hashCode += getDeCpi().hashCode();
		}
		if (getDeProvincia() != null) {
			_hashCode += getDeProvincia().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getNumPopolazione() != null) {
			_hashCode += getNumPopolazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeComune.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deComune"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cap");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codCom");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codCom"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deCpi");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deCpi"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deCpi"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deProvincia");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deProvincia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deProvincia"));
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
		elemField.setFieldName("numPopolazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "numPopolazione"));
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
