/**
 * RapportoLavoroAttivo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0;

public class RapportoLavoroAttivo implements java.io.Serializable {
	private it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Lavoratore lavoratore;

	private it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.DatoreLavoro datoreLavoro;

	private it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoro rapportoLavoro;

	public RapportoLavoroAttivo() {
	}

	public RapportoLavoroAttivo(
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Lavoratore lavoratore,
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.DatoreLavoro datoreLavoro,
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoro rapportoLavoro) {
		this.lavoratore = lavoratore;
		this.datoreLavoro = datoreLavoro;
		this.rapportoLavoro = rapportoLavoro;
	}

	/**
	 * Gets the lavoratore value for this RapportoLavoroAttivo.
	 * 
	 * @return lavoratore
	 */
	public it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Lavoratore getLavoratore() {
		return lavoratore;
	}

	/**
	 * Sets the lavoratore value for this RapportoLavoroAttivo.
	 * 
	 * @param lavoratore
	 */
	public void setLavoratore(
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Lavoratore lavoratore) {
		this.lavoratore = lavoratore;
	}

	/**
	 * Gets the datoreLavoro value for this RapportoLavoroAttivo.
	 * 
	 * @return datoreLavoro
	 */
	public it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.DatoreLavoro getDatoreLavoro() {
		return datoreLavoro;
	}

	/**
	 * Sets the datoreLavoro value for this RapportoLavoroAttivo.
	 * 
	 * @param datoreLavoro
	 */
	public void setDatoreLavoro(
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.DatoreLavoro datoreLavoro) {
		this.datoreLavoro = datoreLavoro;
	}

	/**
	 * Gets the rapportoLavoro value for this RapportoLavoroAttivo.
	 * 
	 * @return rapportoLavoro
	 */
	public it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoro getRapportoLavoro() {
		return rapportoLavoro;
	}

	/**
	 * Sets the rapportoLavoro value for this RapportoLavoroAttivo.
	 * 
	 * @param rapportoLavoro
	 */
	public void setRapportoLavoro(
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoro rapportoLavoro) {
		this.rapportoLavoro = rapportoLavoro;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof RapportoLavoroAttivo))
			return false;
		RapportoLavoroAttivo other = (RapportoLavoroAttivo) obj;
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
				&& ((this.lavoratore == null && other.getLavoratore() == null)
						|| (this.lavoratore != null && this.lavoratore.equals(other.getLavoratore())))
				&& ((this.datoreLavoro == null && other.getDatoreLavoro() == null)
						|| (this.datoreLavoro != null && this.datoreLavoro.equals(other.getDatoreLavoro())))
				&& ((this.rapportoLavoro == null && other.getRapportoLavoro() == null)
						|| (this.rapportoLavoro != null && this.rapportoLavoro.equals(other.getRapportoLavoro())));
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
		if (getLavoratore() != null) {
			_hashCode += getLavoratore().hashCode();
		}
		if (getDatoreLavoro() != null) {
			_hashCode += getDatoreLavoro().hashCode();
		}
		if (getRapportoLavoro() != null) {
			_hashCode += getRapportoLavoro().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			RapportoLavoroAttivo.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"RapportoLavoroAttivo"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lavoratore");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Lavoratore"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Lavoratore"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datoreLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "DatoreLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "DatoreLavoro"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"RapportoLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"RapportoLavoro"));
		elemField.setMinOccurs(0);
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
