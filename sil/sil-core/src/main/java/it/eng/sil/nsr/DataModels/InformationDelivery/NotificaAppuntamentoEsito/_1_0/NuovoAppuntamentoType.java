/**
 * NuovoAppuntamentoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0;

public class NuovoAppuntamentoType implements java.io.Serializable {
	private java.lang.String idAppuntamentoAR;

	private java.lang.String idDisponibilitaAR;

	private java.lang.String codiceIntermediario;

	private java.lang.String idSportello;

	private java.lang.String descrizioneSportello;

	private java.util.Calendar dataOraAppuntamento;

	private java.lang.String noteSedeRegione;

	public NuovoAppuntamentoType() {
	}

	public NuovoAppuntamentoType(java.lang.String idAppuntamentoAR, java.lang.String idDisponibilitaAR,
			java.lang.String codiceIntermediario, java.lang.String idSportello, java.lang.String descrizioneSportello,
			java.util.Calendar dataOraAppuntamento, java.lang.String noteSedeRegione) {
		this.idAppuntamentoAR = idAppuntamentoAR;
		this.idDisponibilitaAR = idDisponibilitaAR;
		this.codiceIntermediario = codiceIntermediario;
		this.idSportello = idSportello;
		this.descrizioneSportello = descrizioneSportello;
		this.dataOraAppuntamento = dataOraAppuntamento;
		this.noteSedeRegione = noteSedeRegione;
	}

	/**
	 * Gets the idAppuntamentoAR value for this NuovoAppuntamentoType.
	 * 
	 * @return idAppuntamentoAR
	 */
	public java.lang.String getIdAppuntamentoAR() {
		return idAppuntamentoAR;
	}

	/**
	 * Sets the idAppuntamentoAR value for this NuovoAppuntamentoType.
	 * 
	 * @param idAppuntamentoAR
	 */
	public void setIdAppuntamentoAR(java.lang.String idAppuntamentoAR) {
		this.idAppuntamentoAR = idAppuntamentoAR;
	}

	/**
	 * Gets the idDisponibilitaAR value for this NuovoAppuntamentoType.
	 * 
	 * @return idDisponibilitaAR
	 */
	public java.lang.String getIdDisponibilitaAR() {
		return idDisponibilitaAR;
	}

	/**
	 * Sets the idDisponibilitaAR value for this NuovoAppuntamentoType.
	 * 
	 * @param idDisponibilitaAR
	 */
	public void setIdDisponibilitaAR(java.lang.String idDisponibilitaAR) {
		this.idDisponibilitaAR = idDisponibilitaAR;
	}

	/**
	 * Gets the codiceIntermediario value for this NuovoAppuntamentoType.
	 * 
	 * @return codiceIntermediario
	 */
	public java.lang.String getCodiceIntermediario() {
		return codiceIntermediario;
	}

	/**
	 * Sets the codiceIntermediario value for this NuovoAppuntamentoType.
	 * 
	 * @param codiceIntermediario
	 */
	public void setCodiceIntermediario(java.lang.String codiceIntermediario) {
		this.codiceIntermediario = codiceIntermediario;
	}

	/**
	 * Gets the idSportello value for this NuovoAppuntamentoType.
	 * 
	 * @return idSportello
	 */
	public java.lang.String getIdSportello() {
		return idSportello;
	}

	/**
	 * Sets the idSportello value for this NuovoAppuntamentoType.
	 * 
	 * @param idSportello
	 */
	public void setIdSportello(java.lang.String idSportello) {
		this.idSportello = idSportello;
	}

	/**
	 * Gets the descrizioneSportello value for this NuovoAppuntamentoType.
	 * 
	 * @return descrizioneSportello
	 */
	public java.lang.String getDescrizioneSportello() {
		return descrizioneSportello;
	}

	/**
	 * Sets the descrizioneSportello value for this NuovoAppuntamentoType.
	 * 
	 * @param descrizioneSportello
	 */
	public void setDescrizioneSportello(java.lang.String descrizioneSportello) {
		this.descrizioneSportello = descrizioneSportello;
	}

	/**
	 * Gets the dataOraAppuntamento value for this NuovoAppuntamentoType.
	 * 
	 * @return dataOraAppuntamento
	 */
	public java.util.Calendar getDataOraAppuntamento() {
		return dataOraAppuntamento;
	}

	/**
	 * Sets the dataOraAppuntamento value for this NuovoAppuntamentoType.
	 * 
	 * @param dataOraAppuntamento
	 */
	public void setDataOraAppuntamento(java.util.Calendar dataOraAppuntamento) {
		this.dataOraAppuntamento = dataOraAppuntamento;
	}

	/**
	 * Gets the noteSedeRegione value for this NuovoAppuntamentoType.
	 * 
	 * @return noteSedeRegione
	 */
	public java.lang.String getNoteSedeRegione() {
		return noteSedeRegione;
	}

	/**
	 * Sets the noteSedeRegione value for this NuovoAppuntamentoType.
	 * 
	 * @param noteSedeRegione
	 */
	public void setNoteSedeRegione(java.lang.String noteSedeRegione) {
		this.noteSedeRegione = noteSedeRegione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof NuovoAppuntamentoType))
			return false;
		NuovoAppuntamentoType other = (NuovoAppuntamentoType) obj;
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
				&& ((this.idAppuntamentoAR == null && other.getIdAppuntamentoAR() == null)
						|| (this.idAppuntamentoAR != null && this.idAppuntamentoAR.equals(other.getIdAppuntamentoAR())))
				&& ((this.idDisponibilitaAR == null && other.getIdDisponibilitaAR() == null)
						|| (this.idDisponibilitaAR != null
								&& this.idDisponibilitaAR.equals(other.getIdDisponibilitaAR())))
				&& ((this.codiceIntermediario == null && other.getCodiceIntermediario() == null)
						|| (this.codiceIntermediario != null
								&& this.codiceIntermediario.equals(other.getCodiceIntermediario())))
				&& ((this.idSportello == null && other.getIdSportello() == null)
						|| (this.idSportello != null && this.idSportello.equals(other.getIdSportello())))
				&& ((this.descrizioneSportello == null && other.getDescrizioneSportello() == null)
						|| (this.descrizioneSportello != null
								&& this.descrizioneSportello.equals(other.getDescrizioneSportello())))
				&& ((this.dataOraAppuntamento == null && other.getDataOraAppuntamento() == null)
						|| (this.dataOraAppuntamento != null
								&& this.dataOraAppuntamento.equals(other.getDataOraAppuntamento())))
				&& ((this.noteSedeRegione == null && other.getNoteSedeRegione() == null)
						|| (this.noteSedeRegione != null && this.noteSedeRegione.equals(other.getNoteSedeRegione())));
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
		if (getIdAppuntamentoAR() != null) {
			_hashCode += getIdAppuntamentoAR().hashCode();
		}
		if (getIdDisponibilitaAR() != null) {
			_hashCode += getIdDisponibilitaAR().hashCode();
		}
		if (getCodiceIntermediario() != null) {
			_hashCode += getCodiceIntermediario().hashCode();
		}
		if (getIdSportello() != null) {
			_hashCode += getIdSportello().hashCode();
		}
		if (getDescrizioneSportello() != null) {
			_hashCode += getDescrizioneSportello().hashCode();
		}
		if (getDataOraAppuntamento() != null) {
			_hashCode += getDataOraAppuntamento().hashCode();
		}
		if (getNoteSedeRegione() != null) {
			_hashCode += getNoteSedeRegione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			NuovoAppuntamentoType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"NuovoAppuntamentoType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idAppuntamentoAR");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdAppuntamentoAR"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idDisponibilitaAR");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdDisponibilitaAR"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceIntermediario");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"CodiceIntermediario"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSportello");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "IdSportello"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descrizioneSportello");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"DescrizioneSportello"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataOraAppuntamento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"DataOraAppuntamento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("noteSedeRegione");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"NoteSedeRegione"));
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
