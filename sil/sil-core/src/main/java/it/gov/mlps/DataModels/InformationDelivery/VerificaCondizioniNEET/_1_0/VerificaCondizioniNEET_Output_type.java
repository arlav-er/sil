/**
 * VerificaCondizioniNEET_Output_type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0;

public class VerificaCondizioniNEET_Output_type implements java.io.Serializable {
	private java.lang.String codiceFiscale;

	private java.util.Calendar dataRiferimento;

	private it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.CondizioneNEET_type[] condizioniNEET;

	private java.lang.String esito;

	public VerificaCondizioniNEET_Output_type() {
	}

	public VerificaCondizioniNEET_Output_type(java.lang.String codiceFiscale, java.util.Calendar dataRiferimento,
			it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.CondizioneNEET_type[] condizioniNEET,
			java.lang.String esito) {
		this.codiceFiscale = codiceFiscale;
		this.dataRiferimento = dataRiferimento;
		this.condizioniNEET = condizioniNEET;
		this.esito = esito;
	}

	/**
	 * Gets the codiceFiscale value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @return codiceFiscale
	 */
	public java.lang.String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(java.lang.String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the dataRiferimento value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @return dataRiferimento
	 */
	public java.util.Calendar getDataRiferimento() {
		return dataRiferimento;
	}

	/**
	 * Sets the dataRiferimento value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @param dataRiferimento
	 */
	public void setDataRiferimento(java.util.Calendar dataRiferimento) {
		this.dataRiferimento = dataRiferimento;
	}

	/**
	 * Gets the condizioniNEET value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @return condizioniNEET
	 */
	public it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.CondizioneNEET_type[] getCondizioniNEET() {
		return condizioniNEET;
	}

	/**
	 * Sets the condizioniNEET value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @param condizioniNEET
	 */
	public void setCondizioniNEET(
			it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.CondizioneNEET_type[] condizioniNEET) {
		this.condizioniNEET = condizioniNEET;
	}

	/**
	 * Gets the esito value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @return esito
	 */
	public java.lang.String getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this VerificaCondizioniNEET_Output_type.
	 * 
	 * @param esito
	 */
	public void setEsito(java.lang.String esito) {
		this.esito = esito;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof VerificaCondizioniNEET_Output_type))
			return false;
		VerificaCondizioniNEET_Output_type other = (VerificaCondizioniNEET_Output_type) obj;
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
				&& ((this.condizioniNEET == null && other.getCondizioniNEET() == null) || (this.condizioniNEET != null
						&& java.util.Arrays.equals(this.condizioniNEET, other.getCondizioniNEET())))
				&& ((this.esito == null && other.getEsito() == null)
						|| (this.esito != null && this.esito.equals(other.getEsito())));
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
		if (getCondizioniNEET() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getCondizioniNEET()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getCondizioniNEET(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getEsito() != null) {
			_hashCode += getEsito().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			VerificaCondizioniNEET_Output_type.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0",
				"VerificaCondizioniNEET_Output_type"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataRiferimento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "DataRiferimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("condizioniNEET");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "CondizioniNEET"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "CondizioneNEET_type"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		elemField.setItemQName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "CondizioneNEET"));
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "Esito"));
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
