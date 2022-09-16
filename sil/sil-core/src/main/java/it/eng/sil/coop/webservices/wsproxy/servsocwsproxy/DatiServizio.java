/**
 * DatiServizio.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class DatiServizio implements java.io.Serializable {
	private java.lang.String criticita;

	private java.lang.String idCriticita;

	private java.lang.String relazione;

	public DatiServizio() {
	}

	public DatiServizio(java.lang.String criticita, java.lang.String idCriticita, java.lang.String relazione) {
		this.criticita = criticita;
		this.idCriticita = idCriticita;
		this.relazione = relazione;
	}

	/**
	 * Gets the criticita value for this DatiServizio.
	 * 
	 * @return criticita
	 */
	public java.lang.String getCriticita() {
		return criticita;
	}

	/**
	 * Sets the criticita value for this DatiServizio.
	 * 
	 * @param criticita
	 */
	public void setCriticita(java.lang.String criticita) {
		this.criticita = criticita;
	}

	/**
	 * Gets the idCriticita value for this DatiServizio.
	 * 
	 * @return idCriticita
	 */
	public java.lang.String getIdCriticita() {
		return idCriticita;
	}

	/**
	 * Sets the idCriticita value for this DatiServizio.
	 * 
	 * @param idCriticita
	 */
	public void setIdCriticita(java.lang.String idCriticita) {
		this.idCriticita = idCriticita;
	}

	/**
	 * Gets the relazione value for this DatiServizio.
	 * 
	 * @return relazione
	 */
	public java.lang.String getRelazione() {
		return relazione;
	}

	/**
	 * Sets the relazione value for this DatiServizio.
	 * 
	 * @param relazione
	 */
	public void setRelazione(java.lang.String relazione) {
		this.relazione = relazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DatiServizio))
			return false;
		DatiServizio other = (DatiServizio) obj;
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
				&& ((this.criticita == null && other.getCriticita() == null)
						|| (this.criticita != null && this.criticita.equals(other.getCriticita())))
				&& ((this.idCriticita == null && other.getIdCriticita() == null)
						|| (this.idCriticita != null && this.idCriticita.equals(other.getIdCriticita())))
				&& ((this.relazione == null && other.getRelazione() == null)
						|| (this.relazione != null && this.relazione.equals(other.getRelazione())));
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
		if (getCriticita() != null) {
			_hashCode += getCriticita().hashCode();
		}
		if (getIdCriticita() != null) {
			_hashCode += getIdCriticita().hashCode();
		}
		if (getRelazione() != null) {
			_hashCode += getRelazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DatiServizio.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiServizio"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("criticita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "criticita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idCriticita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idCriticita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("relazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "relazione"));
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
