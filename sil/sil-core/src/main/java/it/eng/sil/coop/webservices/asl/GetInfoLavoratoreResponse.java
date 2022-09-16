/**
 * GetInfoLavoratoreResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.asl;

public class GetInfoLavoratoreResponse implements java.io.Serializable {
	private java.lang.String errore;

	private java.lang.String cognome;

	private java.lang.String nome;

	private java.lang.String cf;

	private java.lang.String dataNascita;

	private java.lang.String statoOcc;

	private java.lang.String dataStatoOcc;

	public GetInfoLavoratoreResponse() {
	}

	public GetInfoLavoratoreResponse(java.lang.String errore, java.lang.String cognome, java.lang.String nome,
			java.lang.String cf, java.lang.String dataNascita, java.lang.String statoOcc,
			java.lang.String dataStatoOcc) {
		this.errore = errore;
		this.cognome = cognome;
		this.nome = nome;
		this.cf = cf;
		this.dataNascita = dataNascita;
		this.statoOcc = statoOcc;
		this.dataStatoOcc = dataStatoOcc;
	}

	/**
	 * Gets the errore value for this GetInfoLavoratoreResponse.
	 * 
	 * @return errore
	 */
	public java.lang.String getErrore() {
		return errore;
	}

	/**
	 * Sets the errore value for this GetInfoLavoratoreResponse.
	 * 
	 * @param errore
	 */
	public void setErrore(java.lang.String errore) {
		this.errore = errore;
	}

	/**
	 * Gets the cognome value for this GetInfoLavoratoreResponse.
	 * 
	 * @return cognome
	 */
	public java.lang.String getCognome() {
		return cognome;
	}

	/**
	 * Sets the cognome value for this GetInfoLavoratoreResponse.
	 * 
	 * @param cognome
	 */
	public void setCognome(java.lang.String cognome) {
		this.cognome = cognome;
	}

	/**
	 * Gets the nome value for this GetInfoLavoratoreResponse.
	 * 
	 * @return nome
	 */
	public java.lang.String getNome() {
		return nome;
	}

	/**
	 * Sets the nome value for this GetInfoLavoratoreResponse.
	 * 
	 * @param nome
	 */
	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	/**
	 * Gets the cf value for this GetInfoLavoratoreResponse.
	 * 
	 * @return cf
	 */
	public java.lang.String getCf() {
		return cf;
	}

	/**
	 * Sets the cf value for this GetInfoLavoratoreResponse.
	 * 
	 * @param cf
	 */
	public void setCf(java.lang.String cf) {
		this.cf = cf;
	}

	/**
	 * Gets the dataNascita value for this GetInfoLavoratoreResponse.
	 * 
	 * @return dataNascita
	 */
	public java.lang.String getDataNascita() {
		return dataNascita;
	}

	/**
	 * Sets the dataNascita value for this GetInfoLavoratoreResponse.
	 * 
	 * @param dataNascita
	 */
	public void setDataNascita(java.lang.String dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * Gets the statoOcc value for this GetInfoLavoratoreResponse.
	 * 
	 * @return statoOcc
	 */
	public java.lang.String getStatoOcc() {
		return statoOcc;
	}

	/**
	 * Sets the statoOcc value for this GetInfoLavoratoreResponse.
	 * 
	 * @param statoOcc
	 */
	public void setStatoOcc(java.lang.String statoOcc) {
		this.statoOcc = statoOcc;
	}

	/**
	 * Gets the dataStatoOcc value for this GetInfoLavoratoreResponse.
	 * 
	 * @return dataStatoOcc
	 */
	public java.lang.String getDataStatoOcc() {
		return dataStatoOcc;
	}

	/**
	 * Sets the dataStatoOcc value for this GetInfoLavoratoreResponse.
	 * 
	 * @param dataStatoOcc
	 */
	public void setDataStatoOcc(java.lang.String dataStatoOcc) {
		this.dataStatoOcc = dataStatoOcc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GetInfoLavoratoreResponse))
			return false;
		GetInfoLavoratoreResponse other = (GetInfoLavoratoreResponse) obj;
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
				&& ((this.errore == null && other.getErrore() == null)
						|| (this.errore != null && this.errore.equals(other.getErrore())))
				&& ((this.cognome == null && other.getCognome() == null)
						|| (this.cognome != null && this.cognome.equals(other.getCognome())))
				&& ((this.nome == null && other.getNome() == null)
						|| (this.nome != null && this.nome.equals(other.getNome())))
				&& ((this.cf == null && other.getCf() == null) || (this.cf != null && this.cf.equals(other.getCf())))
				&& ((this.dataNascita == null && other.getDataNascita() == null)
						|| (this.dataNascita != null && this.dataNascita.equals(other.getDataNascita())))
				&& ((this.statoOcc == null && other.getStatoOcc() == null)
						|| (this.statoOcc != null && this.statoOcc.equals(other.getStatoOcc())))
				&& ((this.dataStatoOcc == null && other.getDataStatoOcc() == null)
						|| (this.dataStatoOcc != null && this.dataStatoOcc.equals(other.getDataStatoOcc())));
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
		if (getErrore() != null) {
			_hashCode += getErrore().hashCode();
		}
		if (getCognome() != null) {
			_hashCode += getCognome().hashCode();
		}
		if (getNome() != null) {
			_hashCode += getNome().hashCode();
		}
		if (getCf() != null) {
			_hashCode += getCf().hashCode();
		}
		if (getDataNascita() != null) {
			_hashCode += getDataNascita().hashCode();
		}
		if (getStatoOcc() != null) {
			_hashCode += getStatoOcc().hashCode();
		}
		if (getDataStatoOcc() != null) {
			_hashCode += getDataStatoOcc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			GetInfoLavoratoreResponse.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "getInfoLavoratoreResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("errore");
		elemField.setXmlName(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "errore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome");
		elemField.setXmlName(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome");
		elemField.setXmlName(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cf");
		elemField.setXmlName(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "cf"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "dataNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("statoOcc");
		elemField.setXmlName(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "statoOcc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataStatoOcc");
		elemField.setXmlName(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "dataStatoOcc"));
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
