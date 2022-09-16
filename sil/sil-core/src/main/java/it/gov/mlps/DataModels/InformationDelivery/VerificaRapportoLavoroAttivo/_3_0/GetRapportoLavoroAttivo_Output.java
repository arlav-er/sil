/**
 * GetRapportoLavoroAttivo_Output.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0;

public class GetRapportoLavoroAttivo_Output implements java.io.Serializable {
	private it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoroAttivo[] rapportoLavoroAttivo;

	private it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Esito esito;

	public GetRapportoLavoroAttivo_Output() {
	}

	public GetRapportoLavoroAttivo_Output(
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoroAttivo[] rapportoLavoroAttivo,
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Esito esito) {
		this.rapportoLavoroAttivo = rapportoLavoroAttivo;
		this.esito = esito;
	}

	/**
	 * Gets the rapportoLavoroAttivo value for this GetRapportoLavoroAttivo_Output.
	 * 
	 * @return rapportoLavoroAttivo
	 */
	public it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoroAttivo[] getRapportoLavoroAttivo() {
		return rapportoLavoroAttivo;
	}

	/**
	 * Sets the rapportoLavoroAttivo value for this GetRapportoLavoroAttivo_Output.
	 * 
	 * @param rapportoLavoroAttivo
	 */
	public void setRapportoLavoroAttivo(
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoroAttivo[] rapportoLavoroAttivo) {
		this.rapportoLavoroAttivo = rapportoLavoroAttivo;
	}

	public it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoroAttivo getRapportoLavoroAttivo(
			int i) {
		return this.rapportoLavoroAttivo[i];
	}

	public void setRapportoLavoroAttivo(int i,
			it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.RapportoLavoroAttivo _value) {
		this.rapportoLavoroAttivo[i] = _value;
	}

	/**
	 * Gets the esito value for this GetRapportoLavoroAttivo_Output.
	 * 
	 * @return esito
	 */
	public it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Esito getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this GetRapportoLavoroAttivo_Output.
	 * 
	 * @param esito
	 */
	public void setEsito(it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.Esito esito) {
		this.esito = esito;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GetRapportoLavoroAttivo_Output))
			return false;
		GetRapportoLavoroAttivo_Output other = (GetRapportoLavoroAttivo_Output) obj;
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
				&& ((this.rapportoLavoroAttivo == null && other.getRapportoLavoroAttivo() == null)
						|| (this.rapportoLavoroAttivo != null
								&& java.util.Arrays.equals(this.rapportoLavoroAttivo, other.getRapportoLavoroAttivo())))
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
		if (getRapportoLavoroAttivo() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getRapportoLavoroAttivo()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getRapportoLavoroAttivo(), i);
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
			GetRapportoLavoroAttivo_Output.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"GetRapportoLavoroAttivo_Output"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rapportoLavoroAttivo");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"RapportoLavoroAttivo"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"RapportoLavoroAttivo"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Esito"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/VerificaRapportoLavoroAttivo/3.0", "Esito"));
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
