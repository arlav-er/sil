/**
 * RichiestaNotificaEsitoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0;

public class RichiestaNotificaEsitoType implements java.io.Serializable {
	private java.lang.String idAppuntamento;

	private java.lang.String idAppuntamentoAR;

	private java.lang.String idEsitoAppuntamento;

	private java.lang.String idCausa;

	private java.lang.String note;

	private it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NuovoAppuntamentoType nuovoAppuntamento;

	private java.lang.String idCPI;

	public RichiestaNotificaEsitoType() {
	}

	public RichiestaNotificaEsitoType(java.lang.String idAppuntamento, java.lang.String idAppuntamentoAR,
			java.lang.String idEsitoAppuntamento, java.lang.String idCausa, java.lang.String note,
			it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NuovoAppuntamentoType nuovoAppuntamento,
			java.lang.String idCPI) {
		this.idAppuntamento = idAppuntamento;
		this.idAppuntamentoAR = idAppuntamentoAR;
		this.idEsitoAppuntamento = idEsitoAppuntamento;
		this.idCausa = idCausa;
		this.note = note;
		this.nuovoAppuntamento = nuovoAppuntamento;
		this.idCPI = idCPI;
	}

	/**
	 * Gets the idAppuntamento value for this RichiestaNotificaEsitoType.
	 * 
	 * @return idAppuntamento
	 */
	public java.lang.String getIdAppuntamento() {
		return idAppuntamento;
	}

	/**
	 * Sets the idAppuntamento value for this RichiestaNotificaEsitoType.
	 * 
	 * @param idAppuntamento
	 */
	public void setIdAppuntamento(java.lang.String idAppuntamento) {
		this.idAppuntamento = idAppuntamento;
	}

	/**
	 * Gets the idAppuntamentoAR value for this RichiestaNotificaEsitoType.
	 * 
	 * @return idAppuntamentoAR
	 */
	public java.lang.String getIdAppuntamentoAR() {
		return idAppuntamentoAR;
	}

	/**
	 * Sets the idAppuntamentoAR value for this RichiestaNotificaEsitoType.
	 * 
	 * @param idAppuntamentoAR
	 */
	public void setIdAppuntamentoAR(java.lang.String idAppuntamentoAR) {
		this.idAppuntamentoAR = idAppuntamentoAR;
	}

	/**
	 * Gets the idEsitoAppuntamento value for this RichiestaNotificaEsitoType.
	 * 
	 * @return idEsitoAppuntamento
	 */
	public java.lang.String getIdEsitoAppuntamento() {
		return idEsitoAppuntamento;
	}

	/**
	 * Sets the idEsitoAppuntamento value for this RichiestaNotificaEsitoType.
	 * 
	 * @param idEsitoAppuntamento
	 */
	public void setIdEsitoAppuntamento(java.lang.String idEsitoAppuntamento) {
		this.idEsitoAppuntamento = idEsitoAppuntamento;
	}

	/**
	 * Gets the idCausa value for this RichiestaNotificaEsitoType.
	 * 
	 * @return idCausa
	 */
	public java.lang.String getIdCausa() {
		return idCausa;
	}

	/**
	 * Sets the idCausa value for this RichiestaNotificaEsitoType.
	 * 
	 * @param idCausa
	 */
	public void setIdCausa(java.lang.String idCausa) {
		this.idCausa = idCausa;
	}

	/**
	 * Gets the note value for this RichiestaNotificaEsitoType.
	 * 
	 * @return note
	 */
	public java.lang.String getNote() {
		return note;
	}

	/**
	 * Sets the note value for this RichiestaNotificaEsitoType.
	 * 
	 * @param note
	 */
	public void setNote(java.lang.String note) {
		this.note = note;
	}

	/**
	 * Gets the nuovoAppuntamento value for this RichiestaNotificaEsitoType.
	 * 
	 * @return nuovoAppuntamento
	 */
	public it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NuovoAppuntamentoType getNuovoAppuntamento() {
		return nuovoAppuntamento;
	}

	/**
	 * Sets the nuovoAppuntamento value for this RichiestaNotificaEsitoType.
	 * 
	 * @param nuovoAppuntamento
	 */
	public void setNuovoAppuntamento(
			it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NuovoAppuntamentoType nuovoAppuntamento) {
		this.nuovoAppuntamento = nuovoAppuntamento;
	}

	/**
	 * Gets the idCPI value for this RichiestaNotificaEsitoType.
	 * 
	 * @return idCPI
	 */
	public java.lang.String getIdCPI() {
		return idCPI;
	}

	/**
	 * Sets the idCPI value for this RichiestaNotificaEsitoType.
	 * 
	 * @param idCPI
	 */
	public void setIdCPI(java.lang.String idCPI) {
		this.idCPI = idCPI;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof RichiestaNotificaEsitoType))
			return false;
		RichiestaNotificaEsitoType other = (RichiestaNotificaEsitoType) obj;
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
				&& ((this.idAppuntamento == null && other.getIdAppuntamento() == null)
						|| (this.idAppuntamento != null && this.idAppuntamento.equals(other.getIdAppuntamento())))
				&& ((this.idAppuntamentoAR == null && other.getIdAppuntamentoAR() == null)
						|| (this.idAppuntamentoAR != null && this.idAppuntamentoAR.equals(other.getIdAppuntamentoAR())))
				&& ((this.idEsitoAppuntamento == null && other.getIdEsitoAppuntamento() == null)
						|| (this.idEsitoAppuntamento != null
								&& this.idEsitoAppuntamento.equals(other.getIdEsitoAppuntamento())))
				&& ((this.idCausa == null && other.getIdCausa() == null)
						|| (this.idCausa != null && this.idCausa.equals(other.getIdCausa())))
				&& ((this.note == null && other.getNote() == null)
						|| (this.note != null && this.note.equals(other.getNote())))
				&& ((this.nuovoAppuntamento == null && other.getNuovoAppuntamento() == null)
						|| (this.nuovoAppuntamento != null
								&& this.nuovoAppuntamento.equals(other.getNuovoAppuntamento())))
				&& ((this.idCPI == null && other.getIdCPI() == null)
						|| (this.idCPI != null && this.idCPI.equals(other.getIdCPI())));
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
		if (getIdAppuntamento() != null) {
			_hashCode += getIdAppuntamento().hashCode();
		}
		if (getIdAppuntamentoAR() != null) {
			_hashCode += getIdAppuntamentoAR().hashCode();
		}
		if (getIdEsitoAppuntamento() != null) {
			_hashCode += getIdEsitoAppuntamento().hashCode();
		}
		if (getIdCausa() != null) {
			_hashCode += getIdCausa().hashCode();
		}
		if (getNote() != null) {
			_hashCode += getNote().hashCode();
		}
		if (getNuovoAppuntamento() != null) {
			_hashCode += getNuovoAppuntamento().hashCode();
		}
		if (getIdCPI() != null) {
			_hashCode += getIdCPI().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			RichiestaNotificaEsitoType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"RichiestaNotificaEsitoType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idAppuntamento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdAppuntamento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idAppuntamentoAR");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdAppuntamentoAR"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idEsitoAppuntamento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdEsitoAppuntamento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idCausa");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "IdCausa"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("note");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "Note"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nuovoAppuntamento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"NuovoAppuntamento"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"NuovoAppuntamentoType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idCPI");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "IdCPI"));
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
