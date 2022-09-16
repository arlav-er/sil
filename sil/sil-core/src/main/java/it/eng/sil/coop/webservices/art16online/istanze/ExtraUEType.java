/**
 * ExtraUEType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class ExtraUEType implements java.io.Serializable {
	private java.lang.String titolosoggiorno; // attribute

	private java.lang.String numerotitolosogg; // attribute

	private java.lang.String motivopermesso; // attribute

	private java.util.Date scadenzatitolosogg; // attribute

	public ExtraUEType() {
	}

	public ExtraUEType(java.lang.String titolosoggiorno, java.lang.String numerotitolosogg,
			java.lang.String motivopermesso, java.util.Date scadenzatitolosogg) {
		this.titolosoggiorno = titolosoggiorno;
		this.numerotitolosogg = numerotitolosogg;
		this.motivopermesso = motivopermesso;
		this.scadenzatitolosogg = scadenzatitolosogg;
	}

	/**
	 * Gets the titolosoggiorno value for this ExtraUEType.
	 * 
	 * @return titolosoggiorno
	 */
	public java.lang.String getTitolosoggiorno() {
		return titolosoggiorno;
	}

	/**
	 * Sets the titolosoggiorno value for this ExtraUEType.
	 * 
	 * @param titolosoggiorno
	 */
	public void setTitolosoggiorno(java.lang.String titolosoggiorno) {
		this.titolosoggiorno = titolosoggiorno;
	}

	/**
	 * Gets the numerotitolosogg value for this ExtraUEType.
	 * 
	 * @return numerotitolosogg
	 */
	public java.lang.String getNumerotitolosogg() {
		return numerotitolosogg;
	}

	/**
	 * Sets the numerotitolosogg value for this ExtraUEType.
	 * 
	 * @param numerotitolosogg
	 */
	public void setNumerotitolosogg(java.lang.String numerotitolosogg) {
		this.numerotitolosogg = numerotitolosogg;
	}

	/**
	 * Gets the motivopermesso value for this ExtraUEType.
	 * 
	 * @return motivopermesso
	 */
	public java.lang.String getMotivopermesso() {
		return motivopermesso;
	}

	/**
	 * Sets the motivopermesso value for this ExtraUEType.
	 * 
	 * @param motivopermesso
	 */
	public void setMotivopermesso(java.lang.String motivopermesso) {
		this.motivopermesso = motivopermesso;
	}

	/**
	 * Gets the scadenzatitolosogg value for this ExtraUEType.
	 * 
	 * @return scadenzatitolosogg
	 */
	public java.util.Date getScadenzatitolosogg() {
		return scadenzatitolosogg;
	}

	/**
	 * Sets the scadenzatitolosogg value for this ExtraUEType.
	 * 
	 * @param scadenzatitolosogg
	 */
	public void setScadenzatitolosogg(java.util.Date scadenzatitolosogg) {
		this.scadenzatitolosogg = scadenzatitolosogg;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ExtraUEType))
			return false;
		ExtraUEType other = (ExtraUEType) obj;
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
				&& ((this.titolosoggiorno == null && other.getTitolosoggiorno() == null)
						|| (this.titolosoggiorno != null && this.titolosoggiorno.equals(other.getTitolosoggiorno())))
				&& ((this.numerotitolosogg == null && other.getNumerotitolosogg() == null)
						|| (this.numerotitolosogg != null && this.numerotitolosogg.equals(other.getNumerotitolosogg())))
				&& ((this.motivopermesso == null && other.getMotivopermesso() == null)
						|| (this.motivopermesso != null && this.motivopermesso.equals(other.getMotivopermesso())))
				&& ((this.scadenzatitolosogg == null && other.getScadenzatitolosogg() == null)
						|| (this.scadenzatitolosogg != null
								&& this.scadenzatitolosogg.equals(other.getScadenzatitolosogg())));
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
		if (getTitolosoggiorno() != null) {
			_hashCode += getTitolosoggiorno().hashCode();
		}
		if (getNumerotitolosogg() != null) {
			_hashCode += getNumerotitolosogg().hashCode();
		}
		if (getMotivopermesso() != null) {
			_hashCode += getMotivopermesso().hashCode();
		}
		if (getScadenzatitolosogg() != null) {
			_hashCode += getScadenzatitolosogg().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ExtraUEType.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "ExtraUEType"));
		org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("titolosoggiorno");
		attrField.setXmlName(new javax.xml.namespace.QName("", "titolosoggiorno"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				">ExtraUEType>titolosoggiorno"));
		typeDesc.addFieldDesc(attrField);
		attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("numerotitolosogg");
		attrField.setXmlName(new javax.xml.namespace.QName("", "numerotitolosogg"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				">ExtraUEType>numerotitolosogg"));
		typeDesc.addFieldDesc(attrField);
		attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("motivopermesso");
		attrField.setXmlName(new javax.xml.namespace.QName("", "motivopermesso"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				">ExtraUEType>motivopermesso"));
		typeDesc.addFieldDesc(attrField);
		attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("scadenzatitolosogg");
		attrField.setXmlName(new javax.xml.namespace.QName("", "scadenzatitolosogg"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
		typeDesc.addFieldDesc(attrField);
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
