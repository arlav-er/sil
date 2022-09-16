/**
 * DatiRichiedenteType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common;

public class DatiRichiedenteType implements java.io.Serializable {
	private java.lang.String nome;

	private java.lang.String cognome;

	private java.lang.String codiceFiscale;

	private java.util.Date dataNascita;

	private java.lang.String codiceCatastoNascita;

	public DatiRichiedenteType() {
	}

	public DatiRichiedenteType(java.lang.String nome, java.lang.String cognome, java.lang.String codiceFiscale,
			java.util.Date dataNascita, java.lang.String codiceCatastoNascita) {
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.dataNascita = dataNascita;
		this.codiceCatastoNascita = codiceCatastoNascita;
	}

	/**
	 * Gets the nome value for this DatiRichiedenteType.
	 * 
	 * @return nome
	 */
	public java.lang.String getNome() {
		return nome;
	}

	/**
	 * Sets the nome value for this DatiRichiedenteType.
	 * 
	 * @param nome
	 */
	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	/**
	 * Gets the cognome value for this DatiRichiedenteType.
	 * 
	 * @return cognome
	 */
	public java.lang.String getCognome() {
		return cognome;
	}

	/**
	 * Sets the cognome value for this DatiRichiedenteType.
	 * 
	 * @param cognome
	 */
	public void setCognome(java.lang.String cognome) {
		this.cognome = cognome;
	}

	/**
	 * Gets the codiceFiscale value for this DatiRichiedenteType.
	 * 
	 * @return codiceFiscale
	 */
	public java.lang.String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this DatiRichiedenteType.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(java.lang.String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the dataNascita value for this DatiRichiedenteType.
	 * 
	 * @return dataNascita
	 */
	public java.util.Date getDataNascita() {
		return dataNascita;
	}

	/**
	 * Sets the dataNascita value for this DatiRichiedenteType.
	 * 
	 * @param dataNascita
	 */
	public void setDataNascita(java.util.Date dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * Gets the codiceCatastoNascita value for this DatiRichiedenteType.
	 * 
	 * @return codiceCatastoNascita
	 */
	public java.lang.String getCodiceCatastoNascita() {
		return codiceCatastoNascita;
	}

	/**
	 * Sets the codiceCatastoNascita value for this DatiRichiedenteType.
	 * 
	 * @param codiceCatastoNascita
	 */
	public void setCodiceCatastoNascita(java.lang.String codiceCatastoNascita) {
		this.codiceCatastoNascita = codiceCatastoNascita;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DatiRichiedenteType))
			return false;
		DatiRichiedenteType other = (DatiRichiedenteType) obj;
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
				&& ((this.nome == null && other.getNome() == null)
						|| (this.nome != null && this.nome.equals(other.getNome())))
				&& ((this.cognome == null && other.getCognome() == null)
						|| (this.cognome != null && this.cognome.equals(other.getCognome())))
				&& ((this.codiceFiscale == null && other.getCodiceFiscale() == null)
						|| (this.codiceFiscale != null && this.codiceFiscale.equals(other.getCodiceFiscale())))
				&& ((this.dataNascita == null && other.getDataNascita() == null)
						|| (this.dataNascita != null && this.dataNascita.equals(other.getDataNascita())))
				&& ((this.codiceCatastoNascita == null && other.getCodiceCatastoNascita() == null)
						|| (this.codiceCatastoNascita != null
								&& this.codiceCatastoNascita.equals(other.getCodiceCatastoNascita())));
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
		if (getNome() != null) {
			_hashCode += getNome().hashCode();
		}
		if (getCognome() != null) {
			_hashCode += getCognome().hashCode();
		}
		if (getCodiceFiscale() != null) {
			_hashCode += getCodiceFiscale().hashCode();
		}
		if (getDataNascita() != null) {
			_hashCode += getDataNascita().hashCode();
		}
		if (getCodiceCatastoNascita() != null) {
			_hashCode += getCodiceCatastoNascita().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DatiRichiedenteType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"datiRichiedenteType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "codiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataNascita");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "dataNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceCatastoNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"codiceCatastoNascita"));
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
