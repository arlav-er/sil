/**
 * GetRapportoLavoroAttivo_Input.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0;

public class GetRapportoLavoroAttivo_Input implements java.io.Serializable {
	private java.lang.String codiceFiscaleLavoratore;

	private java.util.Calendar dataRiferimentoDal;

	private java.util.Calendar dataRiferimentoAl;

	public GetRapportoLavoroAttivo_Input() {
	}

	public GetRapportoLavoroAttivo_Input(java.lang.String codiceFiscaleLavoratore,
			java.util.Calendar dataRiferimentoDal, java.util.Calendar dataRiferimentoAl) {
		this.codiceFiscaleLavoratore = codiceFiscaleLavoratore;
		this.dataRiferimentoDal = dataRiferimentoDal;
		this.dataRiferimentoAl = dataRiferimentoAl;
	}

	/**
	 * Gets the codiceFiscaleLavoratore value for this GetRapportoLavoroAttivo_Input.
	 * 
	 * @return codiceFiscaleLavoratore
	 */
	public java.lang.String getCodiceFiscaleLavoratore() {
		return codiceFiscaleLavoratore;
	}

	/**
	 * Sets the codiceFiscaleLavoratore value for this GetRapportoLavoroAttivo_Input.
	 * 
	 * @param codiceFiscaleLavoratore
	 */
	public void setCodiceFiscaleLavoratore(java.lang.String codiceFiscaleLavoratore) {
		this.codiceFiscaleLavoratore = codiceFiscaleLavoratore;
	}

	/**
	 * Gets the dataRiferimentoDal value for this GetRapportoLavoroAttivo_Input.
	 * 
	 * @return dataRiferimentoDal
	 */
	public java.util.Calendar getDataRiferimentoDal() {
		return dataRiferimentoDal;
	}

	/**
	 * Sets the dataRiferimentoDal value for this GetRapportoLavoroAttivo_Input.
	 * 
	 * @param dataRiferimentoDal
	 */
	public void setDataRiferimentoDal(java.util.Calendar dataRiferimentoDal) {
		this.dataRiferimentoDal = dataRiferimentoDal;
	}

	/**
	 * Gets the dataRiferimentoAl value for this GetRapportoLavoroAttivo_Input.
	 * 
	 * @return dataRiferimentoAl
	 */
	public java.util.Calendar getDataRiferimentoAl() {
		return dataRiferimentoAl;
	}

	/**
	 * Sets the dataRiferimentoAl value for this GetRapportoLavoroAttivo_Input.
	 * 
	 * @param dataRiferimentoAl
	 */
	public void setDataRiferimentoAl(java.util.Calendar dataRiferimentoAl) {
		this.dataRiferimentoAl = dataRiferimentoAl;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GetRapportoLavoroAttivo_Input))
			return false;
		GetRapportoLavoroAttivo_Input other = (GetRapportoLavoroAttivo_Input) obj;
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
				&& ((this.dataRiferimentoDal == null && other.getDataRiferimentoDal() == null)
						|| (this.dataRiferimentoDal != null
								&& this.dataRiferimentoDal.equals(other.getDataRiferimentoDal())))
				&& ((this.dataRiferimentoAl == null && other.getDataRiferimentoAl() == null)
						|| (this.dataRiferimentoAl != null
								&& this.dataRiferimentoAl.equals(other.getDataRiferimentoAl())));
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
		if (getDataRiferimentoDal() != null) {
			_hashCode += getDataRiferimentoDal().hashCode();
		}
		if (getDataRiferimentoAl() != null) {
			_hashCode += getDataRiferimentoAl().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			GetRapportoLavoroAttivo_Input.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"GetRapportoLavoroAttivo_Input"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscaleLavoratore");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"CodiceFiscaleLavoratore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataRiferimentoDal");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"DataRiferimentoDal"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataRiferimentoAl");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"DataRiferimentoAl"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
