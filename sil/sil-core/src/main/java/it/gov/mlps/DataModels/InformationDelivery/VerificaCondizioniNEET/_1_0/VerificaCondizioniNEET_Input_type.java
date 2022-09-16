/**
 * VerificaCondizioniNEET_Input_type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0;

public class VerificaCondizioniNEET_Input_type implements java.io.Serializable {
	private java.lang.String codiceFiscale;

	private java.util.Calendar dataRiferimento;

	private java.lang.String GUIDUtente;

	private java.lang.String codiceFiscaleOperatore;

	private java.lang.String applicazione;

	public VerificaCondizioniNEET_Input_type() {
	}

	public VerificaCondizioniNEET_Input_type(java.lang.String codiceFiscale, java.util.Calendar dataRiferimento,
			java.lang.String GUIDUtente, java.lang.String codiceFiscaleOperatore, java.lang.String applicazione) {
		this.codiceFiscale = codiceFiscale;
		this.dataRiferimento = dataRiferimento;
		this.GUIDUtente = GUIDUtente;
		this.codiceFiscaleOperatore = codiceFiscaleOperatore;
		this.applicazione = applicazione;
	}

	/**
	 * Gets the codiceFiscale value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @return codiceFiscale
	 */
	public java.lang.String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(java.lang.String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the dataRiferimento value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @return dataRiferimento
	 */
	public java.util.Calendar getDataRiferimento() {
		return dataRiferimento;
	}

	/**
	 * Sets the dataRiferimento value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @param dataRiferimento
	 */
	public void setDataRiferimento(java.util.Calendar dataRiferimento) {
		this.dataRiferimento = dataRiferimento;
	}

	/**
	 * Gets the GUIDUtente value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @return GUIDUtente
	 */
	public java.lang.String getGUIDUtente() {
		return GUIDUtente;
	}

	/**
	 * Sets the GUIDUtente value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @param GUIDUtente
	 */
	public void setGUIDUtente(java.lang.String GUIDUtente) {
		this.GUIDUtente = GUIDUtente;
	}

	/**
	 * Gets the codiceFiscaleOperatore value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @return codiceFiscaleOperatore
	 */
	public java.lang.String getCodiceFiscaleOperatore() {
		return codiceFiscaleOperatore;
	}

	/**
	 * Sets the codiceFiscaleOperatore value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @param codiceFiscaleOperatore
	 */
	public void setCodiceFiscaleOperatore(java.lang.String codiceFiscaleOperatore) {
		this.codiceFiscaleOperatore = codiceFiscaleOperatore;
	}

	/**
	 * Gets the applicazione value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @return applicazione
	 */
	public java.lang.String getApplicazione() {
		return applicazione;
	}

	/**
	 * Sets the applicazione value for this VerificaCondizioniNEET_Input_type.
	 * 
	 * @param applicazione
	 */
	public void setApplicazione(java.lang.String applicazione) {
		this.applicazione = applicazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof VerificaCondizioniNEET_Input_type))
			return false;
		VerificaCondizioniNEET_Input_type other = (VerificaCondizioniNEET_Input_type) obj;
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
				&& ((this.codiceFiscale == null && other.getCodiceFiscale() == null)
						|| (this.codiceFiscale != null && this.codiceFiscale.equals(other.getCodiceFiscale())))
				&& ((this.dataRiferimento == null && other.getDataRiferimento() == null)
						|| (this.dataRiferimento != null && this.dataRiferimento.equals(other.getDataRiferimento())))
				&& ((this.GUIDUtente == null && other.getGUIDUtente() == null)
						|| (this.GUIDUtente != null && this.GUIDUtente.equals(other.getGUIDUtente())))
				&& ((this.codiceFiscaleOperatore == null && other.getCodiceFiscaleOperatore() == null)
						|| (this.codiceFiscaleOperatore != null
								&& this.codiceFiscaleOperatore.equals(other.getCodiceFiscaleOperatore())))
				&& ((this.applicazione == null && other.getApplicazione() == null)
						|| (this.applicazione != null && this.applicazione.equals(other.getApplicazione())));
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
		if (getCodiceFiscale() != null) {
			_hashCode += getCodiceFiscale().hashCode();
		}
		if (getDataRiferimento() != null) {
			_hashCode += getDataRiferimento().hashCode();
		}
		if (getGUIDUtente() != null) {
			_hashCode += getGUIDUtente().hashCode();
		}
		if (getCodiceFiscaleOperatore() != null) {
			_hashCode += getCodiceFiscaleOperatore().hashCode();
		}
		if (getApplicazione() != null) {
			_hashCode += getApplicazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			VerificaCondizioniNEET_Input_type.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0",
				"VerificaCondizioniNEET_Input_type"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataRiferimento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "DataRiferimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("GUIDUtente");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "GUIDUtente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscaleOperatore");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0",
				"CodiceFiscaleOperatore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("applicazione");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "Applicazione"));
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
