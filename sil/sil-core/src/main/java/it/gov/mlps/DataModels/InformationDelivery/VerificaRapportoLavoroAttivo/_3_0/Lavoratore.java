/**
 * Lavoratore.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0;

public class Lavoratore implements java.io.Serializable {
	private java.lang.String codiceFiscaleLavoratore;

	private java.lang.String cognome;

	private java.lang.String nome;

	private java.lang.String genere;

	private java.lang.String codiceCittadinanza;

	private java.lang.String cittadinanza;

	private java.util.Calendar dataNascita;

	private java.lang.String codiceComuneNascita;

	private java.lang.String comuneNascita;

	public Lavoratore() {
	}

	public Lavoratore(java.lang.String codiceFiscaleLavoratore, java.lang.String cognome, java.lang.String nome,
			java.lang.String genere, java.lang.String codiceCittadinanza, java.lang.String cittadinanza,
			java.util.Calendar dataNascita, java.lang.String codiceComuneNascita, java.lang.String comuneNascita) {
		this.codiceFiscaleLavoratore = codiceFiscaleLavoratore;
		this.cognome = cognome;
		this.nome = nome;
		this.genere = genere;
		this.codiceCittadinanza = codiceCittadinanza;
		this.cittadinanza = cittadinanza;
		this.dataNascita = dataNascita;
		this.codiceComuneNascita = codiceComuneNascita;
		this.comuneNascita = comuneNascita;
	}

	/**
	 * Gets the codiceFiscaleLavoratore value for this Lavoratore.
	 * 
	 * @return codiceFiscaleLavoratore
	 */
	public java.lang.String getCodiceFiscaleLavoratore() {
		return codiceFiscaleLavoratore;
	}

	/**
	 * Sets the codiceFiscaleLavoratore value for this Lavoratore.
	 * 
	 * @param codiceFiscaleLavoratore
	 */
	public void setCodiceFiscaleLavoratore(java.lang.String codiceFiscaleLavoratore) {
		this.codiceFiscaleLavoratore = codiceFiscaleLavoratore;
	}

	/**
	 * Gets the cognome value for this Lavoratore.
	 * 
	 * @return cognome
	 */
	public java.lang.String getCognome() {
		return cognome;
	}

	/**
	 * Sets the cognome value for this Lavoratore.
	 * 
	 * @param cognome
	 */
	public void setCognome(java.lang.String cognome) {
		this.cognome = cognome;
	}

	/**
	 * Gets the nome value for this Lavoratore.
	 * 
	 * @return nome
	 */
	public java.lang.String getNome() {
		return nome;
	}

	/**
	 * Sets the nome value for this Lavoratore.
	 * 
	 * @param nome
	 */
	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	/**
	 * Gets the genere value for this Lavoratore.
	 * 
	 * @return genere
	 */
	public java.lang.String getGenere() {
		return genere;
	}

	/**
	 * Sets the genere value for this Lavoratore.
	 * 
	 * @param genere
	 */
	public void setGenere(java.lang.String genere) {
		this.genere = genere;
	}

	/**
	 * Gets the codiceCittadinanza value for this Lavoratore.
	 * 
	 * @return codiceCittadinanza
	 */
	public java.lang.String getCodiceCittadinanza() {
		return codiceCittadinanza;
	}

	/**
	 * Sets the codiceCittadinanza value for this Lavoratore.
	 * 
	 * @param codiceCittadinanza
	 */
	public void setCodiceCittadinanza(java.lang.String codiceCittadinanza) {
		this.codiceCittadinanza = codiceCittadinanza;
	}

	/**
	 * Gets the cittadinanza value for this Lavoratore.
	 * 
	 * @return cittadinanza
	 */
	public java.lang.String getCittadinanza() {
		return cittadinanza;
	}

	/**
	 * Sets the cittadinanza value for this Lavoratore.
	 * 
	 * @param cittadinanza
	 */
	public void setCittadinanza(java.lang.String cittadinanza) {
		this.cittadinanza = cittadinanza;
	}

	/**
	 * Gets the dataNascita value for this Lavoratore.
	 * 
	 * @return dataNascita
	 */
	public java.util.Calendar getDataNascita() {
		return dataNascita;
	}

	/**
	 * Sets the dataNascita value for this Lavoratore.
	 * 
	 * @param dataNascita
	 */
	public void setDataNascita(java.util.Calendar dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * Gets the codiceComuneNascita value for this Lavoratore.
	 * 
	 * @return codiceComuneNascita
	 */
	public java.lang.String getCodiceComuneNascita() {
		return codiceComuneNascita;
	}

	/**
	 * Sets the codiceComuneNascita value for this Lavoratore.
	 * 
	 * @param codiceComuneNascita
	 */
	public void setCodiceComuneNascita(java.lang.String codiceComuneNascita) {
		this.codiceComuneNascita = codiceComuneNascita;
	}

	/**
	 * Gets the comuneNascita value for this Lavoratore.
	 * 
	 * @return comuneNascita
	 */
	public java.lang.String getComuneNascita() {
		return comuneNascita;
	}

	/**
	 * Sets the comuneNascita value for this Lavoratore.
	 * 
	 * @param comuneNascita
	 */
	public void setComuneNascita(java.lang.String comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Lavoratore))
			return false;
		Lavoratore other = (Lavoratore) obj;
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
				&& ((this.codiceFiscaleLavoratore == null && other.getCodiceFiscaleLavoratore() == null)
						|| (this.codiceFiscaleLavoratore != null
								&& this.codiceFiscaleLavoratore.equals(other.getCodiceFiscaleLavoratore())))
				&& ((this.cognome == null && other.getCognome() == null)
						|| (this.cognome != null && this.cognome.equals(other.getCognome())))
				&& ((this.nome == null && other.getNome() == null)
						|| (this.nome != null && this.nome.equals(other.getNome())))
				&& ((this.genere == null && other.getGenere() == null)
						|| (this.genere != null && this.genere.equals(other.getGenere())))
				&& ((this.codiceCittadinanza == null && other.getCodiceCittadinanza() == null)
						|| (this.codiceCittadinanza != null
								&& this.codiceCittadinanza.equals(other.getCodiceCittadinanza())))
				&& ((this.cittadinanza == null && other.getCittadinanza() == null)
						|| (this.cittadinanza != null && this.cittadinanza.equals(other.getCittadinanza())))
				&& ((this.dataNascita == null && other.getDataNascita() == null)
						|| (this.dataNascita != null && this.dataNascita.equals(other.getDataNascita())))
				&& ((this.codiceComuneNascita == null && other.getCodiceComuneNascita() == null)
						|| (this.codiceComuneNascita != null
								&& this.codiceComuneNascita.equals(other.getCodiceComuneNascita())))
				&& ((this.comuneNascita == null && other.getComuneNascita() == null)
						|| (this.comuneNascita != null && this.comuneNascita.equals(other.getComuneNascita())));
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
		if (getCodiceFiscaleLavoratore() != null) {
			_hashCode += getCodiceFiscaleLavoratore().hashCode();
		}
		if (getCognome() != null) {
			_hashCode += getCognome().hashCode();
		}
		if (getNome() != null) {
			_hashCode += getNome().hashCode();
		}
		if (getGenere() != null) {
			_hashCode += getGenere().hashCode();
		}
		if (getCodiceCittadinanza() != null) {
			_hashCode += getCodiceCittadinanza().hashCode();
		}
		if (getCittadinanza() != null) {
			_hashCode += getCittadinanza().hashCode();
		}
		if (getDataNascita() != null) {
			_hashCode += getDataNascita().hashCode();
		}
		if (getCodiceComuneNascita() != null) {
			_hashCode += getCodiceComuneNascita().hashCode();
		}
		if (getComuneNascita() != null) {
			_hashCode += getComuneNascita().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Lavoratore.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Lavoratore"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscaleLavoratore");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"CodiceFiscaleLavoratore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("genere");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Genere"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceCittadinanza");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"CodiceCittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cittadinanza");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Cittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataNascita");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "DataNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceComuneNascita");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"CodiceComuneNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("comuneNascita");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "ComuneNascita"));
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
