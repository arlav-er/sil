/**
 * IstanzaType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class IstanzaType implements java.io.Serializable {
	private java.util.Date datacandidatura;

	private int annoprotocollo;

	private java.lang.String protocollo;

	private java.lang.String idistanza;

	public IstanzaType() {
	}

	public IstanzaType(java.util.Date datacandidatura, int annoprotocollo, java.lang.String protocollo,
			java.lang.String idistanza) {
		this.datacandidatura = datacandidatura;
		this.annoprotocollo = annoprotocollo;
		this.protocollo = protocollo;
		this.idistanza = idistanza;
	}

	/**
	 * Gets the datacandidatura value for this IstanzaType.
	 * 
	 * @return datacandidatura
	 */
	public java.util.Date getDatacandidatura() {
		return datacandidatura;
	}

	/**
	 * Sets the datacandidatura value for this IstanzaType.
	 * 
	 * @param datacandidatura
	 */
	public void setDatacandidatura(java.util.Date datacandidatura) {
		this.datacandidatura = datacandidatura;
	}

	/**
	 * Gets the annoprotocollo value for this IstanzaType.
	 * 
	 * @return annoprotocollo
	 */
	public int getAnnoprotocollo() {
		return annoprotocollo;
	}

	/**
	 * Sets the annoprotocollo value for this IstanzaType.
	 * 
	 * @param annoprotocollo
	 */
	public void setAnnoprotocollo(int annoprotocollo) {
		this.annoprotocollo = annoprotocollo;
	}

	/**
	 * Gets the protocollo value for this IstanzaType.
	 * 
	 * @return protocollo
	 */
	public java.lang.String getProtocollo() {
		return protocollo;
	}

	/**
	 * Sets the protocollo value for this IstanzaType.
	 * 
	 * @param protocollo
	 */
	public void setProtocollo(java.lang.String protocollo) {
		this.protocollo = protocollo;
	}

	/**
	 * Gets the idistanza value for this IstanzaType.
	 * 
	 * @return idistanza
	 */
	public java.lang.String getIdistanza() {
		return idistanza;
	}

	/**
	 * Sets the idistanza value for this IstanzaType.
	 * 
	 * @param idistanza
	 */
	public void setIdistanza(java.lang.String idistanza) {
		this.idistanza = idistanza;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof IstanzaType))
			return false;
		IstanzaType other = (IstanzaType) obj;
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
				&& ((this.datacandidatura == null && other.getDatacandidatura() == null)
						|| (this.datacandidatura != null && this.datacandidatura.equals(other.getDatacandidatura())))
				&& this.annoprotocollo == other.getAnnoprotocollo()
				&& ((this.protocollo == null && other.getProtocollo() == null)
						|| (this.protocollo != null && this.protocollo.equals(other.getProtocollo())))
				&& ((this.idistanza == null && other.getIdistanza() == null)
						|| (this.idistanza != null && this.idistanza.equals(other.getIdistanza())));
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
		if (getDatacandidatura() != null) {
			_hashCode += getDatacandidatura().hashCode();
		}
		_hashCode += getAnnoprotocollo();
		if (getProtocollo() != null) {
			_hashCode += getProtocollo().hashCode();
		}
		if (getIdistanza() != null) {
			_hashCode += getIdistanza().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			IstanzaType.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "IstanzaType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datacandidatura");
		elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"datacandidatura"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("annoprotocollo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"annoprotocollo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("protocollo");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "protocollo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idistanza");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "idistanza"));
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
