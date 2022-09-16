/**
 * Partecipante.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferAccreditamento.request;

public class Partecipante implements java.io.Serializable {
	private java.lang.String codice_fiscale;

	private java.lang.String validita_cf;

	private java.lang.String codice_fiscale_originale;

	private java.lang.Integer cdnlavoratore;

	private java.lang.String codice_provincia;

	private java.lang.String cognome;

	private java.lang.String nome;

	private java.lang.String sesso;

	private java.lang.String nascita_data;

	private java.lang.String nascita_codice_istat;

	private java.lang.String cittadinanza;

	private java.lang.String recapito_telefonico;

	private java.lang.String email;

	private java.lang.String residenza_codice_istat;

	private java.lang.String residenza_indirizzo;

	private java.lang.String residenza_cap;

	private java.lang.String domicilio_codice_istat;

	private java.lang.String domicilio_indirizzo;

	private java.lang.String domicilio_cap;

	private java.lang.String dt_mod_anagrafica;

	public Partecipante() {
	}

	public Partecipante(java.lang.String codice_fiscale, java.lang.String validita_cf,
			java.lang.String codice_fiscale_originale, java.lang.Integer cdnlavoratore,
			java.lang.String codice_provincia, java.lang.String cognome, java.lang.String nome, java.lang.String sesso,
			java.lang.String nascita_data, java.lang.String nascita_codice_istat, java.lang.String cittadinanza,
			java.lang.String recapito_telefonico, java.lang.String email, java.lang.String residenza_codice_istat,
			java.lang.String residenza_indirizzo, java.lang.String residenza_cap,
			java.lang.String domicilio_codice_istat, java.lang.String domicilio_indirizzo,
			java.lang.String domicilio_cap, java.lang.String dt_mod_anagrafica) {
		this.codice_fiscale = codice_fiscale;
		this.validita_cf = validita_cf;
		this.codice_fiscale_originale = codice_fiscale_originale;
		this.cdnlavoratore = cdnlavoratore;
		this.codice_provincia = codice_provincia;
		this.cognome = cognome;
		this.nome = nome;
		this.sesso = sesso;
		this.nascita_data = nascita_data;
		this.nascita_codice_istat = nascita_codice_istat;
		this.cittadinanza = cittadinanza;
		this.recapito_telefonico = recapito_telefonico;
		this.email = email;
		this.residenza_codice_istat = residenza_codice_istat;
		this.residenza_indirizzo = residenza_indirizzo;
		this.residenza_cap = residenza_cap;
		this.domicilio_codice_istat = domicilio_codice_istat;
		this.domicilio_indirizzo = domicilio_indirizzo;
		this.domicilio_cap = domicilio_cap;
		this.dt_mod_anagrafica = dt_mod_anagrafica;
	}

	/**
	 * Gets the codice_fiscale value for this Partecipante.
	 * 
	 * @return codice_fiscale
	 */
	public java.lang.String getCodice_fiscale() {
		return codice_fiscale;
	}

	/**
	 * Sets the codice_fiscale value for this Partecipante.
	 * 
	 * @param codice_fiscale
	 */
	public void setCodice_fiscale(java.lang.String codice_fiscale) {
		this.codice_fiscale = codice_fiscale;
	}

	/**
	 * Gets the validita_cf value for this Partecipante.
	 * 
	 * @return validita_cf
	 */
	public java.lang.String getValidita_cf() {
		return validita_cf;
	}

	/**
	 * Sets the validita_cf value for this Partecipante.
	 * 
	 * @param validita_cf
	 */
	public void setValidita_cf(java.lang.String validita_cf) {
		this.validita_cf = validita_cf;
	}

	/**
	 * Gets the codice_fiscale_originale value for this Partecipante.
	 * 
	 * @return codice_fiscale_originale
	 */
	public java.lang.String getCodice_fiscale_originale() {
		return codice_fiscale_originale;
	}

	/**
	 * Sets the codice_fiscale_originale value for this Partecipante.
	 * 
	 * @param codice_fiscale_originale
	 */
	public void setCodice_fiscale_originale(java.lang.String codice_fiscale_originale) {
		this.codice_fiscale_originale = codice_fiscale_originale;
	}

	/**
	 * Gets the cdnlavoratore value for this Partecipante.
	 * 
	 * @return cdnlavoratore
	 */
	public java.lang.Integer getCdnlavoratore() {
		return cdnlavoratore;
	}

	/**
	 * Sets the cdnlavoratore value for this Partecipante.
	 * 
	 * @param cdnlavoratore
	 */
	public void setCdnlavoratore(java.lang.Integer cdnlavoratore) {
		this.cdnlavoratore = cdnlavoratore;
	}

	/**
	 * Gets the codice_provincia value for this Partecipante.
	 * 
	 * @return codice_provincia
	 */
	public java.lang.String getCodice_provincia() {
		return codice_provincia;
	}

	/**
	 * Sets the codice_provincia value for this Partecipante.
	 * 
	 * @param codice_provincia
	 */
	public void setCodice_provincia(java.lang.String codice_provincia) {
		this.codice_provincia = codice_provincia;
	}

	/**
	 * Gets the cognome value for this Partecipante.
	 * 
	 * @return cognome
	 */
	public java.lang.String getCognome() {
		return cognome;
	}

	/**
	 * Sets the cognome value for this Partecipante.
	 * 
	 * @param cognome
	 */
	public void setCognome(java.lang.String cognome) {
		this.cognome = cognome;
	}

	/**
	 * Gets the nome value for this Partecipante.
	 * 
	 * @return nome
	 */
	public java.lang.String getNome() {
		return nome;
	}

	/**
	 * Sets the nome value for this Partecipante.
	 * 
	 * @param nome
	 */
	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	/**
	 * Gets the sesso value for this Partecipante.
	 * 
	 * @return sesso
	 */
	public java.lang.String getSesso() {
		return sesso;
	}

	/**
	 * Sets the sesso value for this Partecipante.
	 * 
	 * @param sesso
	 */
	public void setSesso(java.lang.String sesso) {
		this.sesso = sesso;
	}

	/**
	 * Gets the nascita_data value for this Partecipante.
	 * 
	 * @return nascita_data
	 */
	public java.lang.String getNascita_data() {
		return nascita_data;
	}

	/**
	 * Sets the nascita_data value for this Partecipante.
	 * 
	 * @param nascita_data
	 */
	public void setNascita_data(java.lang.String nascita_data) {
		this.nascita_data = nascita_data;
	}

	/**
	 * Gets the nascita_codice_istat value for this Partecipante.
	 * 
	 * @return nascita_codice_istat
	 */
	public java.lang.String getNascita_codice_istat() {
		return nascita_codice_istat;
	}

	/**
	 * Sets the nascita_codice_istat value for this Partecipante.
	 * 
	 * @param nascita_codice_istat
	 */
	public void setNascita_codice_istat(java.lang.String nascita_codice_istat) {
		this.nascita_codice_istat = nascita_codice_istat;
	}

	/**
	 * Gets the cittadinanza value for this Partecipante.
	 * 
	 * @return cittadinanza
	 */
	public java.lang.String getCittadinanza() {
		return cittadinanza;
	}

	/**
	 * Sets the cittadinanza value for this Partecipante.
	 * 
	 * @param cittadinanza
	 */
	public void setCittadinanza(java.lang.String cittadinanza) {
		this.cittadinanza = cittadinanza;
	}

	/**
	 * Gets the recapito_telefonico value for this Partecipante.
	 * 
	 * @return recapito_telefonico
	 */
	public java.lang.String getRecapito_telefonico() {
		return recapito_telefonico;
	}

	/**
	 * Sets the recapito_telefonico value for this Partecipante.
	 * 
	 * @param recapito_telefonico
	 */
	public void setRecapito_telefonico(java.lang.String recapito_telefonico) {
		this.recapito_telefonico = recapito_telefonico;
	}

	/**
	 * Gets the email value for this Partecipante.
	 * 
	 * @return email
	 */
	public java.lang.String getEmail() {
		return email;
	}

	/**
	 * Sets the email value for this Partecipante.
	 * 
	 * @param email
	 */
	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	/**
	 * Gets the residenza_codice_istat value for this Partecipante.
	 * 
	 * @return residenza_codice_istat
	 */
	public java.lang.String getResidenza_codice_istat() {
		return residenza_codice_istat;
	}

	/**
	 * Sets the residenza_codice_istat value for this Partecipante.
	 * 
	 * @param residenza_codice_istat
	 */
	public void setResidenza_codice_istat(java.lang.String residenza_codice_istat) {
		this.residenza_codice_istat = residenza_codice_istat;
	}

	/**
	 * Gets the residenza_indirizzo value for this Partecipante.
	 * 
	 * @return residenza_indirizzo
	 */
	public java.lang.String getResidenza_indirizzo() {
		return residenza_indirizzo;
	}

	/**
	 * Sets the residenza_indirizzo value for this Partecipante.
	 * 
	 * @param residenza_indirizzo
	 */
	public void setResidenza_indirizzo(java.lang.String residenza_indirizzo) {
		this.residenza_indirizzo = residenza_indirizzo;
	}

	/**
	 * Gets the residenza_cap value for this Partecipante.
	 * 
	 * @return residenza_cap
	 */
	public java.lang.String getResidenza_cap() {
		return residenza_cap;
	}

	/**
	 * Sets the residenza_cap value for this Partecipante.
	 * 
	 * @param residenza_cap
	 */
	public void setResidenza_cap(java.lang.String residenza_cap) {
		this.residenza_cap = residenza_cap;
	}

	/**
	 * Gets the domicilio_codice_istat value for this Partecipante.
	 * 
	 * @return domicilio_codice_istat
	 */
	public java.lang.String getDomicilio_codice_istat() {
		return domicilio_codice_istat;
	}

	/**
	 * Sets the domicilio_codice_istat value for this Partecipante.
	 * 
	 * @param domicilio_codice_istat
	 */
	public void setDomicilio_codice_istat(java.lang.String domicilio_codice_istat) {
		this.domicilio_codice_istat = domicilio_codice_istat;
	}

	/**
	 * Gets the domicilio_indirizzo value for this Partecipante.
	 * 
	 * @return domicilio_indirizzo
	 */
	public java.lang.String getDomicilio_indirizzo() {
		return domicilio_indirizzo;
	}

	/**
	 * Sets the domicilio_indirizzo value for this Partecipante.
	 * 
	 * @param domicilio_indirizzo
	 */
	public void setDomicilio_indirizzo(java.lang.String domicilio_indirizzo) {
		this.domicilio_indirizzo = domicilio_indirizzo;
	}

	/**
	 * Gets the domicilio_cap value for this Partecipante.
	 * 
	 * @return domicilio_cap
	 */
	public java.lang.String getDomicilio_cap() {
		return domicilio_cap;
	}

	/**
	 * Sets the domicilio_cap value for this Partecipante.
	 * 
	 * @param domicilio_cap
	 */
	public void setDomicilio_cap(java.lang.String domicilio_cap) {
		this.domicilio_cap = domicilio_cap;
	}

	/**
	 * Gets the dt_mod_anagrafica value for this Partecipante.
	 * 
	 * @return dt_mod_anagrafica
	 */
	public java.lang.String getDt_mod_anagrafica() {
		return dt_mod_anagrafica;
	}

	/**
	 * Sets the dt_mod_anagrafica value for this Partecipante.
	 * 
	 * @param dt_mod_anagrafica
	 */
	public void setDt_mod_anagrafica(java.lang.String dt_mod_anagrafica) {
		this.dt_mod_anagrafica = dt_mod_anagrafica;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Partecipante))
			return false;
		Partecipante other = (Partecipante) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.codice_fiscale == null && other.getCodice_fiscale() == null)
						|| (this.codice_fiscale != null && this.codice_fiscale.equals(other.getCodice_fiscale())))
				&& ((this.validita_cf == null && other.getValidita_cf() == null)
						|| (this.validita_cf != null && this.validita_cf.equals(other.getValidita_cf())))
				&& ((this.codice_fiscale_originale == null && other.getCodice_fiscale_originale() == null)
						|| (this.codice_fiscale_originale != null
								&& this.codice_fiscale_originale.equals(other.getCodice_fiscale_originale())))
				&& ((this.cdnlavoratore == null && other.getCdnlavoratore() == null)
						|| (this.cdnlavoratore != null && this.cdnlavoratore.equals(other.getCdnlavoratore())))
				&& ((this.codice_provincia == null && other.getCodice_provincia() == null)
						|| (this.codice_provincia != null && this.codice_provincia.equals(other.getCodice_provincia())))
				&& ((this.cognome == null && other.getCognome() == null)
						|| (this.cognome != null && this.cognome.equals(other.getCognome())))
				&& ((this.nome == null && other.getNome() == null)
						|| (this.nome != null && this.nome.equals(other.getNome())))
				&& ((this.sesso == null && other.getSesso() == null)
						|| (this.sesso != null && this.sesso.equals(other.getSesso())))
				&& ((this.nascita_data == null && other.getNascita_data() == null)
						|| (this.nascita_data != null && this.nascita_data.equals(other.getNascita_data())))
				&& ((this.nascita_codice_istat == null && other.getNascita_codice_istat() == null)
						|| (this.nascita_codice_istat != null
								&& this.nascita_codice_istat.equals(other.getNascita_codice_istat())))
				&& ((this.cittadinanza == null && other.getCittadinanza() == null)
						|| (this.cittadinanza != null && this.cittadinanza.equals(other.getCittadinanza())))
				&& ((this.recapito_telefonico == null && other.getRecapito_telefonico() == null)
						|| (this.recapito_telefonico != null
								&& this.recapito_telefonico.equals(other.getRecapito_telefonico())))
				&& ((this.email == null && other.getEmail() == null)
						|| (this.email != null && this.email.equals(other.getEmail())))
				&& ((this.residenza_codice_istat == null && other.getResidenza_codice_istat() == null)
						|| (this.residenza_codice_istat != null
								&& this.residenza_codice_istat.equals(other.getResidenza_codice_istat())))
				&& ((this.residenza_indirizzo == null && other.getResidenza_indirizzo() == null)
						|| (this.residenza_indirizzo != null
								&& this.residenza_indirizzo.equals(other.getResidenza_indirizzo())))
				&& ((this.residenza_cap == null && other.getResidenza_cap() == null)
						|| (this.residenza_cap != null && this.residenza_cap.equals(other.getResidenza_cap())))
				&& ((this.domicilio_codice_istat == null && other.getDomicilio_codice_istat() == null)
						|| (this.domicilio_codice_istat != null
								&& this.domicilio_codice_istat.equals(other.getDomicilio_codice_istat())))
				&& ((this.domicilio_indirizzo == null && other.getDomicilio_indirizzo() == null)
						|| (this.domicilio_indirizzo != null
								&& this.domicilio_indirizzo.equals(other.getDomicilio_indirizzo())))
				&& ((this.domicilio_cap == null && other.getDomicilio_cap() == null)
						|| (this.domicilio_cap != null && this.domicilio_cap.equals(other.getDomicilio_cap())))
				&& ((this.dt_mod_anagrafica == null && other.getDt_mod_anagrafica() == null)
						|| (this.dt_mod_anagrafica != null
								&& this.dt_mod_anagrafica.equals(other.getDt_mod_anagrafica())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getCodice_fiscale() != null) {
			_hashCode += getCodice_fiscale().hashCode();
		}
		if (getValidita_cf() != null) {
			_hashCode += getValidita_cf().hashCode();
		}
		if (getCodice_fiscale_originale() != null) {
			_hashCode += getCodice_fiscale_originale().hashCode();
		}
		if (getCdnlavoratore() != null) {
			_hashCode += getCdnlavoratore().hashCode();
		}
		if (getCodice_provincia() != null) {
			_hashCode += getCodice_provincia().hashCode();
		}
		if (getCognome() != null) {
			_hashCode += getCognome().hashCode();
		}
		if (getNome() != null) {
			_hashCode += getNome().hashCode();
		}
		if (getSesso() != null) {
			_hashCode += getSesso().hashCode();
		}
		if (getNascita_data() != null) {
			_hashCode += getNascita_data().hashCode();
		}
		if (getNascita_codice_istat() != null) {
			_hashCode += getNascita_codice_istat().hashCode();
		}
		if (getCittadinanza() != null) {
			_hashCode += getCittadinanza().hashCode();
		}
		if (getRecapito_telefonico() != null) {
			_hashCode += getRecapito_telefonico().hashCode();
		}
		if (getEmail() != null) {
			_hashCode += getEmail().hashCode();
		}
		if (getResidenza_codice_istat() != null) {
			_hashCode += getResidenza_codice_istat().hashCode();
		}
		if (getResidenza_indirizzo() != null) {
			_hashCode += getResidenza_indirizzo().hashCode();
		}
		if (getResidenza_cap() != null) {
			_hashCode += getResidenza_cap().hashCode();
		}
		if (getDomicilio_codice_istat() != null) {
			_hashCode += getDomicilio_codice_istat().hashCode();
		}
		if (getDomicilio_indirizzo() != null) {
			_hashCode += getDomicilio_indirizzo().hashCode();
		}
		if (getDomicilio_cap() != null) {
			_hashCode += getDomicilio_cap().hashCode();
		}
		if (getDt_mod_anagrafica() != null) {
			_hashCode += getDt_mod_anagrafica().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Partecipante.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"Partecipante"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codice_fiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codice_fiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("validita_cf");
		elemField.setXmlName(new javax.xml.namespace.QName("", "validita_cf"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codice_fiscale_originale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codice_fiscale_originale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cdnlavoratore");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cdnlavoratore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codice_provincia");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codice_provincia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sesso");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sesso"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nascita_data");
		elemField.setXmlName(new javax.xml.namespace.QName("", "nascita_data"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nascita_codice_istat");
		elemField.setXmlName(new javax.xml.namespace.QName("", "nascita_codice_istat"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cittadinanza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("recapito_telefonico");
		elemField.setXmlName(new javax.xml.namespace.QName("", "recapito_telefonico"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("email");
		elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("residenza_codice_istat");
		elemField.setXmlName(new javax.xml.namespace.QName("", "residenza_codice_istat"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("residenza_indirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "residenza_indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("residenza_cap");
		elemField.setXmlName(new javax.xml.namespace.QName("", "residenza_cap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("domicilio_codice_istat");
		elemField.setXmlName(new javax.xml.namespace.QName("", "domicilio_codice_istat"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("domicilio_indirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "domicilio_indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("domicilio_cap");
		elemField.setXmlName(new javax.xml.namespace.QName("", "domicilio_cap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dt_mod_anagrafica");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dt_mod_anagrafica"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
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
