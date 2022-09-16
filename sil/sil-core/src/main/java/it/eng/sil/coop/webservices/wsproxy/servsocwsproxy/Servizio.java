/**
 * Servizio.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class Servizio implements java.io.Serializable {
	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema sistema;

	private java.lang.String soggettoCF;

	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiSosia SOSIA;

	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiOsservatorio OSSERVATORIO;

	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGradus GRADUS;

	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGarsia GARSIA;

	public Servizio() {
	}

	public Servizio(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema sistema, java.lang.String soggettoCF,
			it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiSosia SOSIA,
			it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiOsservatorio OSSERVATORIO,
			it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGradus GRADUS,
			it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGarsia GARSIA) {
		this.sistema = sistema;
		this.soggettoCF = soggettoCF;
		this.SOSIA = SOSIA;
		this.OSSERVATORIO = OSSERVATORIO;
		this.GRADUS = GRADUS;
		this.GARSIA = GARSIA;
	}

	/**
	 * Gets the sistema value for this Servizio.
	 * 
	 * @return sistema
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema getSistema() {
		return sistema;
	}

	/**
	 * Sets the sistema value for this Servizio.
	 * 
	 * @param sistema
	 */
	public void setSistema(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema sistema) {
		this.sistema = sistema;
	}

	/**
	 * Gets the soggettoCF value for this Servizio.
	 * 
	 * @return soggettoCF
	 */
	public java.lang.String getSoggettoCF() {
		return soggettoCF;
	}

	/**
	 * Sets the soggettoCF value for this Servizio.
	 * 
	 * @param soggettoCF
	 */
	public void setSoggettoCF(java.lang.String soggettoCF) {
		this.soggettoCF = soggettoCF;
	}

	/**
	 * Gets the SOSIA value for this Servizio.
	 * 
	 * @return SOSIA
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiSosia getSOSIA() {
		return SOSIA;
	}

	/**
	 * Sets the SOSIA value for this Servizio.
	 * 
	 * @param SOSIA
	 */
	public void setSOSIA(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiSosia SOSIA) {
		this.SOSIA = SOSIA;
	}

	/**
	 * Gets the OSSERVATORIO value for this Servizio.
	 * 
	 * @return OSSERVATORIO
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiOsservatorio getOSSERVATORIO() {
		return OSSERVATORIO;
	}

	/**
	 * Sets the OSSERVATORIO value for this Servizio.
	 * 
	 * @param OSSERVATORIO
	 */
	public void setOSSERVATORIO(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiOsservatorio OSSERVATORIO) {
		this.OSSERVATORIO = OSSERVATORIO;
	}

	/**
	 * Gets the GRADUS value for this Servizio.
	 * 
	 * @return GRADUS
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGradus getGRADUS() {
		return GRADUS;
	}

	/**
	 * Sets the GRADUS value for this Servizio.
	 * 
	 * @param GRADUS
	 */
	public void setGRADUS(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGradus GRADUS) {
		this.GRADUS = GRADUS;
	}

	/**
	 * Gets the GARSIA value for this Servizio.
	 * 
	 * @return GARSIA
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGarsia getGARSIA() {
		return GARSIA;
	}

	/**
	 * Sets the GARSIA value for this Servizio.
	 * 
	 * @param GARSIA
	 */
	public void setGARSIA(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiGarsia GARSIA) {
		this.GARSIA = GARSIA;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Servizio))
			return false;
		Servizio other = (Servizio) obj;
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
				&& ((this.sistema == null && other.getSistema() == null)
						|| (this.sistema != null && this.sistema.equals(other.getSistema())))
				&& ((this.soggettoCF == null && other.getSoggettoCF() == null)
						|| (this.soggettoCF != null && this.soggettoCF.equals(other.getSoggettoCF())))
				&& ((this.SOSIA == null && other.getSOSIA() == null)
						|| (this.SOSIA != null && this.SOSIA.equals(other.getSOSIA())))
				&& ((this.OSSERVATORIO == null && other.getOSSERVATORIO() == null)
						|| (this.OSSERVATORIO != null && this.OSSERVATORIO.equals(other.getOSSERVATORIO())))
				&& ((this.GRADUS == null && other.getGRADUS() == null)
						|| (this.GRADUS != null && this.GRADUS.equals(other.getGRADUS())))
				&& ((this.GARSIA == null && other.getGARSIA() == null)
						|| (this.GARSIA != null && this.GARSIA.equals(other.getGARSIA())));
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
		if (getSistema() != null) {
			_hashCode += getSistema().hashCode();
		}
		if (getSoggettoCF() != null) {
			_hashCode += getSoggettoCF().hashCode();
		}
		if (getSOSIA() != null) {
			_hashCode += getSOSIA().hashCode();
		}
		if (getOSSERVATORIO() != null) {
			_hashCode += getOSSERVATORIO().hashCode();
		}
		if (getGRADUS() != null) {
			_hashCode += getGRADUS().hashCode();
		}
		if (getGARSIA() != null) {
			_hashCode += getGARSIA().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Servizio.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "servizio"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sistema");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sistema"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "tipoSistema"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("soggettoCF");
		elemField.setXmlName(new javax.xml.namespace.QName("", "soggettoCF"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("SOSIA");
		elemField.setXmlName(new javax.xml.namespace.QName("", "SOSIA"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiSosia"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("OSSERVATORIO");
		elemField.setXmlName(new javax.xml.namespace.QName("", "OSSERVATORIO"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiOsservatorio"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("GRADUS");
		elemField.setXmlName(new javax.xml.namespace.QName("", "GRADUS"));
		elemField
				.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiGradus"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("GARSIA");
		elemField.setXmlName(new javax.xml.namespace.QName("", "GARSIA"));
		elemField
				.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiGarsia"));
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
