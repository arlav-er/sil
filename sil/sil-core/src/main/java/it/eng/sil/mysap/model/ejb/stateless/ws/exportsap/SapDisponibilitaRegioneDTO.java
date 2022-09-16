/**
 * SapDisponibilitaRegioneDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapDisponibilitaRegioneDTO implements java.io.Serializable {
	private java.lang.String codRegione;

	private java.lang.String codRegioneDesc;

	public SapDisponibilitaRegioneDTO() {
	}

	public SapDisponibilitaRegioneDTO(java.lang.String codRegione, java.lang.String codRegioneDesc) {
		this.codRegione = codRegione;
		this.codRegioneDesc = codRegioneDesc;
	}

	/**
	 * Gets the codRegione value for this SapDisponibilitaRegioneDTO.
	 * 
	 * @return codRegione
	 */
	public java.lang.String getCodRegione() {
		return codRegione;
	}

	/**
	 * Sets the codRegione value for this SapDisponibilitaRegioneDTO.
	 * 
	 * @param codRegione
	 */
	public void setCodRegione(java.lang.String codRegione) {
		this.codRegione = codRegione;
	}

	/**
	 * Gets the codRegioneDesc value for this SapDisponibilitaRegioneDTO.
	 * 
	 * @return codRegioneDesc
	 */
	public java.lang.String getCodRegioneDesc() {
		return codRegioneDesc;
	}

	/**
	 * Sets the codRegioneDesc value for this SapDisponibilitaRegioneDTO.
	 * 
	 * @param codRegioneDesc
	 */
	public void setCodRegioneDesc(java.lang.String codRegioneDesc) {
		this.codRegioneDesc = codRegioneDesc;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapDisponibilitaRegioneDTO))
			return false;
		SapDisponibilitaRegioneDTO other = (SapDisponibilitaRegioneDTO) obj;
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
				&& ((this.codRegione == null && other.getCodRegione() == null)
						|| (this.codRegione != null && this.codRegione.equals(other.getCodRegione())))
				&& ((this.codRegioneDesc == null && other.getCodRegioneDesc() == null)
						|| (this.codRegioneDesc != null && this.codRegioneDesc.equals(other.getCodRegioneDesc())));
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
		if (getCodRegione() != null) {
			_hashCode += getCodRegione().hashCode();
		}
		if (getCodRegioneDesc() != null) {
			_hashCode += getCodRegioneDesc().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapDisponibilitaRegioneDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaRegioneDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codRegione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codRegione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codRegioneDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codRegioneDesc"));
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
