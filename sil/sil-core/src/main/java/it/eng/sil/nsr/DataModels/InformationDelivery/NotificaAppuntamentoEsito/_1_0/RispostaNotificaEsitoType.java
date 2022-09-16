/**
 * RispostaNotificaEsitoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0;

public class RispostaNotificaEsitoType implements java.io.Serializable {
	private java.lang.Object idEsito;

	private java.lang.String descrizioneEsitoNegativo;

	private java.lang.String idNuovoAppuntamento;

	public RispostaNotificaEsitoType() {
	}

	public RispostaNotificaEsitoType(java.lang.Object idEsito, java.lang.String descrizioneEsitoNegativo,
			java.lang.String idNuovoAppuntamento) {
		this.idEsito = idEsito;
		this.descrizioneEsitoNegativo = descrizioneEsitoNegativo;
		this.idNuovoAppuntamento = idNuovoAppuntamento;
	}

	/**
	 * Gets the idEsito value for this RispostaNotificaEsitoType.
	 * 
	 * @return idEsito
	 */
	public java.lang.Object getIdEsito() {
		return idEsito;
	}

	/**
	 * Sets the idEsito value for this RispostaNotificaEsitoType.
	 * 
	 * @param idEsito
	 */
	public void setIdEsito(java.lang.Object idEsito) {
		this.idEsito = idEsito;
	}

	/**
	 * Gets the descrizioneEsitoNegativo value for this RispostaNotificaEsitoType.
	 * 
	 * @return descrizioneEsitoNegativo
	 */
	public java.lang.String getDescrizioneEsitoNegativo() {
		return descrizioneEsitoNegativo;
	}

	/**
	 * Sets the descrizioneEsitoNegativo value for this RispostaNotificaEsitoType.
	 * 
	 * @param descrizioneEsitoNegativo
	 */
	public void setDescrizioneEsitoNegativo(java.lang.String descrizioneEsitoNegativo) {
		this.descrizioneEsitoNegativo = descrizioneEsitoNegativo;
	}

	/**
	 * Gets the idNuovoAppuntamento value for this RispostaNotificaEsitoType.
	 * 
	 * @return idNuovoAppuntamento
	 */
	public java.lang.String getIdNuovoAppuntamento() {
		return idNuovoAppuntamento;
	}

	/**
	 * Sets the idNuovoAppuntamento value for this RispostaNotificaEsitoType.
	 * 
	 * @param idNuovoAppuntamento
	 */
	public void setIdNuovoAppuntamento(java.lang.String idNuovoAppuntamento) {
		this.idNuovoAppuntamento = idNuovoAppuntamento;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof RispostaNotificaEsitoType))
			return false;
		RispostaNotificaEsitoType other = (RispostaNotificaEsitoType) obj;
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
				&& ((this.idEsito == null && other.getIdEsito() == null)
						|| (this.idEsito != null && this.idEsito.equals(other.getIdEsito())))
				&& ((this.descrizioneEsitoNegativo == null && other.getDescrizioneEsitoNegativo() == null)
						|| (this.descrizioneEsitoNegativo != null
								&& this.descrizioneEsitoNegativo.equals(other.getDescrizioneEsitoNegativo())))
				&& ((this.idNuovoAppuntamento == null && other.getIdNuovoAppuntamento() == null)
						|| (this.idNuovoAppuntamento != null
								&& this.idNuovoAppuntamento.equals(other.getIdNuovoAppuntamento())));
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
		if (getIdEsito() != null) {
			_hashCode += getIdEsito().hashCode();
		}
		if (getDescrizioneEsitoNegativo() != null) {
			_hashCode += getDescrizioneEsitoNegativo().hashCode();
		}
		if (getIdNuovoAppuntamento() != null) {
			_hashCode += getIdNuovoAppuntamento().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			RispostaNotificaEsitoType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"RispostaNotificaEsitoType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idEsito");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "IdEsito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descrizioneEsitoNegativo");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"DescrizioneEsitoNegativo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idNuovoAppuntamento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdNuovoAppuntamento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
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
