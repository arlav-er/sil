/**
 * ResidenzaType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class ResidenzaType implements java.io.Serializable {
	private java.lang.String comune;

	private java.lang.String indirizzo;

	private java.lang.String cap;

	public ResidenzaType() {
	}

	public ResidenzaType(java.lang.String comune, java.lang.String indirizzo, java.lang.String cap) {
		this.comune = comune;
		this.indirizzo = indirizzo;
		this.cap = cap;
	}

	/**
	 * Gets the comune value for this ResidenzaType.
	 * 
	 * @return comune
	 */
	public java.lang.String getComune() {
		return comune;
	}

	/**
	 * Sets the comune value for this ResidenzaType.
	 * 
	 * @param comune
	 */
	public void setComune(java.lang.String comune) {
		this.comune = comune;
	}

	/**
	 * Gets the indirizzo value for this ResidenzaType.
	 * 
	 * @return indirizzo
	 */
	public java.lang.String getIndirizzo() {
		return indirizzo;
	}

	/**
	 * Sets the indirizzo value for this ResidenzaType.
	 * 
	 * @param indirizzo
	 */
	public void setIndirizzo(java.lang.String indirizzo) {
		this.indirizzo = indirizzo;
	}

	/**
	 * Gets the cap value for this ResidenzaType.
	 * 
	 * @return cap
	 */
	public java.lang.String getCap() {
		return cap;
	}

	/**
	 * Sets the cap value for this ResidenzaType.
	 * 
	 * @param cap
	 */
	public void setCap(java.lang.String cap) {
		this.cap = cap;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ResidenzaType))
			return false;
		ResidenzaType other = (ResidenzaType) obj;
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
				&& ((this.comune == null && other.getComune() == null)
						|| (this.comune != null && this.comune.equals(other.getComune())))
				&& ((this.indirizzo == null && other.getIndirizzo() == null)
						|| (this.indirizzo != null && this.indirizzo.equals(other.getIndirizzo())))
				&& ((this.cap == null && other.getCap() == null)
						|| (this.cap != null && this.cap.equals(other.getCap())));
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
		if (getComune() != null) {
			_hashCode += getComune().hashCode();
		}
		if (getIndirizzo() != null) {
			_hashCode += getIndirizzo().hashCode();
		}
		if (getCap() != null) {
			_hashCode += getCap().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ResidenzaType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"ResidenzaType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("comune");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "comune"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indirizzo");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "Indirizzo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cap");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "cap"));
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
