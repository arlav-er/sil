/**
 * PattoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.pattoonline;

public class PattoType implements java.io.Serializable {
	private java.lang.String codiceFiscale;

	private java.util.Date dataPatto;

	private java.lang.String codServiziAmministrativi;

	private java.lang.String numProtocollo;

	private java.math.BigInteger annoProtocollo;

	private java.lang.String codProvinciaProv;

	public PattoType() {
	}

	public PattoType(java.lang.String codiceFiscale, java.util.Date dataPatto,
			java.lang.String codServiziAmministrativi, java.lang.String numProtocollo,
			java.math.BigInteger annoProtocollo, java.lang.String codProvinciaProv) {
		this.codiceFiscale = codiceFiscale;
		this.dataPatto = dataPatto;
		this.codServiziAmministrativi = codServiziAmministrativi;
		this.numProtocollo = numProtocollo;
		this.annoProtocollo = annoProtocollo;
		this.codProvinciaProv = codProvinciaProv;
	}

	/**
	 * Gets the codiceFiscale value for this PattoType.
	 * 
	 * @return codiceFiscale
	 */
	public java.lang.String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this PattoType.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(java.lang.String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the dataPatto value for this PattoType.
	 * 
	 * @return dataPatto
	 */
	public java.util.Date getDataPatto() {
		return dataPatto;
	}

	/**
	 * Sets the dataPatto value for this PattoType.
	 * 
	 * @param dataPatto
	 */
	public void setDataPatto(java.util.Date dataPatto) {
		this.dataPatto = dataPatto;
	}

	/**
	 * Gets the codServiziAmministrativi value for this PattoType.
	 * 
	 * @return codServiziAmministrativi
	 */
	public java.lang.String getCodServiziAmministrativi() {
		return codServiziAmministrativi;
	}

	/**
	 * Sets the codServiziAmministrativi value for this PattoType.
	 * 
	 * @param codServiziAmministrativi
	 */
	public void setCodServiziAmministrativi(java.lang.String codServiziAmministrativi) {
		this.codServiziAmministrativi = codServiziAmministrativi;
	}

	/**
	 * Gets the numProtocollo value for this PattoType.
	 * 
	 * @return numProtocollo
	 */
	public java.lang.String getNumProtocollo() {
		return numProtocollo;
	}

	/**
	 * Sets the numProtocollo value for this PattoType.
	 * 
	 * @param numProtocollo
	 */
	public void setNumProtocollo(java.lang.String numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	/**
	 * Gets the annoProtocollo value for this PattoType.
	 * 
	 * @return annoProtocollo
	 */
	public java.math.BigInteger getAnnoProtocollo() {
		return annoProtocollo;
	}

	/**
	 * Sets the annoProtocollo value for this PattoType.
	 * 
	 * @param annoProtocollo
	 */
	public void setAnnoProtocollo(java.math.BigInteger annoProtocollo) {
		this.annoProtocollo = annoProtocollo;
	}

	/**
	 * Gets the codProvinciaProv value for this PattoType.
	 * 
	 * @return codProvinciaProv
	 */
	public java.lang.String getCodProvinciaProv() {
		return codProvinciaProv;
	}

	/**
	 * Sets the codProvinciaProv value for this PattoType.
	 * 
	 * @param codProvinciaProv
	 */
	public void setCodProvinciaProv(java.lang.String codProvinciaProv) {
		this.codProvinciaProv = codProvinciaProv;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof PattoType))
			return false;
		PattoType other = (PattoType) obj;
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
				&& ((this.codiceFiscale == null && other.getCodiceFiscale() == null)
						|| (this.codiceFiscale != null && this.codiceFiscale.equals(other.getCodiceFiscale())))
				&& ((this.dataPatto == null && other.getDataPatto() == null)
						|| (this.dataPatto != null && this.dataPatto.equals(other.getDataPatto())))
				&& ((this.codServiziAmministrativi == null && other.getCodServiziAmministrativi() == null)
						|| (this.codServiziAmministrativi != null
								&& this.codServiziAmministrativi.equals(other.getCodServiziAmministrativi())))
				&& ((this.numProtocollo == null && other.getNumProtocollo() == null)
						|| (this.numProtocollo != null && this.numProtocollo.equals(other.getNumProtocollo())))
				&& ((this.annoProtocollo == null && other.getAnnoProtocollo() == null)
						|| (this.annoProtocollo != null && this.annoProtocollo.equals(other.getAnnoProtocollo())))
				&& ((this.codProvinciaProv == null && other.getCodProvinciaProv() == null)
						|| (this.codProvinciaProv != null
								&& this.codProvinciaProv.equals(other.getCodProvinciaProv())));
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
		if (getCodiceFiscale() != null) {
			_hashCode += getCodiceFiscale().hashCode();
		}
		if (getDataPatto() != null) {
			_hashCode += getDataPatto().hashCode();
		}
		if (getCodServiziAmministrativi() != null) {
			_hashCode += getCodServiziAmministrativi().hashCode();
		}
		if (getNumProtocollo() != null) {
			_hashCode += getNumProtocollo().hashCode();
		}
		if (getAnnoProtocollo() != null) {
			_hashCode += getAnnoProtocollo().hashCode();
		}
		if (getCodProvinciaProv() != null) {
			_hashCode += getCodProvinciaProv().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			PattoType.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "PattoType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataPatto");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "DataPatto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codServiziAmministrativi");
		elemField.setXmlName(new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/",
				"CodServiziAmministrativi"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("numProtocollo");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "NumProtocollo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("annoProtocollo");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "AnnoProtocollo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codProvinciaProv");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://pattoonline.webservices.coop.sil.eng.it/", "CodProvinciaProv"));
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
