/**
 * RapportoLavoro.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0;

public class RapportoLavoro implements java.io.Serializable {
	private java.util.Calendar dataInizio;

	private java.util.Calendar dataFine;

	private java.lang.String codiceTipologiaContrattuale;

	private java.lang.String tipologiaContrattuale;

	private java.lang.String tipologiaContrattualeTedesco;

	public RapportoLavoro() {
	}

	public RapportoLavoro(java.util.Calendar dataInizio, java.util.Calendar dataFine,
			java.lang.String codiceTipologiaContrattuale, java.lang.String tipologiaContrattuale,
			java.lang.String tipologiaContrattualeTedesco) {
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.codiceTipologiaContrattuale = codiceTipologiaContrattuale;
		this.tipologiaContrattuale = tipologiaContrattuale;
		this.tipologiaContrattualeTedesco = tipologiaContrattualeTedesco;
	}

	/**
	 * Gets the dataInizio value for this RapportoLavoro.
	 * 
	 * @return dataInizio
	 */
	public java.util.Calendar getDataInizio() {
		return dataInizio;
	}

	/**
	 * Sets the dataInizio value for this RapportoLavoro.
	 * 
	 * @param dataInizio
	 */
	public void setDataInizio(java.util.Calendar dataInizio) {
		this.dataInizio = dataInizio;
	}

	/**
	 * Gets the dataFine value for this RapportoLavoro.
	 * 
	 * @return dataFine
	 */
	public java.util.Calendar getDataFine() {
		return dataFine;
	}

	/**
	 * Sets the dataFine value for this RapportoLavoro.
	 * 
	 * @param dataFine
	 */
	public void setDataFine(java.util.Calendar dataFine) {
		this.dataFine = dataFine;
	}

	/**
	 * Gets the codiceTipologiaContrattuale value for this RapportoLavoro.
	 * 
	 * @return codiceTipologiaContrattuale
	 */
	public java.lang.String getCodiceTipologiaContrattuale() {
		return codiceTipologiaContrattuale;
	}

	/**
	 * Sets the codiceTipologiaContrattuale value for this RapportoLavoro.
	 * 
	 * @param codiceTipologiaContrattuale
	 */
	public void setCodiceTipologiaContrattuale(java.lang.String codiceTipologiaContrattuale) {
		this.codiceTipologiaContrattuale = codiceTipologiaContrattuale;
	}

	/**
	 * Gets the tipologiaContrattuale value for this RapportoLavoro.
	 * 
	 * @return tipologiaContrattuale
	 */
	public java.lang.String getTipologiaContrattuale() {
		return tipologiaContrattuale;
	}

	/**
	 * Sets the tipologiaContrattuale value for this RapportoLavoro.
	 * 
	 * @param tipologiaContrattuale
	 */
	public void setTipologiaContrattuale(java.lang.String tipologiaContrattuale) {
		this.tipologiaContrattuale = tipologiaContrattuale;
	}

	/**
	 * Gets the tipologiaContrattualeTedesco value for this RapportoLavoro.
	 * 
	 * @return tipologiaContrattualeTedesco
	 */
	public java.lang.String getTipologiaContrattualeTedesco() {
		return tipologiaContrattualeTedesco;
	}

	/**
	 * Sets the tipologiaContrattualeTedesco value for this RapportoLavoro.
	 * 
	 * @param tipologiaContrattualeTedesco
	 */
	public void setTipologiaContrattualeTedesco(java.lang.String tipologiaContrattualeTedesco) {
		this.tipologiaContrattualeTedesco = tipologiaContrattualeTedesco;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof RapportoLavoro))
			return false;
		RapportoLavoro other = (RapportoLavoro) obj;
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
				&& ((this.dataInizio == null && other.getDataInizio() == null)
						|| (this.dataInizio != null && this.dataInizio.equals(other.getDataInizio())))
				&& ((this.dataFine == null && other.getDataFine() == null)
						|| (this.dataFine != null && this.dataFine.equals(other.getDataFine())))
				&& ((this.codiceTipologiaContrattuale == null && other.getCodiceTipologiaContrattuale() == null)
						|| (this.codiceTipologiaContrattuale != null
								&& this.codiceTipologiaContrattuale.equals(other.getCodiceTipologiaContrattuale())))
				&& ((this.tipologiaContrattuale == null && other.getTipologiaContrattuale() == null)
						|| (this.tipologiaContrattuale != null
								&& this.tipologiaContrattuale.equals(other.getTipologiaContrattuale())))
				&& ((this.tipologiaContrattualeTedesco == null && other.getTipologiaContrattualeTedesco() == null)
						|| (this.tipologiaContrattualeTedesco != null
								&& this.tipologiaContrattualeTedesco.equals(other.getTipologiaContrattualeTedesco())));
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
		if (getDataInizio() != null) {
			_hashCode += getDataInizio().hashCode();
		}
		if (getDataFine() != null) {
			_hashCode += getDataFine().hashCode();
		}
		if (getCodiceTipologiaContrattuale() != null) {
			_hashCode += getCodiceTipologiaContrattuale().hashCode();
		}
		if (getTipologiaContrattuale() != null) {
			_hashCode += getTipologiaContrattuale().hashCode();
		}
		if (getTipologiaContrattualeTedesco() != null) {
			_hashCode += getTipologiaContrattualeTedesco().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			RapportoLavoro.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"RapportoLavoro"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataInizio");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "DataInizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataFine");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "DataFine"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceTipologiaContrattuale");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"CodiceTipologiaContrattuale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipologiaContrattuale");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"TipologiaContrattuale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipologiaContrattualeTedesco");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"TipologiaContrattualeTedesco"));
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
