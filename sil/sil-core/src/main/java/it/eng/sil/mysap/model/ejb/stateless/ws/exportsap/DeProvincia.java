/**
 * DeProvincia.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeProvincia extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codProvincia;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGruppo deGruppo;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione deRegione;

	private java.lang.String descrizione;

	private java.lang.String destinatarioSare;

	private java.lang.String emailRichiestaSare;

	private java.lang.String faxRichiestaSare;

	private java.lang.Boolean flagCapoluogo;

	private java.lang.String id;

	private java.lang.String targa;

	public DeProvincia() {
	}

	public DeProvincia(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codProvincia,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGruppo deGruppo,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione deRegione, java.lang.String descrizione,
			java.lang.String destinatarioSare, java.lang.String emailRichiestaSare, java.lang.String faxRichiestaSare,
			java.lang.Boolean flagCapoluogo, java.lang.String id, java.lang.String targa) {
		super(dtFineVal, dtInizioVal);
		this.codProvincia = codProvincia;
		this.deGruppo = deGruppo;
		this.deRegione = deRegione;
		this.descrizione = descrizione;
		this.destinatarioSare = destinatarioSare;
		this.emailRichiestaSare = emailRichiestaSare;
		this.faxRichiestaSare = faxRichiestaSare;
		this.flagCapoluogo = flagCapoluogo;
		this.id = id;
		this.targa = targa;
	}

	/**
	 * Gets the codProvincia value for this DeProvincia.
	 * 
	 * @return codProvincia
	 */
	public java.lang.String getCodProvincia() {
		return codProvincia;
	}

	/**
	 * Sets the codProvincia value for this DeProvincia.
	 * 
	 * @param codProvincia
	 */
	public void setCodProvincia(java.lang.String codProvincia) {
		this.codProvincia = codProvincia;
	}

	/**
	 * Gets the deGruppo value for this DeProvincia.
	 * 
	 * @return deGruppo
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGruppo getDeGruppo() {
		return deGruppo;
	}

	/**
	 * Sets the deGruppo value for this DeProvincia.
	 * 
	 * @param deGruppo
	 */
	public void setDeGruppo(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeGruppo deGruppo) {
		this.deGruppo = deGruppo;
	}

	/**
	 * Gets the deRegione value for this DeProvincia.
	 * 
	 * @return deRegione
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione getDeRegione() {
		return deRegione;
	}

	/**
	 * Sets the deRegione value for this DeProvincia.
	 * 
	 * @param deRegione
	 */
	public void setDeRegione(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeRegione deRegione) {
		this.deRegione = deRegione;
	}

	/**
	 * Gets the descrizione value for this DeProvincia.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeProvincia.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the destinatarioSare value for this DeProvincia.
	 * 
	 * @return destinatarioSare
	 */
	public java.lang.String getDestinatarioSare() {
		return destinatarioSare;
	}

	/**
	 * Sets the destinatarioSare value for this DeProvincia.
	 * 
	 * @param destinatarioSare
	 */
	public void setDestinatarioSare(java.lang.String destinatarioSare) {
		this.destinatarioSare = destinatarioSare;
	}

	/**
	 * Gets the emailRichiestaSare value for this DeProvincia.
	 * 
	 * @return emailRichiestaSare
	 */
	public java.lang.String getEmailRichiestaSare() {
		return emailRichiestaSare;
	}

	/**
	 * Sets the emailRichiestaSare value for this DeProvincia.
	 * 
	 * @param emailRichiestaSare
	 */
	public void setEmailRichiestaSare(java.lang.String emailRichiestaSare) {
		this.emailRichiestaSare = emailRichiestaSare;
	}

	/**
	 * Gets the faxRichiestaSare value for this DeProvincia.
	 * 
	 * @return faxRichiestaSare
	 */
	public java.lang.String getFaxRichiestaSare() {
		return faxRichiestaSare;
	}

	/**
	 * Sets the faxRichiestaSare value for this DeProvincia.
	 * 
	 * @param faxRichiestaSare
	 */
	public void setFaxRichiestaSare(java.lang.String faxRichiestaSare) {
		this.faxRichiestaSare = faxRichiestaSare;
	}

	/**
	 * Gets the flagCapoluogo value for this DeProvincia.
	 * 
	 * @return flagCapoluogo
	 */
	public java.lang.Boolean getFlagCapoluogo() {
		return flagCapoluogo;
	}

	/**
	 * Sets the flagCapoluogo value for this DeProvincia.
	 * 
	 * @param flagCapoluogo
	 */
	public void setFlagCapoluogo(java.lang.Boolean flagCapoluogo) {
		this.flagCapoluogo = flagCapoluogo;
	}

	/**
	 * Gets the id value for this DeProvincia.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeProvincia.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the targa value for this DeProvincia.
	 * 
	 * @return targa
	 */
	public java.lang.String getTarga() {
		return targa;
	}

	/**
	 * Sets the targa value for this DeProvincia.
	 * 
	 * @param targa
	 */
	public void setTarga(java.lang.String targa) {
		this.targa = targa;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeProvincia))
			return false;
		DeProvincia other = (DeProvincia) obj;
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
				&& ((this.codProvincia == null && other.getCodProvincia() == null)
						|| (this.codProvincia != null && this.codProvincia.equals(other.getCodProvincia())))
				&& ((this.deGruppo == null && other.getDeGruppo() == null)
						|| (this.deGruppo != null && this.deGruppo.equals(other.getDeGruppo())))
				&& ((this.deRegione == null && other.getDeRegione() == null)
						|| (this.deRegione != null && this.deRegione.equals(other.getDeRegione())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.destinatarioSare == null && other.getDestinatarioSare() == null)
						|| (this.destinatarioSare != null && this.destinatarioSare.equals(other.getDestinatarioSare())))
				&& ((this.emailRichiestaSare == null && other.getEmailRichiestaSare() == null)
						|| (this.emailRichiestaSare != null
								&& this.emailRichiestaSare.equals(other.getEmailRichiestaSare())))
				&& ((this.faxRichiestaSare == null && other.getFaxRichiestaSare() == null)
						|| (this.faxRichiestaSare != null && this.faxRichiestaSare.equals(other.getFaxRichiestaSare())))
				&& ((this.flagCapoluogo == null && other.getFlagCapoluogo() == null)
						|| (this.flagCapoluogo != null && this.flagCapoluogo.equals(other.getFlagCapoluogo())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.targa == null && other.getTarga() == null)
						|| (this.targa != null && this.targa.equals(other.getTarga())));
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
		if (getCodProvincia() != null) {
			_hashCode += getCodProvincia().hashCode();
		}
		if (getDeGruppo() != null) {
			_hashCode += getDeGruppo().hashCode();
		}
		if (getDeRegione() != null) {
			_hashCode += getDeRegione().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getDestinatarioSare() != null) {
			_hashCode += getDestinatarioSare().hashCode();
		}
		if (getEmailRichiestaSare() != null) {
			_hashCode += getEmailRichiestaSare().hashCode();
		}
		if (getFaxRichiestaSare() != null) {
			_hashCode += getFaxRichiestaSare().hashCode();
		}
		if (getFlagCapoluogo() != null) {
			_hashCode += getFlagCapoluogo().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getTarga() != null) {
			_hashCode += getTarga().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeProvincia.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deProvincia"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codProvincia");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codProvincia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deGruppo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deGruppo"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deGruppo"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deRegione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deRegione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deRegione"));
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
		elemField.setFieldName("destinatarioSare");
		elemField.setXmlName(new javax.xml.namespace.QName("", "destinatarioSare"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("emailRichiestaSare");
		elemField.setXmlName(new javax.xml.namespace.QName("", "emailRichiestaSare"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("faxRichiestaSare");
		elemField.setXmlName(new javax.xml.namespace.QName("", "faxRichiestaSare"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flagCapoluogo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flagCapoluogo"));
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
		elemField.setFieldName("targa");
		elemField.setXmlName(new javax.xml.namespace.QName("", "targa"));
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
