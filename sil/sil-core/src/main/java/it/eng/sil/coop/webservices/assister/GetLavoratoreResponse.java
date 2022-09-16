/**
 * GetInfoLavoratoreResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.assister;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.util.Utils;

public class GetLavoratoreResponse implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private java.lang.String errore;

	private java.lang.String strCodiceFiscale;

	private java.lang.String strCognome;

	private java.lang.String strNome;

	private java.lang.String datNasc;

	private java.lang.String codComNas;

	private java.lang.String codCittadinanza;

	private java.lang.String strSesso;

	private java.lang.String codStatoCivile;

	private java.lang.String codComRes;

	private java.lang.String strLocalitaRes;

	private java.lang.String strIndirizzoRes;

	private java.lang.String codComDom;

	private java.lang.String strLocalitaDom;

	private java.lang.String strIndirizzoDom;

	private java.lang.String strNumDocumento;

	private java.lang.String datScadenza;

	private java.lang.String codPermesso;

	private java.lang.String codTitolo;

	public GetLavoratoreResponse() {
	}

	public GetLavoratoreResponse(String errore, String strCodiceFiscale) {
		this.errore = errore;
		this.strCodiceFiscale = strCodiceFiscale;
		this.strCognome = "";
		this.strNome = "";
		this.datNasc = "";
		this.codComNas = "";
		this.codCittadinanza = "";
		this.strSesso = "";
		this.codStatoCivile = "";
		this.codComRes = "";
		this.strLocalitaRes = "";
		this.strIndirizzoRes = "";
		this.codComDom = "";
		this.strLocalitaDom = "";
		this.strIndirizzoDom = "";
		this.strNumDocumento = "";
		this.datScadenza = "";
		this.codPermesso = null;
		this.codTitolo = "";
	}

	public GetLavoratoreResponse(String errore, SourceBean sbLavoratore) {
		this.errore = errore;
		this.strCodiceFiscale = Utils.notNull(sbLavoratore.getAttribute("strCodiceFiscale"));
		this.strCognome = Utils.notNull(sbLavoratore.getAttribute("strCognome"));
		this.strNome = Utils.notNull(sbLavoratore.getAttribute("strNome"));
		this.datNasc = Utils.notNull(sbLavoratore.getAttribute("datNasc"));
		this.codComNas = Utils.notNull(sbLavoratore.getAttribute("codComNas"));
		this.codCittadinanza = Utils.notNull(sbLavoratore.getAttribute("codCittadinanza"));
		this.strSesso = Utils.notNull(sbLavoratore.getAttribute("strSesso"));
		this.codStatoCivile = Utils.notNull(sbLavoratore.getAttribute("codStatoCivile"));
		this.codComRes = Utils.notNull(sbLavoratore.getAttribute("codComRes"));
		this.strLocalitaRes = Utils.notNull(sbLavoratore.getAttribute("strLocalitaRes"));
		this.strIndirizzoRes = Utils.notNull(sbLavoratore.getAttribute("strIndirizzoRes"));
		this.codComDom = Utils.notNull(sbLavoratore.getAttribute("codComDom"));
		this.strLocalitaDom = Utils.notNull(sbLavoratore.getAttribute("strLocalitaDom"));
		this.strIndirizzoDom = Utils.notNull(sbLavoratore.getAttribute("strIndirizzoDom"));
		this.strNumDocumento = Utils.notNull(sbLavoratore.getAttribute("strNumDocumento"));
		this.datScadenza = Utils.notNull(sbLavoratore.getAttribute("datScadenza"));
		this.codPermesso = Utils.notNull(sbLavoratore.getAttribute("codPermesso"));
		this.codTitolo = Utils.notNull(sbLavoratore.getAttribute("codTitolo"));
	}

	/**
	 * Gets the errore value for this GetInfoLavoratoreResponse.
	 *
	 * @return errore
	 */
	public java.lang.String getErrore() {
		return errore;
	}

	/**
	 * Sets the errore value for this GetInfoLavoratoreResponse.
	 *
	 * @param errore
	 */
	public void setErrore(java.lang.String errore) {
		this.errore = errore;
	}

	/**
	 * Gets the strCodiceFiscale value for this GetInfoLavoratoreResponse.
	 *
	 * @return strCodiceFiscale
	 */
	public java.lang.String getStrCodiceFiscale() {
		return strCodiceFiscale;
	}

	/**
	 * Sets the strCodiceFiscale value for this GetInfoLavoratoreResponse.
	 *
	 * @param strCodiceFiscale
	 */
	public void setStrCodiceFiscale(java.lang.String strCodiceFiscale) {
		this.strCodiceFiscale = strCodiceFiscale;
	}

	/**
	 * Gets the strCognome value for this GetInfoLavoratoreResponse.
	 *
	 * @return strCognome
	 */
	public java.lang.String getStrCognome() {
		return strCognome;
	}

	/**
	 * Sets the strCognome value for this GetInfoLavoratoreResponse.
	 *
	 * @param strCognome
	 */
	public void setStrCognome(java.lang.String strCognome) {
		this.strCognome = strCognome;
	}

	/**
	 * Gets the strNome value for this GetInfoLavoratoreResponse.
	 *
	 * @return strNome
	 */
	public java.lang.String getStrNome() {
		return strNome;
	}

	/**
	 * Sets the strNome value for this GetInfoLavoratoreResponse.
	 *
	 * @param strNome
	 */
	public void setStrNome(java.lang.String strNome) {
		this.strNome = strNome;
	}

	/**
	 * Gets the datNasc value for this GetInfoLavoratoreResponse.
	 *
	 * @return datNasc
	 */
	public java.lang.String getDatNasc() {
		return datNasc;
	}

	/**
	 * Sets the datNasc value for this GetInfoLavoratoreResponse.
	 *
	 * @param datNasc
	 */
	public void setDatNasc(java.lang.String datNasc) {
		this.datNasc = datNasc;
	}

	/**
	 * Gets the codComNas value for this GetInfoLavoratoreResponse.
	 *
	 * @return codComNas
	 */
	public java.lang.String getCodComNas() {
		return codComNas;
	}

	/**
	 * Sets the codComNas value for this GetInfoLavoratoreResponse.
	 *
	 * @param codComNas
	 */
	public void setCodComNas(java.lang.String codComNas) {
		this.codComNas = codComNas;
	}

	/**
	 * Gets the codCittadinanza value for this GetInfoLavoratoreResponse.
	 *
	 * @return codCittadinanza
	 */
	public java.lang.String getCodCittadinanza() {
		return codCittadinanza;
	}

	/**
	 * Sets the codCittadinanza value for this GetInfoLavoratoreResponse.
	 *
	 * @param codCittadinanza
	 */
	public void setCodCittadinanza(java.lang.String codCittadinanza) {
		this.codCittadinanza = codCittadinanza;
	}

	/**
	 * Gets the strSesso value for this GetInfoLavoratoreResponse.
	 *
	 * @return strSesso
	 */
	public java.lang.String getStrSesso() {
		return strSesso;
	}

	/**
	 * Sets the strSesso value for this GetInfoLavoratoreResponse.
	 *
	 * @param strSesso
	 */
	public void setStrSesso(java.lang.String strSesso) {
		this.strSesso = strSesso;
	}

	/**
	 * Gets the codStatoCivile value for this GetInfoLavoratoreResponse.
	 *
	 * @return codStatoCivile
	 */
	public java.lang.String getCodStatoCivile() {
		return codStatoCivile;
	}

	/**
	 * Sets the codStatoCivile value for this GetInfoLavoratoreResponse.
	 *
	 * @param codStatoCivile
	 */
	public void setCodStatoCivile(java.lang.String codStatoCivile) {
		this.codStatoCivile = codStatoCivile;
	}

	/**
	 * Gets the codComRes value for this GetInfoLavoratoreResponse.
	 *
	 * @return codComRes
	 */
	public java.lang.String getCodComRes() {
		return codComRes;
	}

	/**
	 * Sets the codComRes value for this GetInfoLavoratoreResponse.
	 *
	 * @param codComRes
	 */
	public void setCodComRes(java.lang.String codComRes) {
		this.codComRes = codComRes;
	}

	/**
	 * Gets the strLocalitaRes value for this GetInfoLavoratoreResponse.
	 *
	 * @return strLocalitaRes
	 */
	public java.lang.String getStrLocalitaRes() {
		return strLocalitaRes;
	}

	/**
	 * Sets the strLocalitaRes value for this GetInfoLavoratoreResponse.
	 *
	 * @param strLocalitaRes
	 */
	public void setStrLocalitaRes(java.lang.String strLocalitaRes) {
		this.strLocalitaRes = strLocalitaRes;
	}

	/**
	 * Gets the strIndirizzoRes value for this GetInfoLavoratoreResponse.
	 *
	 * @return strIndirizzoRes
	 */
	public java.lang.String getStrIndirizzoRes() {
		return strIndirizzoRes;
	}

	/**
	 * Sets the strIndirizzoRes value for this GetInfoLavoratoreResponse.
	 *
	 * @param strIndirizzoRes
	 */
	public void setStrIndirizzoRes(java.lang.String strIndirizzoRes) {
		this.strIndirizzoRes = strIndirizzoRes;
	}

	/**
	 * Gets the codComDom value for this GetInfoLavoratoreResponse.
	 *
	 * @return codComDom
	 */
	public java.lang.String getCodComDom() {
		return codComDom;
	}

	/**
	 * Sets the codComDom value for this GetInfoLavoratoreResponse.
	 *
	 * @param codComDom
	 */
	public void setCodComDom(java.lang.String codComDom) {
		this.codComDom = codComDom;
	}

	/**
	 * Gets the strLocalitaDom value for this GetInfoLavoratoreResponse.
	 *
	 * @return strLocalitaDom
	 */
	public java.lang.String getStrLocalitaDom() {
		return strLocalitaDom;
	}

	/**
	 * Sets the strLocalitaDom value for this GetInfoLavoratoreResponse.
	 *
	 * @param strLocalitaDom
	 */
	public void setStrLocalitaDom(java.lang.String strLocalitaDom) {
		this.strLocalitaDom = strLocalitaDom;
	}

	/**
	 * Gets the strIndirizzoDom value for this GetInfoLavoratoreResponse.
	 *
	 * @return strIndirizzoDom
	 */
	public java.lang.String getStrIndirizzoDom() {
		return strIndirizzoDom;
	}

	/**
	 * Sets the strIndirizzoDom value for this GetInfoLavoratoreResponse.
	 *
	 * @param strIndirizzoDom
	 */
	public void setStrIndirizzoDom(java.lang.String strIndirizzoDom) {
		this.strIndirizzoDom = strIndirizzoDom;
	}

	/**
	 * Gets the strNumDocumento value for this GetInfoLavoratoreResponse.
	 *
	 * @return strNumDocumento
	 */
	public java.lang.String getStrNumDocumento() {
		return strNumDocumento;
	}

	/**
	 * Sets the strNumDocumento value for this GetInfoLavoratoreResponse.
	 *
	 * @param strNumDocumento
	 */
	public void setStrNumDocumento(java.lang.String strNumDocumento) {
		this.strNumDocumento = strNumDocumento;
	}

	/**
	 * Gets the datScadenza value for this GetInfoLavoratoreResponse.
	 *
	 * @return datScadenza
	 */
	public java.lang.String getDatScadenza() {
		return datScadenza;
	}

	/**
	 * Sets the datScadenza value for this GetInfoLavoratoreResponse.
	 *
	 * @param datScadenza
	 */
	public void setDatScadenza(java.lang.String datScadenza) {
		this.datScadenza = datScadenza;
	}

	/**
	 * Gets the codPermesso value for this GetInfoLavoratoreResponse.
	 *
	 * @return codPermesso
	 */
	public java.lang.String getCodPermesso() {
		return codPermesso;
	}

	/**
	 * Sets the codPermesso value for this GetInfoLavoratoreResponse.
	 *
	 * @param codPermesso
	 */
	public void setCodPermesso(java.lang.String codPermesso) {
		this.codPermesso = codPermesso;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GetLavoratoreResponse))
			return false;
		GetLavoratoreResponse other = (GetLavoratoreResponse) obj;
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
				&& ((this.errore == null && other.getErrore() == null)
						|| (this.errore != null && this.errore.equals(other.getErrore())))
				&& ((this.strCodiceFiscale == null && other.getStrCodiceFiscale() == null)
						|| (this.strCodiceFiscale != null && this.strCodiceFiscale.equals(other.getStrCodiceFiscale())))
				&& ((this.strCognome == null && other.getStrCognome() == null)
						|| (this.strCognome != null && this.strCognome.equals(other.getStrCognome())))
				&& ((this.strNome == null && other.getStrNome() == null)
						|| (this.strNome != null && this.strNome.equals(other.getStrNome())))
				&& ((this.datNasc == null && other.getDatNasc() == null)
						|| (this.datNasc != null && this.datNasc.equals(other.getDatNasc())))
				&& ((this.codComNas == null && other.getCodComNas() == null)
						|| (this.codComNas != null && this.codComNas.equals(other.getCodComNas())))
				&& ((this.codCittadinanza == null && other.getCodCittadinanza() == null)
						|| (this.codCittadinanza != null && this.codCittadinanza.equals(other.getCodCittadinanza())))
				&& ((this.strSesso == null && other.getStrSesso() == null)
						|| (this.strSesso != null && this.strSesso.equals(other.getStrSesso())))
				&& ((this.codStatoCivile == null && other.getCodStatoCivile() == null)
						|| (this.codStatoCivile != null && this.codStatoCivile.equals(other.getCodStatoCivile())))
				&& ((this.codComRes == null && other.getCodComRes() == null)
						|| (this.codComRes != null && this.codComRes.equals(other.getCodComRes())))
				&& ((this.strLocalitaRes == null && other.getStrLocalitaRes() == null)
						|| (this.strLocalitaRes != null && this.strLocalitaRes.equals(other.getStrLocalitaRes())))
				&& ((this.strIndirizzoRes == null && other.getStrIndirizzoRes() == null)
						|| (this.strIndirizzoRes != null && this.strIndirizzoRes.equals(other.getStrIndirizzoRes())))
				&& ((this.codComDom == null && other.getCodComDom() == null)
						|| (this.codComDom != null && this.codComDom.equals(other.getCodComDom())))
				&& ((this.strLocalitaDom == null && other.getStrLocalitaDom() == null)
						|| (this.strLocalitaDom != null && this.strLocalitaDom.equals(other.getStrLocalitaDom())))
				&& ((this.strIndirizzoDom == null && other.getStrIndirizzoDom() == null)
						|| (this.strIndirizzoDom != null && this.strIndirizzoDom.equals(other.getStrIndirizzoDom())))
				&& ((this.strNumDocumento == null && other.getStrNumDocumento() == null)
						|| (this.strNumDocumento != null && this.strNumDocumento.equals(other.getStrNumDocumento())))
				&& ((this.datScadenza == null && other.getDatScadenza() == null)
						|| (this.datScadenza != null && this.datScadenza.equals(other.getDatScadenza())))
				&& ((this.codPermesso == null && other.getCodPermesso() == null)
						|| (this.codPermesso != null && this.codPermesso.equals(other.getCodPermesso())));
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
		if (getErrore() != null) {
			_hashCode += getErrore().hashCode();
		}
		if (getStrCodiceFiscale() != null) {
			_hashCode += getStrCodiceFiscale().hashCode();
		}
		if (getStrCognome() != null) {
			_hashCode += getStrCognome().hashCode();
		}
		if (getStrNome() != null) {
			_hashCode += getStrNome().hashCode();
		}
		if (getDatNasc() != null) {
			_hashCode += getDatNasc().hashCode();
		}
		if (getCodComNas() != null) {
			_hashCode += getCodComNas().hashCode();
		}
		if (getCodCittadinanza() != null) {
			_hashCode += getCodCittadinanza().hashCode();
		}
		if (getStrSesso() != null) {
			_hashCode += getStrSesso().hashCode();
		}
		if (getCodStatoCivile() != null) {
			_hashCode += getCodStatoCivile().hashCode();
		}
		if (getCodComRes() != null) {
			_hashCode += getCodComRes().hashCode();
		}
		if (getStrLocalitaRes() != null) {
			_hashCode += getStrLocalitaRes().hashCode();
		}
		if (getStrIndirizzoRes() != null) {
			_hashCode += getStrIndirizzoRes().hashCode();
		}
		if (getCodComDom() != null) {
			_hashCode += getCodComDom().hashCode();
		}
		if (getStrLocalitaDom() != null) {
			_hashCode += getStrLocalitaDom().hashCode();
		}
		if (getStrIndirizzoDom() != null) {
			_hashCode += getStrIndirizzoDom().hashCode();
		}
		if (getStrNumDocumento() != null) {
			_hashCode += getStrNumDocumento().hashCode();
		}
		if (getDatScadenza() != null) {
			_hashCode += getDatScadenza().hashCode();
		}
		if (getCodPermesso() != null) {
			_hashCode += getCodPermesso().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	public java.lang.String getCodTitolo() {
		return codTitolo;
	}

	public void setCodTitolo(java.lang.String codTitolo) {
		this.codTitolo = codTitolo;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			GetLavoratoreResponse.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it",
				"getInfoLavoratoreResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("errore");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "errore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strCodiceFiscale");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strCodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strCognome");
		elemField
				.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strCognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strNome");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strNome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datNasc");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "datNasc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComNas");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "codComNas"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codCittadinanza");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "codCittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strSesso");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strSesso"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codStatoCivile");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "codStatoCivile"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComRes");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "codComRes"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strLocalitaRes");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strLocalitaRes"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strIndirizzoRes");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strIndirizzoRes"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComDom");
		elemField.setXmlName(new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "codComDom"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strLocalitaDom");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strLocalitaDom"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strIndirizzoDom");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strIndirizzoDom"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strNumDocumento");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "strNumDocumento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datScadenza");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "datScadenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPermesso");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://assister.webservices.coop.sil.eng.it", "codPermesso"));
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
