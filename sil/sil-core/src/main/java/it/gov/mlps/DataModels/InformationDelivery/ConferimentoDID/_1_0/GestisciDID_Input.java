/**
 * GestisciDID_Input.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0;

public class GestisciDID_Input implements java.io.Serializable {
	private it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling variabiliDiProfiling;

	private it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID informazioniDID;

	private java.lang.String GUIDOperatore;

	private java.lang.String codiceFiscaleOperatore;

	private it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Applicazione applicazione;

	public GestisciDID_Input() {
	}

	public GestisciDID_Input(
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling variabiliDiProfiling,
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID informazioniDID,
			java.lang.String GUIDOperatore, java.lang.String codiceFiscaleOperatore,
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Applicazione applicazione) {
		this.variabiliDiProfiling = variabiliDiProfiling;
		this.informazioniDID = informazioniDID;
		this.GUIDOperatore = GUIDOperatore;
		this.codiceFiscaleOperatore = codiceFiscaleOperatore;
		this.applicazione = applicazione;
	}

	/**
	 * Gets the variabiliDiProfiling value for this GestisciDID_Input.
	 * 
	 * @return variabiliDiProfiling
	 */
	public it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling getVariabiliDiProfiling() {
		return variabiliDiProfiling;
	}

	/**
	 * Sets the variabiliDiProfiling value for this GestisciDID_Input.
	 * 
	 * @param variabiliDiProfiling
	 */
	public void setVariabiliDiProfiling(
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling variabiliDiProfiling) {
		this.variabiliDiProfiling = variabiliDiProfiling;
	}

	/**
	 * Gets the informazioniDID value for this GestisciDID_Input.
	 * 
	 * @return informazioniDID
	 */
	public it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID getInformazioniDID() {
		return informazioniDID;
	}

	/**
	 * Sets the informazioniDID value for this GestisciDID_Input.
	 * 
	 * @param informazioniDID
	 */
	public void setInformazioniDID(
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID informazioniDID) {
		this.informazioniDID = informazioniDID;
	}

	/**
	 * Gets the GUIDOperatore value for this GestisciDID_Input.
	 * 
	 * @return GUIDOperatore
	 */
	public java.lang.String getGUIDOperatore() {
		return GUIDOperatore;
	}

	/**
	 * Sets the GUIDOperatore value for this GestisciDID_Input.
	 * 
	 * @param GUIDOperatore
	 */
	public void setGUIDOperatore(java.lang.String GUIDOperatore) {
		this.GUIDOperatore = GUIDOperatore;
	}

	/**
	 * Gets the codiceFiscaleOperatore value for this GestisciDID_Input.
	 * 
	 * @return codiceFiscaleOperatore
	 */
	public java.lang.String getCodiceFiscaleOperatore() {
		return codiceFiscaleOperatore;
	}

	/**
	 * Sets the codiceFiscaleOperatore value for this GestisciDID_Input.
	 * 
	 * @param codiceFiscaleOperatore
	 */
	public void setCodiceFiscaleOperatore(java.lang.String codiceFiscaleOperatore) {
		this.codiceFiscaleOperatore = codiceFiscaleOperatore;
	}

	/**
	 * Gets the applicazione value for this GestisciDID_Input.
	 * 
	 * @return applicazione
	 */
	public it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Applicazione getApplicazione() {
		return applicazione;
	}

	/**
	 * Sets the applicazione value for this GestisciDID_Input.
	 * 
	 * @param applicazione
	 */
	public void setApplicazione(
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Applicazione applicazione) {
		this.applicazione = applicazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GestisciDID_Input))
			return false;
		GestisciDID_Input other = (GestisciDID_Input) obj;
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
				&& ((this.variabiliDiProfiling == null && other.getVariabiliDiProfiling() == null)
						|| (this.variabiliDiProfiling != null
								&& this.variabiliDiProfiling.equals(other.getVariabiliDiProfiling())))
				&& ((this.informazioniDID == null && other.getInformazioniDID() == null)
						|| (this.informazioniDID != null && this.informazioniDID.equals(other.getInformazioniDID())))
				&& ((this.GUIDOperatore == null && other.getGUIDOperatore() == null)
						|| (this.GUIDOperatore != null && this.GUIDOperatore.equals(other.getGUIDOperatore())))
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
		if (getVariabiliDiProfiling() != null) {
			_hashCode += getVariabiliDiProfiling().hashCode();
		}
		if (getInformazioniDID() != null) {
			_hashCode += getInformazioniDID().hashCode();
		}
		if (getGUIDOperatore() != null) {
			_hashCode += getGUIDOperatore().hashCode();
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
			GestisciDID_Input.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "GestisciDID_Input"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("variabiliDiProfiling");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "VariabiliDiProfiling"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "VariabiliDiProfiling"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("informazioniDID");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "InformazioniDID"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "InformazioniDID"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("GUIDOperatore");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "GUIDOperatore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscaleOperatore");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "CodiceFiscaleOperatore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("applicazione");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Applicazione"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Applicazione"));
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
