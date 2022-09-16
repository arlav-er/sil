/**
 * AzioneConcordata.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

public class AzioneConcordata implements java.io.Serializable {
	private java.lang.String azione;

	private java.lang.String cpi;

	private java.util.Date dataColloquio;

	private java.util.Date dataStimata;

	private java.lang.String domicilio;

	private java.lang.String esito;

	private java.lang.String telefono;

	public AzioneConcordata() {
	}

	public AzioneConcordata(java.lang.String azione, java.lang.String cpi, java.util.Date dataColloquio,
			java.util.Date dataStimata, java.lang.String domicilio, java.lang.String esito, java.lang.String telefono) {
		this.azione = azione;
		this.cpi = cpi;
		this.dataColloquio = dataColloquio;
		this.dataStimata = dataStimata;
		this.domicilio = domicilio;
		this.esito = esito;
		this.telefono = telefono;
	}

	/**
	 * Gets the azione value for this AzioneConcordata.
	 * 
	 * @return azione
	 */
	public java.lang.String getAzione() {
		return azione;
	}

	/**
	 * Sets the azione value for this AzioneConcordata.
	 * 
	 * @param azione
	 */
	public void setAzione(java.lang.String azione) {
		this.azione = azione;
	}

	/**
	 * Gets the cpi value for this AzioneConcordata.
	 * 
	 * @return cpi
	 */
	public java.lang.String getCpi() {
		return cpi;
	}

	/**
	 * Sets the cpi value for this AzioneConcordata.
	 * 
	 * @param cpi
	 */
	public void setCpi(java.lang.String cpi) {
		this.cpi = cpi;
	}

	/**
	 * Gets the dataColloquio value for this AzioneConcordata.
	 * 
	 * @return dataColloquio
	 */
	public java.util.Date getDataColloquio() {
		return dataColloquio;
	}

	/**
	 * Sets the dataColloquio value for this AzioneConcordata.
	 * 
	 * @param dataColloquio
	 */
	public void setDataColloquio(java.util.Date dataColloquio) {
		this.dataColloquio = dataColloquio;
	}

	/**
	 * Gets the dataStimata value for this AzioneConcordata.
	 * 
	 * @return dataStimata
	 */
	public java.util.Date getDataStimata() {
		return dataStimata;
	}

	/**
	 * Sets the dataStimata value for this AzioneConcordata.
	 * 
	 * @param dataStimata
	 */
	public void setDataStimata(java.util.Date dataStimata) {
		this.dataStimata = dataStimata;
	}

	/**
	 * Gets the domicilio value for this AzioneConcordata.
	 * 
	 * @return domicilio
	 */
	public java.lang.String getDomicilio() {
		return domicilio;
	}

	/**
	 * Sets the domicilio value for this AzioneConcordata.
	 * 
	 * @param domicilio
	 */
	public void setDomicilio(java.lang.String domicilio) {
		this.domicilio = domicilio;
	}

	/**
	 * Gets the esito value for this AzioneConcordata.
	 * 
	 * @return esito
	 */
	public java.lang.String getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this AzioneConcordata.
	 * 
	 * @param esito
	 */
	public void setEsito(java.lang.String esito) {
		this.esito = esito;
	}

	/**
	 * Gets the telefono value for this AzioneConcordata.
	 * 
	 * @return telefono
	 */
	public java.lang.String getTelefono() {
		return telefono;
	}

	/**
	 * Sets the telefono value for this AzioneConcordata.
	 * 
	 * @param telefono
	 */
	public void setTelefono(java.lang.String telefono) {
		this.telefono = telefono;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof AzioneConcordata))
			return false;
		AzioneConcordata other = (AzioneConcordata) obj;
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
				&& ((this.azione == null && other.getAzione() == null)
						|| (this.azione != null && this.azione.equals(other.getAzione())))
				&& ((this.cpi == null && other.getCpi() == null)
						|| (this.cpi != null && this.cpi.equals(other.getCpi())))
				&& ((this.dataColloquio == null && other.getDataColloquio() == null)
						|| (this.dataColloquio != null && this.dataColloquio.equals(other.getDataColloquio())))
				&& ((this.dataStimata == null && other.getDataStimata() == null)
						|| (this.dataStimata != null && this.dataStimata.equals(other.getDataStimata())))
				&& ((this.domicilio == null && other.getDomicilio() == null)
						|| (this.domicilio != null && this.domicilio.equals(other.getDomicilio())))
				&& ((this.esito == null && other.getEsito() == null)
						|| (this.esito != null && this.esito.equals(other.getEsito())))
				&& ((this.telefono == null && other.getTelefono() == null)
						|| (this.telefono != null && this.telefono.equals(other.getTelefono())));
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
		if (getAzione() != null) {
			_hashCode += getAzione().hashCode();
		}
		if (getCpi() != null) {
			_hashCode += getCpi().hashCode();
		}
		if (getDataColloquio() != null) {
			_hashCode += getDataColloquio().hashCode();
		}
		if (getDataStimata() != null) {
			_hashCode += getDataStimata().hashCode();
		}
		if (getDomicilio() != null) {
			_hashCode += getDomicilio().hashCode();
		}
		if (getEsito() != null) {
			_hashCode += getEsito().hashCode();
		}
		if (getTelefono() != null) {
			_hashCode += getTelefono().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			AzioneConcordata.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "AzioneConcordata"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("azione");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "azione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cpi");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "cpi"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataColloquio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataColloquio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataStimata");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "dataStimata"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("domicilio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "domicilio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "esito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("telefono");
		elemField.setXmlName(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "telefono"));
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
