/**
 * IndirizzoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common;

public class IndirizzoType implements java.io.Serializable {
	private java.lang.String indirizzo;

	private java.lang.String cap;

	private java.lang.String codiceComune;

	private java.lang.String codiceProvincia;

	private java.util.Date dataVariazioneResidenza;

	public IndirizzoType() {
	}

	public IndirizzoType(java.lang.String indirizzo, java.lang.String cap, java.lang.String codiceComune,
			java.lang.String codiceProvincia, java.util.Date dataVariazioneResidenza) {
		this.indirizzo = indirizzo;
		this.cap = cap;
		this.codiceComune = codiceComune;
		this.codiceProvincia = codiceProvincia;
		this.dataVariazioneResidenza = dataVariazioneResidenza;
	}

	/**
	 * Gets the indirizzo value for this IndirizzoType.
	 * 
	 * @return indirizzo
	 */
	public java.lang.String getIndirizzo() {
		return indirizzo;
	}

	/**
	 * Sets the indirizzo value for this IndirizzoType.
	 * 
	 * @param indirizzo
	 */
	public void setIndirizzo(java.lang.String indirizzo) {
		this.indirizzo = indirizzo;
	}

	/**
	 * Gets the cap value for this IndirizzoType.
	 * 
	 * @return cap
	 */
	public java.lang.String getCap() {
		return cap;
	}

	/**
	 * Sets the cap value for this IndirizzoType.
	 * 
	 * @param cap
	 */
	public void setCap(java.lang.String cap) {
		this.cap = cap;
	}

	/**
	 * Gets the codiceComune value for this IndirizzoType.
	 * 
	 * @return codiceComune
	 */
	public java.lang.String getCodiceComune() {
		return codiceComune;
	}

	/**
	 * Sets the codiceComune value for this IndirizzoType.
	 * 
	 * @param codiceComune
	 */
	public void setCodiceComune(java.lang.String codiceComune) {
		this.codiceComune = codiceComune;
	}

	/**
	 * Gets the codiceProvincia value for this IndirizzoType.
	 * 
	 * @return codiceProvincia
	 */
	public java.lang.String getCodiceProvincia() {
		return codiceProvincia;
	}

	/**
	 * Sets the codiceProvincia value for this IndirizzoType.
	 * 
	 * @param codiceProvincia
	 */
	public void setCodiceProvincia(java.lang.String codiceProvincia) {
		this.codiceProvincia = codiceProvincia;
	}

	/**
	 * Gets the dataVariazioneResidenza value for this IndirizzoType.
	 * 
	 * @return dataVariazioneResidenza
	 */
	public java.util.Date getDataVariazioneResidenza() {
		return dataVariazioneResidenza;
	}

	/**
	 * Sets the dataVariazioneResidenza value for this IndirizzoType.
	 * 
	 * @param dataVariazioneResidenza
	 */
	public void setDataVariazioneResidenza(java.util.Date dataVariazioneResidenza) {
		this.dataVariazioneResidenza = dataVariazioneResidenza;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof IndirizzoType))
			return false;
		IndirizzoType other = (IndirizzoType) obj;
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
				&& ((this.indirizzo == null && other.getIndirizzo() == null)
						|| (this.indirizzo != null && this.indirizzo.equals(other.getIndirizzo())))
				&& ((this.cap == null && other.getCap() == null)
						|| (this.cap != null && this.cap.equals(other.getCap())))
				&& ((this.codiceComune == null && other.getCodiceComune() == null)
						|| (this.codiceComune != null && this.codiceComune.equals(other.getCodiceComune())))
				&& ((this.codiceProvincia == null && other.getCodiceProvincia() == null)
						|| (this.codiceProvincia != null && this.codiceProvincia.equals(other.getCodiceProvincia())))
				&& ((this.dataVariazioneResidenza == null && other.getDataVariazioneResidenza() == null)
						|| (this.dataVariazioneResidenza != null
								&& this.dataVariazioneResidenza.equals(other.getDataVariazioneResidenza())));
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
		if (getIndirizzo() != null) {
			_hashCode += getIndirizzo().hashCode();
		}
		if (getCap() != null) {
			_hashCode += getCap().hashCode();
		}
		if (getCodiceComune() != null) {
			_hashCode += getCodiceComune().hashCode();
		}
		if (getCodiceProvincia() != null) {
			_hashCode += getCodiceProvincia().hashCode();
		}
		if (getDataVariazioneResidenza() != null) {
			_hashCode += getDataVariazioneResidenza().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			IndirizzoType.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "indirizzoType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indirizzo");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cap");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "cap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceComune");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "codiceComune"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceProvincia");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"codiceProvincia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataVariazioneResidenza");
		elemField.setXmlName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"dataVariazioneResidenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
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
