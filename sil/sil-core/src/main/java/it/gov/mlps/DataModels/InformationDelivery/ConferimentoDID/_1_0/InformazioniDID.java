/**
 * InformazioniDID.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0;

public class InformazioniDID implements java.io.Serializable {
	private it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale codiceFiscale;

	private it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento tipoEvento;

	private java.util.Calendar dataEvento;

	private java.util.Calendar dataDID;

	private java.lang.String codiceEntePromotore;

	public InformazioniDID() {
	}

	public InformazioniDID(it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale codiceFiscale,
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento tipoEvento,
			java.util.Calendar dataEvento, java.util.Calendar dataDID, java.lang.String codiceEntePromotore) {
		this.codiceFiscale = codiceFiscale;
		this.tipoEvento = tipoEvento;
		this.dataEvento = dataEvento;
		this.dataDID = dataDID;
		this.codiceEntePromotore = codiceEntePromotore;
	}

	/**
	 * Gets the codiceFiscale value for this InformazioniDID.
	 * 
	 * @return codiceFiscale
	 */
	public it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this InformazioniDID.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the tipoEvento value for this InformazioniDID.
	 * 
	 * @return tipoEvento
	 */
	public it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento getTipoEvento() {
		return tipoEvento;
	}

	/**
	 * Sets the tipoEvento value for this InformazioniDID.
	 * 
	 * @param tipoEvento
	 */
	public void setTipoEvento(it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	/**
	 * Gets the dataEvento value for this InformazioniDID.
	 * 
	 * @return dataEvento
	 */
	public java.util.Calendar getDataEvento() {
		return dataEvento;
	}

	/**
	 * Sets the dataEvento value for this InformazioniDID.
	 * 
	 * @param dataEvento
	 */
	public void setDataEvento(java.util.Calendar dataEvento) {
		this.dataEvento = dataEvento;
	}

	/**
	 * Gets the dataDID value for this InformazioniDID.
	 * 
	 * @return dataDID
	 */
	public java.util.Calendar getDataDID() {
		return dataDID;
	}

	/**
	 * Sets the dataDID value for this InformazioniDID.
	 * 
	 * @param dataDID
	 */
	public void setDataDID(java.util.Calendar dataDID) {
		this.dataDID = dataDID;
	}

	/**
	 * Gets the codiceEntePromotore value for this InformazioniDID.
	 * 
	 * @return codiceEntePromotore
	 */
	public java.lang.String getCodiceEntePromotore() {
		return codiceEntePromotore;
	}

	/**
	 * Sets the codiceEntePromotore value for this InformazioniDID.
	 * 
	 * @param codiceEntePromotore
	 */
	public void setCodiceEntePromotore(java.lang.String codiceEntePromotore) {
		this.codiceEntePromotore = codiceEntePromotore;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof InformazioniDID))
			return false;
		InformazioniDID other = (InformazioniDID) obj;
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
				&& ((this.tipoEvento == null && other.getTipoEvento() == null)
						|| (this.tipoEvento != null && this.tipoEvento.equals(other.getTipoEvento())))
				&& ((this.dataEvento == null && other.getDataEvento() == null)
						|| (this.dataEvento != null && this.dataEvento.equals(other.getDataEvento())))
				&& ((this.dataDID == null && other.getDataDID() == null)
						|| (this.dataDID != null && this.dataDID.equals(other.getDataDID())))
				&& ((this.codiceEntePromotore == null && other.getCodiceEntePromotore() == null)
						|| (this.codiceEntePromotore != null
								&& this.codiceEntePromotore.equals(other.getCodiceEntePromotore())));
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
		if (getTipoEvento() != null) {
			_hashCode += getTipoEvento().hashCode();
		}
		if (getDataEvento() != null) {
			_hashCode += getDataEvento().hashCode();
		}
		if (getDataDID() != null) {
			_hashCode += getDataDID().hashCode();
		}
		if (getCodiceEntePromotore() != null) {
			_hashCode += getCodiceEntePromotore().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			InformazioniDID.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "InformazioniDID"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "CodiceFiscale"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipoEvento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "TipoEvento"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "TipoEvento"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataEvento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "DataEvento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataDID");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "DataDID"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceEntePromotore");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "CodiceEntePromotore"));
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
