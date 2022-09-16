/**
 * ComunicazioniSuccessiveNraType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response;

public class ComunicazioniSuccessiveNraType implements java.io.Serializable {
	private int idDomandaWeb;

	private int idDomandaIntranet;

	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente;

	private java.lang.String numeroProvvedimento;

	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.TipologiaEventoType tipoProvvedimento;

	private java.util.Calendar dataProvvedimento;

	private java.util.Calendar dataCreazioneComunicazione;

	private java.lang.String identificativoComunicazione;

	private java.lang.String codiceOperatore;

	private java.lang.String identificativoComunicazioneRichiesta;

	public ComunicazioniSuccessiveNraType() {
	}

	public ComunicazioniSuccessiveNraType(int idDomandaWeb, int idDomandaIntranet,
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente,
			java.lang.String numeroProvvedimento,
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.TipologiaEventoType tipoProvvedimento,
			java.util.Calendar dataProvvedimento, java.util.Calendar dataCreazioneComunicazione,
			java.lang.String identificativoComunicazione, java.lang.String codiceOperatore,
			java.lang.String identificativoComunicazioneRichiesta) {
		this.idDomandaWeb = idDomandaWeb;
		this.idDomandaIntranet = idDomandaIntranet;
		this.richiedente = richiedente;
		this.numeroProvvedimento = numeroProvvedimento;
		this.tipoProvvedimento = tipoProvvedimento;
		this.dataProvvedimento = dataProvvedimento;
		this.dataCreazioneComunicazione = dataCreazioneComunicazione;
		this.identificativoComunicazione = identificativoComunicazione;
		this.codiceOperatore = codiceOperatore;
		this.identificativoComunicazioneRichiesta = identificativoComunicazioneRichiesta;
	}

	/**
	 * Gets the idDomandaWeb value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return idDomandaWeb
	 */
	public int getIdDomandaWeb() {
		return idDomandaWeb;
	}

	/**
	 * Sets the idDomandaWeb value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param idDomandaWeb
	 */
	public void setIdDomandaWeb(int idDomandaWeb) {
		this.idDomandaWeb = idDomandaWeb;
	}

	/**
	 * Gets the idDomandaIntranet value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return idDomandaIntranet
	 */
	public int getIdDomandaIntranet() {
		return idDomandaIntranet;
	}

	/**
	 * Sets the idDomandaIntranet value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param idDomandaIntranet
	 */
	public void setIdDomandaIntranet(int idDomandaIntranet) {
		this.idDomandaIntranet = idDomandaIntranet;
	}

	/**
	 * Gets the richiedente value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return richiedente
	 */
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType getRichiedente() {
		return richiedente;
	}

	/**
	 * Sets the richiedente value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param richiedente
	 */
	public void setRichiedente(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente) {
		this.richiedente = richiedente;
	}

	/**
	 * Gets the numeroProvvedimento value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return numeroProvvedimento
	 */
	public java.lang.String getNumeroProvvedimento() {
		return numeroProvvedimento;
	}

	/**
	 * Sets the numeroProvvedimento value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param numeroProvvedimento
	 */
	public void setNumeroProvvedimento(java.lang.String numeroProvvedimento) {
		this.numeroProvvedimento = numeroProvvedimento;
	}

	/**
	 * Gets the tipoProvvedimento value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return tipoProvvedimento
	 */
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.TipologiaEventoType getTipoProvvedimento() {
		return tipoProvvedimento;
	}

	/**
	 * Sets the tipoProvvedimento value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param tipoProvvedimento
	 */
	public void setTipoProvvedimento(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.TipologiaEventoType tipoProvvedimento) {
		this.tipoProvvedimento = tipoProvvedimento;
	}

	/**
	 * Gets the dataProvvedimento value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return dataProvvedimento
	 */
	public java.util.Calendar getDataProvvedimento() {
		return dataProvvedimento;
	}

	/**
	 * Sets the dataProvvedimento value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param dataProvvedimento
	 */
	public void setDataProvvedimento(java.util.Calendar dataProvvedimento) {
		this.dataProvvedimento = dataProvvedimento;
	}

	/**
	 * Gets the dataCreazioneComunicazione value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return dataCreazioneComunicazione
	 */
	public java.util.Calendar getDataCreazioneComunicazione() {
		return dataCreazioneComunicazione;
	}

	/**
	 * Sets the dataCreazioneComunicazione value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param dataCreazioneComunicazione
	 */
	public void setDataCreazioneComunicazione(java.util.Calendar dataCreazioneComunicazione) {
		this.dataCreazioneComunicazione = dataCreazioneComunicazione;
	}

	/**
	 * Gets the identificativoComunicazione value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return identificativoComunicazione
	 */
	public java.lang.String getIdentificativoComunicazione() {
		return identificativoComunicazione;
	}

	/**
	 * Sets the identificativoComunicazione value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param identificativoComunicazione
	 */
	public void setIdentificativoComunicazione(java.lang.String identificativoComunicazione) {
		this.identificativoComunicazione = identificativoComunicazione;
	}

	/**
	 * Gets the codiceOperatore value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return codiceOperatore
	 */
	public java.lang.String getCodiceOperatore() {
		return codiceOperatore;
	}

	/**
	 * Sets the codiceOperatore value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param codiceOperatore
	 */
	public void setCodiceOperatore(java.lang.String codiceOperatore) {
		this.codiceOperatore = codiceOperatore;
	}

	/**
	 * Gets the identificativoComunicazioneRichiesta value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @return identificativoComunicazioneRichiesta
	 */
	public java.lang.String getIdentificativoComunicazioneRichiesta() {
		return identificativoComunicazioneRichiesta;
	}

	/**
	 * Sets the identificativoComunicazioneRichiesta value for this ComunicazioniSuccessiveNraType.
	 * 
	 * @param identificativoComunicazioneRichiesta
	 */
	public void setIdentificativoComunicazioneRichiesta(java.lang.String identificativoComunicazioneRichiesta) {
		this.identificativoComunicazioneRichiesta = identificativoComunicazioneRichiesta;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ComunicazioniSuccessiveNraType))
			return false;
		ComunicazioniSuccessiveNraType other = (ComunicazioniSuccessiveNraType) obj;
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
				&& ((this.tipoProvvedimento == null && other.getTipoProvvedimento() == null)
						|| (this.tipoProvvedimento != null
								&& this.tipoProvvedimento.equals(other.getTipoProvvedimento())))
				&& ((this.dataProvvedimento == null && other.getDataProvvedimento() == null)
						|| (this.dataProvvedimento != null
								&& this.dataProvvedimento.equals(other.getDataProvvedimento())))
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
		if (getTipoProvvedimento() != null) {
			_hashCode += getTipoProvvedimento().hashCode();
		}
		if (getDataProvvedimento() != null) {
			_hashCode += getDataProvvedimento().hashCode();
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
			ComunicazioniSuccessiveNraType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"comunicazioniSuccessiveNraType"));
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
		elemField.setFieldName("tipoProvvedimento");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"tipoProvvedimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"tipologiaEventoType"));
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
