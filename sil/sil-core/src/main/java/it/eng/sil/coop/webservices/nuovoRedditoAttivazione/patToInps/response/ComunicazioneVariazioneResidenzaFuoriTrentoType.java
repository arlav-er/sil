/**
 * ComunicazioneVariazioneResidenzaFuoriTrentoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response;

public class ComunicazioneVariazioneResidenzaFuoriTrentoType implements java.io.Serializable {
	private int idDomandaWeb;

	private int idDomandaIntranet;

	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente;

	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.IndirizzoType nuovoIndirizzo;

	private java.util.Calendar dataCreazioneComunicazione;

	private java.lang.String identificativoComunicazione;

	private java.lang.String codiceOperatore;

	public ComunicazioneVariazioneResidenzaFuoriTrentoType() {
	}

	public ComunicazioneVariazioneResidenzaFuoriTrentoType(int idDomandaWeb, int idDomandaIntranet,
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente,
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.IndirizzoType nuovoIndirizzo,
			java.util.Calendar dataCreazioneComunicazione, java.lang.String identificativoComunicazione,
			java.lang.String codiceOperatore) {
		this.idDomandaWeb = idDomandaWeb;
		this.idDomandaIntranet = idDomandaIntranet;
		this.richiedente = richiedente;
		this.nuovoIndirizzo = nuovoIndirizzo;
		this.dataCreazioneComunicazione = dataCreazioneComunicazione;
		this.identificativoComunicazione = identificativoComunicazione;
		this.codiceOperatore = codiceOperatore;
	}

	/**
	 * Gets the idDomandaWeb value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @return idDomandaWeb
	 */
	public int getIdDomandaWeb() {
		return idDomandaWeb;
	}

	/**
	 * Sets the idDomandaWeb value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @param idDomandaWeb
	 */
	public void setIdDomandaWeb(int idDomandaWeb) {
		this.idDomandaWeb = idDomandaWeb;
	}

	/**
	 * Gets the idDomandaIntranet value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @return idDomandaIntranet
	 */
	public int getIdDomandaIntranet() {
		return idDomandaIntranet;
	}

	/**
	 * Sets the idDomandaIntranet value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @param idDomandaIntranet
	 */
	public void setIdDomandaIntranet(int idDomandaIntranet) {
		this.idDomandaIntranet = idDomandaIntranet;
	}

	/**
	 * Gets the richiedente value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @return richiedente
	 */
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType getRichiedente() {
		return richiedente;
	}

	/**
	 * Sets the richiedente value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @param richiedente
	 */
	public void setRichiedente(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType richiedente) {
		this.richiedente = richiedente;
	}

	/**
	 * Gets the nuovoIndirizzo value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @return nuovoIndirizzo
	 */
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.IndirizzoType getNuovoIndirizzo() {
		return nuovoIndirizzo;
	}

	/**
	 * Sets the nuovoIndirizzo value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @param nuovoIndirizzo
	 */
	public void setNuovoIndirizzo(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.IndirizzoType nuovoIndirizzo) {
		this.nuovoIndirizzo = nuovoIndirizzo;
	}

	/**
	 * Gets the dataCreazioneComunicazione value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @return dataCreazioneComunicazione
	 */
	public java.util.Calendar getDataCreazioneComunicazione() {
		return dataCreazioneComunicazione;
	}

	/**
	 * Sets the dataCreazioneComunicazione value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @param dataCreazioneComunicazione
	 */
	public void setDataCreazioneComunicazione(java.util.Calendar dataCreazioneComunicazione) {
		this.dataCreazioneComunicazione = dataCreazioneComunicazione;
	}

	/**
	 * Gets the identificativoComunicazione value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @return identificativoComunicazione
	 */
	public java.lang.String getIdentificativoComunicazione() {
		return identificativoComunicazione;
	}

	/**
	 * Sets the identificativoComunicazione value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @param identificativoComunicazione
	 */
	public void setIdentificativoComunicazione(java.lang.String identificativoComunicazione) {
		this.identificativoComunicazione = identificativoComunicazione;
	}

	/**
	 * Gets the codiceOperatore value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @return codiceOperatore
	 */
	public java.lang.String getCodiceOperatore() {
		return codiceOperatore;
	}

	/**
	 * Sets the codiceOperatore value for this ComunicazioneVariazioneResidenzaFuoriTrentoType.
	 * 
	 * @param codiceOperatore
	 */
	public void setCodiceOperatore(java.lang.String codiceOperatore) {
		this.codiceOperatore = codiceOperatore;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ComunicazioneVariazioneResidenzaFuoriTrentoType))
			return false;
		ComunicazioneVariazioneResidenzaFuoriTrentoType other = (ComunicazioneVariazioneResidenzaFuoriTrentoType) obj;
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
				&& ((this.nuovoIndirizzo == null && other.getNuovoIndirizzo() == null)
						|| (this.nuovoIndirizzo != null && this.nuovoIndirizzo.equals(other.getNuovoIndirizzo())))
				&& ((this.dataCreazioneComunicazione == null && other.getDataCreazioneComunicazione() == null)
						|| (this.dataCreazioneComunicazione != null
								&& this.dataCreazioneComunicazione.equals(other.getDataCreazioneComunicazione())))
				&& ((this.identificativoComunicazione == null && other.getIdentificativoComunicazione() == null)
						|| (this.identificativoComunicazione != null
								&& this.identificativoComunicazione.equals(other.getIdentificativoComunicazione())))
				&& ((this.codiceOperatore == null && other.getCodiceOperatore() == null)
						|| (this.codiceOperatore != null && this.codiceOperatore.equals(other.getCodiceOperatore())));
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
		if (getNuovoIndirizzo() != null) {
			_hashCode += getNuovoIndirizzo().hashCode();
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
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ComunicazioneVariazioneResidenzaFuoriTrentoType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"comunicazioneVariazioneResidenzaFuoriTrentoType"));
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
		elemField.setFieldName("nuovoIndirizzo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"nuovoIndirizzo"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "indirizzoType"));
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
