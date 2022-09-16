/**
 * DeModalitaLavoro.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeModalitaLavoro extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codModalitaLavoro;

	private java.lang.String descrizione;

	public DeModalitaLavoro() {
	}

	public DeModalitaLavoro(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal,
			java.lang.String codModalitaLavoro, java.lang.String descrizione) {
		super(dtFineVal, dtInizioVal);
		this.codModalitaLavoro = codModalitaLavoro;
		this.descrizione = descrizione;
	}

	/**
	 * Gets the codModalitaLavoro value for this DeModalitaLavoro.
	 * 
	 * @return codModalitaLavoro
	 */
	public java.lang.String getCodModalitaLavoro() {
		return codModalitaLavoro;
	}

	/**
	 * Sets the codModalitaLavoro value for this DeModalitaLavoro.
	 * 
	 * @param codModalitaLavoro
	 */
	public void setCodModalitaLavoro(java.lang.String codModalitaLavoro) {
		this.codModalitaLavoro = codModalitaLavoro;
	}

	/**
	 * Gets the descrizione value for this DeModalitaLavoro.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeModalitaLavoro.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeModalitaLavoro))
			return false;
		DeModalitaLavoro other = (DeModalitaLavoro) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj) && ((this.codModalitaLavoro == null && other.getCodModalitaLavoro() == null)
				|| (this.codModalitaLavoro != null && this.codModalitaLavoro.equals(other.getCodModalitaLavoro())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = super.hashCode();
		if (getCodModalitaLavoro() != null) {
			_hashCode += getCodModalitaLavoro().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeModalitaLavoro.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deModalitaLavoro"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codModalitaLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codModalitaLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descrizione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descrizione"));
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
