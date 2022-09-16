/**
 * Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

public class Response implements java.io.Serializable {
	private java.lang.String cf;

	private java.lang.String cognome;

	private java.util.Date dataDid;

	private java.util.Date dataInizioStatoOcc;

	private java.util.Date dataNascita;

	private java.lang.String errore;

	private it.eng.sil.coop.webservices.infosilperservsoc.Appuntamento[] listaAppuntamenti;

	private it.eng.sil.coop.webservices.infosilperservsoc.AzioneConcordata[] listaAzioniConcordate;

	private it.eng.sil.coop.webservices.infosilperservsoc.Colloquio[] listaColloqui;

	private it.eng.sil.coop.webservices.infosilperservsoc.SegnalazioneRosa[] listaSegnalazioniRosa;

	private java.lang.String nome;

	private java.lang.String statoDid;

	private java.lang.String statoOcc;

	private java.lang.String tipoDid;

	public Response() {
	}

	public Response(java.lang.String cf, java.lang.String cognome, java.util.Date dataDid,
			java.util.Date dataInizioStatoOcc, java.util.Date dataNascita, java.lang.String errore,
			it.eng.sil.coop.webservices.infosilperservsoc.Appuntamento[] listaAppuntamenti,
			it.eng.sil.coop.webservices.infosilperservsoc.AzioneConcordata[] listaAzioniConcordate,
			it.eng.sil.coop.webservices.infosilperservsoc.Colloquio[] listaColloqui,
			it.eng.sil.coop.webservices.infosilperservsoc.SegnalazioneRosa[] listaSegnalazioniRosa,
			java.lang.String nome, java.lang.String statoDid, java.lang.String statoOcc, java.lang.String tipoDid) {
		this.cf = cf;
		this.cognome = cognome;
		this.dataDid = dataDid;
		this.dataInizioStatoOcc = dataInizioStatoOcc;
		this.dataNascita = dataNascita;
		this.errore = errore;
		this.listaAppuntamenti = listaAppuntamenti;
		this.listaAzioniConcordate = listaAzioniConcordate;
		this.listaColloqui = listaColloqui;
		this.listaSegnalazioniRosa = listaSegnalazioniRosa;
		this.nome = nome;
		this.statoDid = statoDid;
		this.statoOcc = statoOcc;
		this.tipoDid = tipoDid;
	}

	/**
	 * Gets the cf value for this Response.
	 * 
	 * @return cf
	 */
	public java.lang.String getCf() {
		return cf;
	}

	/**
	 * Sets the cf value for this Response.
	 * 
	 * @param cf
	 */
	public void setCf(java.lang.String cf) {
		this.cf = cf;
	}

	/**
	 * Gets the cognome value for this Response.
	 * 
	 * @return cognome
	 */
	public java.lang.String getCognome() {
		return cognome;
	}

	/**
	 * Sets the cognome value for this Response.
	 * 
	 * @param cognome
	 */
	public void setCognome(java.lang.String cognome) {
		this.cognome = cognome;
	}

	/**
	 * Gets the dataDid value for this Response.
	 * 
	 * @return dataDid
	 */
	public java.util.Date getDataDid() {
		return dataDid;
	}

	/**
	 * Sets the dataDid value for this Response.
	 * 
	 * @param dataDid
	 */
	public void setDataDid(java.util.Date dataDid) {
		this.dataDid = dataDid;
	}

	/**
	 * Gets the dataInizioStatoOcc value for this Response.
	 * 
	 * @return dataInizioStatoOcc
	 */
	public java.util.Date getDataInizioStatoOcc() {
		return dataInizioStatoOcc;
	}

	/**
	 * Sets the dataInizioStatoOcc value for this Response.
	 * 
	 * @param dataInizioStatoOcc
	 */
	public void setDataInizioStatoOcc(java.util.Date dataInizioStatoOcc) {
		this.dataInizioStatoOcc = dataInizioStatoOcc;
	}

	/**
	 * Gets the dataNascita value for this Response.
	 * 
	 * @return dataNascita
	 */
	public java.util.Date getDataNascita() {
		return dataNascita;
	}

	/**
	 * Sets the dataNascita value for this Response.
	 * 
	 * @param dataNascita
	 */
	public void setDataNascita(java.util.Date dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * Gets the errore value for this Response.
	 * 
	 * @return errore
	 */
	public java.lang.String getErrore() {
		return errore;
	}

	/**
	 * Sets the errore value for this Response.
	 * 
	 * @param errore
	 */
	public void setErrore(java.lang.String errore) {
		this.errore = errore;
	}

	/**
	 * Gets the listaAppuntamenti value for this Response.
	 * 
	 * @return listaAppuntamenti
	 */
	public it.eng.sil.coop.webservices.infosilperservsoc.Appuntamento[] getListaAppuntamenti() {
		return listaAppuntamenti;
	}

	/**
	 * Sets the listaAppuntamenti value for this Response.
	 * 
	 * @param listaAppuntamenti
	 */
	public void setListaAppuntamenti(it.eng.sil.coop.webservices.infosilperservsoc.Appuntamento[] listaAppuntamenti) {
		this.listaAppuntamenti = listaAppuntamenti;
	}

	public it.eng.sil.coop.webservices.infosilperservsoc.Appuntamento getListaAppuntamenti(int i) {
		return this.listaAppuntamenti[i];
	}

	public void setListaAppuntamenti(int i, it.eng.sil.coop.webservices.infosilperservsoc.Appuntamento _value) {
		this.listaAppuntamenti[i] = _value;
	}

	/**
	 * Gets the listaAzioniConcordate value for this Response.
	 * 
	 * @return listaAzioniConcordate
	 */
	public it.eng.sil.coop.webservices.infosilperservsoc.AzioneConcordata[] getListaAzioniConcordate() {
		return listaAzioniConcordate;
	}

	/**
	 * Sets the listaAzioniConcordate value for this Response.
	 * 
	 * @param listaAzioniConcordate
	 */
	public void setListaAzioniConcordate(
			it.eng.sil.coop.webservices.infosilperservsoc.AzioneConcordata[] listaAzioniConcordate) {
		this.listaAzioniConcordate = listaAzioniConcordate;
	}

	public it.eng.sil.coop.webservices.infosilperservsoc.AzioneConcordata getListaAzioniConcordate(int i) {
		return this.listaAzioniConcordate[i];
	}

	public void setListaAzioniConcordate(int i, it.eng.sil.coop.webservices.infosilperservsoc.AzioneConcordata _value) {
		this.listaAzioniConcordate[i] = _value;
	}

	/**
	 * Gets the listaColloqui value for this Response.
	 * 
	 * @return listaColloqui
	 */
	public it.eng.sil.coop.webservices.infosilperservsoc.Colloquio[] getListaColloqui() {
		return listaColloqui;
	}

	/**
	 * Sets the listaColloqui value for this Response.
	 * 
	 * @param listaColloqui
	 */
	public void setListaColloqui(it.eng.sil.coop.webservices.infosilperservsoc.Colloquio[] listaColloqui) {
		this.listaColloqui = listaColloqui;
	}

	public it.eng.sil.coop.webservices.infosilperservsoc.Colloquio getListaColloqui(int i) {
		return this.listaColloqui[i];
	}

	public void setListaColloqui(int i, it.eng.sil.coop.webservices.infosilperservsoc.Colloquio _value) {
		this.listaColloqui[i] = _value;
	}

	/**
	 * Gets the listaSegnalazioniRosa value for this Response.
	 * 
	 * @return listaSegnalazioniRosa
	 */
	public it.eng.sil.coop.webservices.infosilperservsoc.SegnalazioneRosa[] getListaSegnalazioniRosa() {
		return listaSegnalazioniRosa;
	}

	/**
	 * Sets the listaSegnalazioniRosa value for this Response.
	 * 
	 * @param listaSegnalazioniRosa
	 */
	public void setListaSegnalazioniRosa(
			it.eng.sil.coop.webservices.infosilperservsoc.SegnalazioneRosa[] listaSegnalazioniRosa) {
		this.listaSegnalazioniRosa = listaSegnalazioniRosa;
	}

	public it.eng.sil.coop.webservices.infosilperservsoc.SegnalazioneRosa getListaSegnalazioniRosa(int i) {
		return this.listaSegnalazioniRosa[i];
	}

	public void setListaSegnalazioniRosa(int i, it.eng.sil.coop.webservices.infosilperservsoc.SegnalazioneRosa _value) {
		this.listaSegnalazioniRosa[i] = _value;
	}

	/**
	 * Gets the nome value for this Response.
	 * 
	 * @return nome
	 */
	public java.lang.String getNome() {
		return nome;
	}

	/**
	 * Sets the nome value for this Response.
	 * 
	 * @param nome
	 */
	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	/**
	 * Gets the statoDid value for this Response.
	 * 
	 * @return statoDid
	 */
	public java.lang.String getStatoDid() {
		return statoDid;
	}

	/**
	 * Sets the statoDid value for this Response.
	 * 
	 * @param statoDid
	 */
	public void setStatoDid(java.lang.String statoDid) {
		this.statoDid = statoDid;
	}

	/**
	 * Gets the statoOcc value for this Response.
	 * 
	 * @return statoOcc
	 */
	public java.lang.String getStatoOcc() {
		return statoOcc;
	}

	/**
	 * Sets the statoOcc value for this Response.
	 * 
	 * @param statoOcc
	 */
	public void setStatoOcc(java.lang.String statoOcc) {
		this.statoOcc = statoOcc;
	}

	/**
	 * Gets the tipoDid value for this Response.
	 * 
	 * @return tipoDid
	 */
	public java.lang.String getTipoDid() {
		return tipoDid;
	}

	/**
	 * Sets the tipoDid value for this Response.
	 * 
	 * @param tipoDid
	 */
	public void setTipoDid(java.lang.String tipoDid) {
		this.tipoDid = tipoDid;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Response))
			return false;
		Response other = (Response) obj;
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
				&& ((this.cf == null && other.getCf() == null) || (this.cf != null && this.cf.equals(other.getCf())))
				&& ((this.cognome == null && other.getCognome() == null)
						|| (this.cognome != null && this.cognome.equals(other.getCognome())))
				&& ((this.dataDid == null && other.getDataDid() == null)
						|| (this.dataDid != null && this.dataDid.equals(other.getDataDid())))
				&& ((this.dataInizioStatoOcc == null && other.getDataInizioStatoOcc() == null)
						|| (this.dataInizioStatoOcc != null
								&& this.dataInizioStatoOcc.equals(other.getDataInizioStatoOcc())))
				&& ((this.dataNascita == null && other.getDataNascita() == null)
						|| (this.dataNascita != null && this.dataNascita.equals(other.getDataNascita())))
				&& ((this.errore == null && other.getErrore() == null)
						|| (this.errore != null && this.errore.equals(other.getErrore())))
				&& ((this.listaAppuntamenti == null && other.getListaAppuntamenti() == null)
						|| (this.listaAppuntamenti != null
								&& java.util.Arrays.equals(this.listaAppuntamenti, other.getListaAppuntamenti())))
				&& ((this.listaAzioniConcordate == null && other.getListaAzioniConcordate() == null)
						|| (this.listaAzioniConcordate != null && java.util.Arrays.equals(this.listaAzioniConcordate,
								other.getListaAzioniConcordate())))
				&& ((this.listaColloqui == null && other.getListaColloqui() == null) || (this.listaColloqui != null
						&& java.util.Arrays.equals(this.listaColloqui, other.getListaColloqui())))
				&& ((this.listaSegnalazioniRosa == null && other.getListaSegnalazioniRosa() == null)
						|| (this.listaSegnalazioniRosa != null && java.util.Arrays.equals(this.listaSegnalazioniRosa,
								other.getListaSegnalazioniRosa())))
				&& ((this.nome == null && other.getNome() == null)
						|| (this.nome != null && this.nome.equals(other.getNome())))
				&& ((this.statoDid == null && other.getStatoDid() == null)
						|| (this.statoDid != null && this.statoDid.equals(other.getStatoDid())))
				&& ((this.statoOcc == null && other.getStatoOcc() == null)
						|| (this.statoOcc != null && this.statoOcc.equals(other.getStatoOcc())))
				&& ((this.tipoDid == null && other.getTipoDid() == null)
						|| (this.tipoDid != null && this.tipoDid.equals(other.getTipoDid())));
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
		if (getCf() != null) {
			_hashCode += getCf().hashCode();
		}
		if (getCognome() != null) {
			_hashCode += getCognome().hashCode();
		}
		if (getDataDid() != null) {
			_hashCode += getDataDid().hashCode();
		}
		if (getDataInizioStatoOcc() != null) {
			_hashCode += getDataInizioStatoOcc().hashCode();
		}
		if (getDataNascita() != null) {
			_hashCode += getDataNascita().hashCode();
		}
		if (getErrore() != null) {
			_hashCode += getErrore().hashCode();
		}
		if (getListaAppuntamenti() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getListaAppuntamenti()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getListaAppuntamenti(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getListaAzioniConcordate() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getListaAzioniConcordate()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getListaAzioniConcordate(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getListaColloqui() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getListaColloqui()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getListaColloqui(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getListaSegnalazioniRosa() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getListaSegnalazioniRosa()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getListaSegnalazioniRosa(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getNome() != null) {
			_hashCode += getNome().hashCode();
		}
		if (getStatoDid() != null) {
			_hashCode += getStatoDid().hashCode();
		}
		if (getStatoOcc() != null) {
			_hashCode += getStatoOcc().hashCode();
		}
		if (getTipoDid() != null) {
			_hashCode += getTipoDid().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Response.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "Response"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cf");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "cf"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataDid");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataDid"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataInizioStatoOcc");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataInizioStatoOcc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("errore");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "errore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("listaAppuntamenti");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "listaAppuntamenti"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "Appuntamento"));
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("listaAzioniConcordate");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "listaAzioniConcordate"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "AzioneConcordata"));
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("listaColloqui");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "listaColloqui"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "Colloquio"));
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("listaSegnalazioniRosa");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "listaSegnalazioniRosa"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "SegnalazioneRosa"));
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("statoDid");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "statoDid"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("statoOcc");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "statoOcc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipoDid");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "tipoDid"));
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
