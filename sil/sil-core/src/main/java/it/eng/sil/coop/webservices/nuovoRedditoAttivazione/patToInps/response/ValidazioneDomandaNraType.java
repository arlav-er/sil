/**
 * ValidazioneDomandaNraType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response;

public class ValidazioneDomandaNraType implements java.io.Serializable {
	private int idDomandaWeb;

	private int idDomandaIntranet;

	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente;

	private java.lang.String numeroProvvedimento;

	private java.util.Calendar dataProvvedimento;

	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.EsitoProvvedimentoType esitoElaborazione;

	private java.lang.Integer codiceReiezione;

	private java.util.Calendar dataCreazioneComunicazione;

	private java.lang.String identificativoComunicazione;

	private java.lang.String codiceOperatore;

	private java.lang.String identificativoComunicazioneRichiesta;

	public ValidazioneDomandaNraType() {
	}

	public ValidazioneDomandaNraType(int idDomandaWeb, int idDomandaIntranet,
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente,
			java.lang.String numeroProvvedimento, java.util.Calendar dataProvvedimento,
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.EsitoProvvedimentoType esitoElaborazione,
			java.lang.Integer codiceReiezione, java.util.Calendar dataCreazioneComunicazione,
			java.lang.String identificativoComunicazione, java.lang.String codiceOperatore,
			java.lang.String identificativoComunicazioneRichiesta) {
		this.idDomandaWeb = idDomandaWeb;
		this.idDomandaIntranet = idDomandaIntranet;
		this.richiedente = richiedente;
		this.numeroProvvedimento = numeroProvvedimento;
		this.dataProvvedimento = dataProvvedimento;
		this.esitoElaborazione = esitoElaborazione;
		this.codiceReiezione = codiceReiezione;
		this.dataCreazioneComunicazione = dataCreazioneComunicazione;
		this.identificativoComunicazione = identificativoComunicazione;
		this.codiceOperatore = codiceOperatore;
		this.identificativoComunicazioneRichiesta = identificativoComunicazioneRichiesta;
	}

	/**
	 * Gets the idDomandaWeb value for this ValidazioneDomandaNraType.
	 * 
	 * @return idDomandaWeb
	 */
	public int getIdDomandaWeb() {
		return idDomandaWeb;
	}

	/**
	 * Sets the idDomandaWeb value for this ValidazioneDomandaNraType.
	 * 
	 * @param idDomandaWeb
	 */
	public void setIdDomandaWeb(int idDomandaWeb) {
		this.idDomandaWeb = idDomandaWeb;
	}

	/**
	 * Gets the idDomandaIntranet value for this ValidazioneDomandaNraType.
	 * 
	 * @return idDomandaIntranet
	 */
	public int getIdDomandaIntranet() {
		return idDomandaIntranet;
	}

	/**
	 * Sets the idDomandaIntranet value for this ValidazioneDomandaNraType.
	 * 
	 * @param idDomandaIntranet
	 */
	public void setIdDomandaIntranet(int idDomandaIntranet) {
		this.idDomandaIntranet = idDomandaIntranet;
	}

	/**
	 * Gets the richiedente value for this ValidazioneDomandaNraType.
	 * 
	 * @return richiedente
	 */
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType getRichiedente() {
		return richiedente;
	}

	/**
	 * Sets the richiedente value for this ValidazioneDomandaNraType.
	 * 
	 * @param richiedente
	 */
	public void setRichiedente(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente) {
		this.richiedente = richiedente;
	}

	/**
	 * Gets the numeroProvvedimento value for this ValidazioneDomandaNraType.
	 * 
	 * @return numeroProvvedimento
	 */
	public java.lang.String getNumeroProvvedimento() {
		return numeroProvvedimento;
	}

	/**
	 * Sets the numeroProvvedimento value for this ValidazioneDomandaNraType.
	 * 
	 * @param numeroProvvedimento
	 */
	public void setNumeroProvvedimento(java.lang.String numeroProvvedimento) {
		this.numeroProvvedimento = numeroProvvedimento;
	}

	/**
	 * Gets the dataProvvedimento value for this ValidazioneDomandaNraType.
	 * 
	 * @return dataProvvedimento
	 */
	public java.util.Calendar getDataProvvedimento() {
		return dataProvvedimento;
	}

	/**
	 * Sets the dataProvvedimento value for this ValidazioneDomandaNraType.
	 * 
	 * @param dataProvvedimento
	 */
	public void setDataProvvedimento(java.util.Calendar dataProvvedimento) {
		this.dataProvvedimento = dataProvvedimento;
	}

	/**
	 * Gets the esitoElaborazione value for this ValidazioneDomandaNraType.
	 * 
	 * @return esitoElaborazione
	 */
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.EsitoProvvedimentoType getEsitoElaborazione() {
		return esitoElaborazione;
	}

	/**
	 * Sets the esitoElaborazione value for this ValidazioneDomandaNraType.
	 * 
	 * @param esitoElaborazione
	 */
	public void setEsitoElaborazione(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.EsitoProvvedimentoType esitoElaborazione) {
		this.esitoElaborazione = esitoElaborazione;
	}

	/**
	 * Gets the codiceReiezione value for this ValidazioneDomandaNraType.
	 * 
	 * @return codiceReiezione
	 */
	public java.lang.Integer getCodiceReiezione() {
		return codiceReiezione;
	}

	/**
	 * Sets the codiceReiezione value for this ValidazioneDomandaNraType.
	 * 
	 * @param codiceReiezione
	 */
	public void setCodiceReiezione(java.lang.Integer codiceReiezione) {
		this.codiceReiezione = codiceReiezione;
	}

	/**
	 * Gets the dataCreazioneComunicazione value for this ValidazioneDomandaNraType.
	 * 
	 * @return dataCreazioneComunicazione
	 */
	public java.util.Calendar getDataCreazioneComunicazione() {
		return dataCreazioneComunicazione;
	}

	/**
	 * Sets the dataCreazioneComunicazione value for this ValidazioneDomandaNraType.
	 * 
	 * @param dataCreazioneComunicazione
	 */
	public void setDataCreazioneComunicazione(java.util.Calendar dataCreazioneComunicazione) {
		this.dataCreazioneComunicazione = dataCreazioneComunicazione;
	}

	/**
	 * Gets the identificativoComunicazione value for this ValidazioneDomandaNraType.
	 * 
	 * @return identificativoComunicazione
	 */
	public java.lang.String getIdentificativoComunicazione() {
		return identificativoComunicazione;
	}

	/**
	 * Sets the identificativoComunicazione value for this ValidazioneDomandaNraType.
	 * 
	 * @param identificativoComunicazione
	 */
	public void setIdentificativoComunicazione(java.lang.String identificativoComunicazione) {
		this.identificativoComunicazione = identificativoComunicazione;
	}

	/**
	 * Gets the codiceOperatore value for this ValidazioneDomandaNraType.
	 * 
	 * @return codiceOperatore
	 */
	public java.lang.String getCodiceOperatore() {
		return codiceOperatore;
	}

	/**
	 * Sets the codiceOperatore value for this ValidazioneDomandaNraType.
	 * 
	 * @param codiceOperatore
	 */
	public void setCodiceOperatore(java.lang.String codiceOperatore) {
		this.codiceOperatore = codiceOperatore;
	}

	/**
	 * Gets the identificativoComunicazioneRichiesta value for this ValidazioneDomandaNraType.
	 * 
	 * @return identificativoComunicazioneRichiesta
	 */
	public java.lang.String getIdentificativoComunicazioneRichiesta() {
		return identificativoComunicazioneRichiesta;
	}

	/**
	 * Sets the identificativoComunicazioneRichiesta value for this ValidazioneDomandaNraType.
	 * 
	 * @param identificativoComunicazioneRichiesta
	 */
	public void setIdentificativoComunicazioneRichiesta(java.lang.String identificativoComunicazioneRichiesta) {
		this.identificativoComunicazioneRichiesta = identificativoComunicazioneRichiesta;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ValidazioneDomandaNraType))
			return false;
		ValidazioneDomandaNraType other = (ValidazioneDomandaNraType) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.idDomandaWeb == other.getIdDomandaWeb()
				&& this.idDomandaIntranet == other.getIdDomandaIntranet()
				&& ((this.richiedente == null && other.getRichiedente() == null)
						|| (this.richiedente != null && this.richiedente.equals(other.getRichiedente())))
				&& ((this.numeroProvvedimento == null && other.getNumeroProvvedimento() == null)
						|| (this.numeroProvvedimento != null
								&& this.numeroProvvedimento.equals(other.getNumeroProvvedimento())))
				&& ((this.dataProvvedimento == null && other.getDataProvvedimento() == null)
						|| (this.dataProvvedimento != null
								&& this.dataProvvedimento.equals(other.getDataProvvedimento())))
				&& ((this.esitoElaborazione == null && other.getEsitoElaborazione() == null)
						|| (this.esitoElaborazione != null
								&& this.esitoElaborazione.equals(other.getEsitoElaborazione())))
				&& ((this.codiceReiezione == null && other.getCodiceReiezione() == null)
						|| (this.codiceReiezione != null && this.codiceReiezione.equals(other.getCodiceReiezione())))
				&& ((this.dataCreazioneComunicazione == null && other.getDataCreazioneComunicazione() == null)
						|| (this.dataCreazioneComunicazione != null
								&& this.dataCreazioneComunicazione.equals(other.getDataCreazioneComunicazione())))
				&& ((this.identificativoComunicazione == null && other.getIdentificativoComunicazione() == null)
						|| (this.identificativoComunicazione != null
								&& this.identificativoComunicazione.equals(other.getIdentificativoComunicazione())))
				&& ((this.codiceOperatore == null && other.getCodiceOperatore() == null)
						|| (this.codiceOperatore != null && this.codiceOperatore.equals(other.getCodiceOperatore())))
				&& ((this.identificativoComunicazioneRichiesta == null
						&& other.getIdentificativoComunicazioneRichiesta() == null)
						|| (this.identificativoComunicazioneRichiesta != null
								&& this.identificativoComunicazioneRichiesta
										.equals(other.getIdentificativoComunicazioneRichiesta())));
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
		_hashCode += getIdDomandaWeb();
		_hashCode += getIdDomandaIntranet();
		if (getRichiedente() != null) {
			_hashCode += getRichiedente().hashCode();
		}
		if (getNumeroProvvedimento() != null) {
			_hashCode += getNumeroProvvedimento().hashCode();
		}
		if (getDataProvvedimento() != null) {
			_hashCode += getDataProvvedimento().hashCode();
		}
		if (getEsitoElaborazione() != null) {
			_hashCode += getEsitoElaborazione().hashCode();
		}
		if (getCodiceReiezione() != null) {
			_hashCode += getCodiceReiezione().hashCode();
		}
		if (getDataCreazioneComunicazione() != null) {
			_hashCode += getDataCreazioneComunicazione().hashCode();
		}
		if (getIdentificativoComunicazione() != null) {
			_hashCode += getIdentificativoComunicazione().hashCode();
		}
		if (getCodiceOperatore() != null) {
			_hashCode += getCodiceOperatore().hashCode();
		}
		if (getIdentificativoComunicazioneRichiesta() != null) {
			_hashCode += getIdentificativoComunicazioneRichiesta().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ValidazioneDomandaNraType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"validazioneDomandaNraType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idDomandaWeb");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1", "idDomandaWeb"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idDomandaIntranet");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"idDomandaIntranet"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("richiedente");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1", "richiedente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"datiRichiedenteType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("numeroProvvedimento");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"numeroProvvedimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataProvvedimento");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"dataProvvedimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esitoElaborazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"esitoElaborazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"esitoProvvedimentoType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceReiezione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"codiceReiezione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataCreazioneComunicazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"dataCreazioneComunicazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("identificativoComunicazione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"identificativoComunicazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceOperatore");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"codiceOperatore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("identificativoComunicazioneRichiesta");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"identificativoComunicazioneRichiesta"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
