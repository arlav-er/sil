/**
 * DeCpi.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeCpi extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String cap;

	private java.lang.String codCpi;

	private java.lang.String codCpiMin;

	private java.lang.String codIntermediarioCl;

	private java.lang.String codMonoTipoFile;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia;

	private java.lang.String descrizione;

	private java.lang.String descrizioneMin;

	private java.lang.String email;

	private java.lang.String emailMigrazione;

	private java.lang.String emailPortale;

	private java.lang.String emailRifCl;

	private java.lang.String emailServiziOnline;

	private java.lang.String fax;

	private java.lang.Boolean flgPatronato;

	private java.lang.String id;

	private java.lang.String indirizzo;

	private java.lang.String indirizzoStampa;

	private java.lang.String localita;

	private java.lang.String note;

	private java.lang.String orario;

	private java.lang.String responsabile;

	private java.lang.String rifSms;

	private java.lang.String tel;

	private java.lang.String telPatronato;

	private java.lang.String telRifCl;

	public DeCpi() {
	}

	public DeCpi(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String cap,
			java.lang.String codCpi, java.lang.String codCpiMin, java.lang.String codIntermediarioCl,
			java.lang.String codMonoTipoFile, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia,
			java.lang.String descrizione, java.lang.String descrizioneMin, java.lang.String email,
			java.lang.String emailMigrazione, java.lang.String emailPortale, java.lang.String emailRifCl,
			java.lang.String emailServiziOnline, java.lang.String fax, java.lang.Boolean flgPatronato,
			java.lang.String id, java.lang.String indirizzo, java.lang.String indirizzoStampa,
			java.lang.String localita, java.lang.String note, java.lang.String orario, java.lang.String responsabile,
			java.lang.String rifSms, java.lang.String tel, java.lang.String telPatronato, java.lang.String telRifCl) {
		super(dtFineVal, dtInizioVal);
		this.cap = cap;
		this.codCpi = codCpi;
		this.codCpiMin = codCpiMin;
		this.codIntermediarioCl = codIntermediarioCl;
		this.codMonoTipoFile = codMonoTipoFile;
		this.deProvincia = deProvincia;
		this.descrizione = descrizione;
		this.descrizioneMin = descrizioneMin;
		this.email = email;
		this.emailMigrazione = emailMigrazione;
		this.emailPortale = emailPortale;
		this.emailRifCl = emailRifCl;
		this.emailServiziOnline = emailServiziOnline;
		this.fax = fax;
		this.flgPatronato = flgPatronato;
		this.id = id;
		this.indirizzo = indirizzo;
		this.indirizzoStampa = indirizzoStampa;
		this.localita = localita;
		this.note = note;
		this.orario = orario;
		this.responsabile = responsabile;
		this.rifSms = rifSms;
		this.tel = tel;
		this.telPatronato = telPatronato;
		this.telRifCl = telRifCl;
	}

	/**
	 * Gets the cap value for this DeCpi.
	 * 
	 * @return cap
	 */
	public java.lang.String getCap() {
		return cap;
	}

	/**
	 * Sets the cap value for this DeCpi.
	 * 
	 * @param cap
	 */
	public void setCap(java.lang.String cap) {
		this.cap = cap;
	}

	/**
	 * Gets the codCpi value for this DeCpi.
	 * 
	 * @return codCpi
	 */
	public java.lang.String getCodCpi() {
		return codCpi;
	}

	/**
	 * Sets the codCpi value for this DeCpi.
	 * 
	 * @param codCpi
	 */
	public void setCodCpi(java.lang.String codCpi) {
		this.codCpi = codCpi;
	}

	/**
	 * Gets the codCpiMin value for this DeCpi.
	 * 
	 * @return codCpiMin
	 */
	public java.lang.String getCodCpiMin() {
		return codCpiMin;
	}

	/**
	 * Sets the codCpiMin value for this DeCpi.
	 * 
	 * @param codCpiMin
	 */
	public void setCodCpiMin(java.lang.String codCpiMin) {
		this.codCpiMin = codCpiMin;
	}

	/**
	 * Gets the codIntermediarioCl value for this DeCpi.
	 * 
	 * @return codIntermediarioCl
	 */
	public java.lang.String getCodIntermediarioCl() {
		return codIntermediarioCl;
	}

	/**
	 * Sets the codIntermediarioCl value for this DeCpi.
	 * 
	 * @param codIntermediarioCl
	 */
	public void setCodIntermediarioCl(java.lang.String codIntermediarioCl) {
		this.codIntermediarioCl = codIntermediarioCl;
	}

	/**
	 * Gets the codMonoTipoFile value for this DeCpi.
	 * 
	 * @return codMonoTipoFile
	 */
	public java.lang.String getCodMonoTipoFile() {
		return codMonoTipoFile;
	}

	/**
	 * Sets the codMonoTipoFile value for this DeCpi.
	 * 
	 * @param codMonoTipoFile
	 */
	public void setCodMonoTipoFile(java.lang.String codMonoTipoFile) {
		this.codMonoTipoFile = codMonoTipoFile;
	}

	/**
	 * Gets the deProvincia value for this DeCpi.
	 * 
	 * @return deProvincia
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia getDeProvincia() {
		return deProvincia;
	}

	/**
	 * Sets the deProvincia value for this DeCpi.
	 * 
	 * @param deProvincia
	 */
	public void setDeProvincia(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	/**
	 * Gets the descrizione value for this DeCpi.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeCpi.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the descrizioneMin value for this DeCpi.
	 * 
	 * @return descrizioneMin
	 */
	public java.lang.String getDescrizioneMin() {
		return descrizioneMin;
	}

	/**
	 * Sets the descrizioneMin value for this DeCpi.
	 * 
	 * @param descrizioneMin
	 */
	public void setDescrizioneMin(java.lang.String descrizioneMin) {
		this.descrizioneMin = descrizioneMin;
	}

	/**
	 * Gets the email value for this DeCpi.
	 * 
	 * @return email
	 */
	public java.lang.String getEmail() {
		return email;
	}

	/**
	 * Sets the email value for this DeCpi.
	 * 
	 * @param email
	 */
	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	/**
	 * Gets the emailMigrazione value for this DeCpi.
	 * 
	 * @return emailMigrazione
	 */
	public java.lang.String getEmailMigrazione() {
		return emailMigrazione;
	}

	/**
	 * Sets the emailMigrazione value for this DeCpi.
	 * 
	 * @param emailMigrazione
	 */
	public void setEmailMigrazione(java.lang.String emailMigrazione) {
		this.emailMigrazione = emailMigrazione;
	}

	/**
	 * Gets the emailPortale value for this DeCpi.
	 * 
	 * @return emailPortale
	 */
	public java.lang.String getEmailPortale() {
		return emailPortale;
	}

	/**
	 * Sets the emailPortale value for this DeCpi.
	 * 
	 * @param emailPortale
	 */
	public void setEmailPortale(java.lang.String emailPortale) {
		this.emailPortale = emailPortale;
	}

	/**
	 * Gets the emailRifCl value for this DeCpi.
	 * 
	 * @return emailRifCl
	 */
	public java.lang.String getEmailRifCl() {
		return emailRifCl;
	}

	/**
	 * Sets the emailRifCl value for this DeCpi.
	 * 
	 * @param emailRifCl
	 */
	public void setEmailRifCl(java.lang.String emailRifCl) {
		this.emailRifCl = emailRifCl;
	}

	/**
	 * Gets the emailServiziOnline value for this DeCpi.
	 * 
	 * @return emailServiziOnline
	 */
	public java.lang.String getEmailServiziOnline() {
		return emailServiziOnline;
	}

	/**
	 * Sets the emailServiziOnline value for this DeCpi.
	 * 
	 * @param emailServiziOnline
	 */
	public void setEmailServiziOnline(java.lang.String emailServiziOnline) {
		this.emailServiziOnline = emailServiziOnline;
	}

	/**
	 * Gets the fax value for this DeCpi.
	 * 
	 * @return fax
	 */
	public java.lang.String getFax() {
		return fax;
	}

	/**
	 * Sets the fax value for this DeCpi.
	 * 
	 * @param fax
	 */
	public void setFax(java.lang.String fax) {
		this.fax = fax;
	}

	/**
	 * Gets the flgPatronato value for this DeCpi.
	 * 
	 * @return flgPatronato
	 */
	public java.lang.Boolean getFlgPatronato() {
		return flgPatronato;
	}

	/**
	 * Sets the flgPatronato value for this DeCpi.
	 * 
	 * @param flgPatronato
	 */
	public void setFlgPatronato(java.lang.Boolean flgPatronato) {
		this.flgPatronato = flgPatronato;
	}

	/**
	 * Gets the id value for this DeCpi.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeCpi.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the indirizzo value for this DeCpi.
	 * 
	 * @return indirizzo
	 */
	public java.lang.String getIndirizzo() {
		return indirizzo;
	}

	/**
	 * Sets the indirizzo value for this DeCpi.
	 * 
	 * @param indirizzo
	 */
	public void setIndirizzo(java.lang.String indirizzo) {
		this.indirizzo = indirizzo;
	}

	/**
	 * Gets the indirizzoStampa value for this DeCpi.
	 * 
	 * @return indirizzoStampa
	 */
	public java.lang.String getIndirizzoStampa() {
		return indirizzoStampa;
	}

	/**
	 * Sets the indirizzoStampa value for this DeCpi.
	 * 
	 * @param indirizzoStampa
	 */
	public void setIndirizzoStampa(java.lang.String indirizzoStampa) {
		this.indirizzoStampa = indirizzoStampa;
	}

	/**
	 * Gets the localita value for this DeCpi.
	 * 
	 * @return localita
	 */
	public java.lang.String getLocalita() {
		return localita;
	}

	/**
	 * Sets the localita value for this DeCpi.
	 * 
	 * @param localita
	 */
	public void setLocalita(java.lang.String localita) {
		this.localita = localita;
	}

	/**
	 * Gets the note value for this DeCpi.
	 * 
	 * @return note
	 */
	public java.lang.String getNote() {
		return note;
	}

	/**
	 * Sets the note value for this DeCpi.
	 * 
	 * @param note
	 */
	public void setNote(java.lang.String note) {
		this.note = note;
	}

	/**
	 * Gets the orario value for this DeCpi.
	 * 
	 * @return orario
	 */
	public java.lang.String getOrario() {
		return orario;
	}

	/**
	 * Sets the orario value for this DeCpi.
	 * 
	 * @param orario
	 */
	public void setOrario(java.lang.String orario) {
		this.orario = orario;
	}

	/**
	 * Gets the responsabile value for this DeCpi.
	 * 
	 * @return responsabile
	 */
	public java.lang.String getResponsabile() {
		return responsabile;
	}

	/**
	 * Sets the responsabile value for this DeCpi.
	 * 
	 * @param responsabile
	 */
	public void setResponsabile(java.lang.String responsabile) {
		this.responsabile = responsabile;
	}

	/**
	 * Gets the rifSms value for this DeCpi.
	 * 
	 * @return rifSms
	 */
	public java.lang.String getRifSms() {
		return rifSms;
	}

	/**
	 * Sets the rifSms value for this DeCpi.
	 * 
	 * @param rifSms
	 */
	public void setRifSms(java.lang.String rifSms) {
		this.rifSms = rifSms;
	}

	/**
	 * Gets the tel value for this DeCpi.
	 * 
	 * @return tel
	 */
	public java.lang.String getTel() {
		return tel;
	}

	/**
	 * Sets the tel value for this DeCpi.
	 * 
	 * @param tel
	 */
	public void setTel(java.lang.String tel) {
		this.tel = tel;
	}

	/**
	 * Gets the telPatronato value for this DeCpi.
	 * 
	 * @return telPatronato
	 */
	public java.lang.String getTelPatronato() {
		return telPatronato;
	}

	/**
	 * Sets the telPatronato value for this DeCpi.
	 * 
	 * @param telPatronato
	 */
	public void setTelPatronato(java.lang.String telPatronato) {
		this.telPatronato = telPatronato;
	}

	/**
	 * Gets the telRifCl value for this DeCpi.
	 * 
	 * @return telRifCl
	 */
	public java.lang.String getTelRifCl() {
		return telRifCl;
	}

	/**
	 * Sets the telRifCl value for this DeCpi.
	 * 
	 * @param telRifCl
	 */
	public void setTelRifCl(java.lang.String telRifCl) {
		this.telRifCl = telRifCl;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeCpi))
			return false;
		DeCpi other = (DeCpi) obj;
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
				&& ((this.codCpi == null && other.getCodCpi() == null)
						|| (this.codCpi != null && this.codCpi.equals(other.getCodCpi())))
				&& ((this.codCpiMin == null && other.getCodCpiMin() == null)
						|| (this.codCpiMin != null && this.codCpiMin.equals(other.getCodCpiMin())))
				&& ((this.codIntermediarioCl == null && other.getCodIntermediarioCl() == null)
						|| (this.codIntermediarioCl != null
								&& this.codIntermediarioCl.equals(other.getCodIntermediarioCl())))
				&& ((this.codMonoTipoFile == null && other.getCodMonoTipoFile() == null)
						|| (this.codMonoTipoFile != null && this.codMonoTipoFile.equals(other.getCodMonoTipoFile())))
				&& ((this.deProvincia == null && other.getDeProvincia() == null)
						|| (this.deProvincia != null && this.deProvincia.equals(other.getDeProvincia())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.descrizioneMin == null && other.getDescrizioneMin() == null)
						|| (this.descrizioneMin != null && this.descrizioneMin.equals(other.getDescrizioneMin())))
				&& ((this.email == null && other.getEmail() == null)
						|| (this.email != null && this.email.equals(other.getEmail())))
				&& ((this.emailMigrazione == null && other.getEmailMigrazione() == null)
						|| (this.emailMigrazione != null && this.emailMigrazione.equals(other.getEmailMigrazione())))
				&& ((this.emailPortale == null && other.getEmailPortale() == null)
						|| (this.emailPortale != null && this.emailPortale.equals(other.getEmailPortale())))
				&& ((this.emailRifCl == null && other.getEmailRifCl() == null)
						|| (this.emailRifCl != null && this.emailRifCl.equals(other.getEmailRifCl())))
				&& ((this.emailServiziOnline == null && other.getEmailServiziOnline() == null)
						|| (this.emailServiziOnline != null
								&& this.emailServiziOnline.equals(other.getEmailServiziOnline())))
				&& ((this.fax == null && other.getFax() == null)
						|| (this.fax != null && this.fax.equals(other.getFax())))
				&& ((this.flgPatronato == null && other.getFlgPatronato() == null)
						|| (this.flgPatronato != null && this.flgPatronato.equals(other.getFlgPatronato())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.indirizzo == null && other.getIndirizzo() == null)
						|| (this.indirizzo != null && this.indirizzo.equals(other.getIndirizzo())))
				&& ((this.indirizzoStampa == null && other.getIndirizzoStampa() == null)
						|| (this.indirizzoStampa != null && this.indirizzoStampa.equals(other.getIndirizzoStampa())))
				&& ((this.localita == null && other.getLocalita() == null)
						|| (this.localita != null && this.localita.equals(other.getLocalita())))
				&& ((this.note == null && other.getNote() == null)
						|| (this.note != null && this.note.equals(other.getNote())))
				&& ((this.orario == null && other.getOrario() == null)
						|| (this.orario != null && this.orario.equals(other.getOrario())))
				&& ((this.responsabile == null && other.getResponsabile() == null)
						|| (this.responsabile != null && this.responsabile.equals(other.getResponsabile())))
				&& ((this.rifSms == null && other.getRifSms() == null)
						|| (this.rifSms != null && this.rifSms.equals(other.getRifSms())))
				&& ((this.tel == null && other.getTel() == null)
						|| (this.tel != null && this.tel.equals(other.getTel())))
				&& ((this.telPatronato == null && other.getTelPatronato() == null)
						|| (this.telPatronato != null && this.telPatronato.equals(other.getTelPatronato())))
				&& ((this.telRifCl == null && other.getTelRifCl() == null)
						|| (this.telRifCl != null && this.telRifCl.equals(other.getTelRifCl())));
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
		if (getCodCpi() != null) {
			_hashCode += getCodCpi().hashCode();
		}
		if (getCodCpiMin() != null) {
			_hashCode += getCodCpiMin().hashCode();
		}
		if (getCodIntermediarioCl() != null) {
			_hashCode += getCodIntermediarioCl().hashCode();
		}
		if (getCodMonoTipoFile() != null) {
			_hashCode += getCodMonoTipoFile().hashCode();
		}
		if (getDeProvincia() != null) {
			_hashCode += getDeProvincia().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getDescrizioneMin() != null) {
			_hashCode += getDescrizioneMin().hashCode();
		}
		if (getEmail() != null) {
			_hashCode += getEmail().hashCode();
		}
		if (getEmailMigrazione() != null) {
			_hashCode += getEmailMigrazione().hashCode();
		}
		if (getEmailPortale() != null) {
			_hashCode += getEmailPortale().hashCode();
		}
		if (getEmailRifCl() != null) {
			_hashCode += getEmailRifCl().hashCode();
		}
		if (getEmailServiziOnline() != null) {
			_hashCode += getEmailServiziOnline().hashCode();
		}
		if (getFax() != null) {
			_hashCode += getFax().hashCode();
		}
		if (getFlgPatronato() != null) {
			_hashCode += getFlgPatronato().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getIndirizzo() != null) {
			_hashCode += getIndirizzo().hashCode();
		}
		if (getIndirizzoStampa() != null) {
			_hashCode += getIndirizzoStampa().hashCode();
		}
		if (getLocalita() != null) {
			_hashCode += getLocalita().hashCode();
		}
		if (getNote() != null) {
			_hashCode += getNote().hashCode();
		}
		if (getOrario() != null) {
			_hashCode += getOrario().hashCode();
		}
		if (getResponsabile() != null) {
			_hashCode += getResponsabile().hashCode();
		}
		if (getRifSms() != null) {
			_hashCode += getRifSms().hashCode();
		}
		if (getTel() != null) {
			_hashCode += getTel().hashCode();
		}
		if (getTelPatronato() != null) {
			_hashCode += getTelPatronato().hashCode();
		}
		if (getTelRifCl() != null) {
			_hashCode += getTelRifCl().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(DeCpi.class,
			true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deCpi"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cap");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codCpi");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codCpi"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codCpiMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codCpiMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codIntermediarioCl");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codIntermediarioCl"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMonoTipoFile");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMonoTipoFile"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
		elemField.setFieldName("descrizioneMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descrizioneMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("email");
		elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("emailMigrazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "emailMigrazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("emailPortale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "emailPortale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("emailRifCl");
		elemField.setXmlName(new javax.xml.namespace.QName("", "emailRifCl"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("emailServiziOnline");
		elemField.setXmlName(new javax.xml.namespace.QName("", "emailServiziOnline"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("fax");
		elemField.setXmlName(new javax.xml.namespace.QName("", "fax"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgPatronato");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgPatronato"));
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
		elemField.setFieldName("indirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indirizzoStampa");
		elemField.setXmlName(new javax.xml.namespace.QName("", "indirizzoStampa"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("localita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "localita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("note");
		elemField.setXmlName(new javax.xml.namespace.QName("", "note"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("orario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "orario"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("responsabile");
		elemField.setXmlName(new javax.xml.namespace.QName("", "responsabile"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rifSms");
		elemField.setXmlName(new javax.xml.namespace.QName("", "rifSms"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tel");
		elemField.setXmlName(new javax.xml.namespace.QName("", "tel"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("telPatronato");
		elemField.setXmlName(new javax.xml.namespace.QName("", "telPatronato"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("telRifCl");
		elemField.setXmlName(new javax.xml.namespace.QName("", "telRifCl"));
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
