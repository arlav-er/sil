/**
 * EsitoComunicazioneType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common;

public class EsitoComunicazioneType implements java.io.Serializable {
	private java.lang.String codice;

	private java.lang.String descrizione;

	private it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.CategoriaEsitoType categoria;

	public EsitoComunicazioneType() {
	}

	public EsitoComunicazioneType(java.lang.String codice, java.lang.String descrizione,
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.CategoriaEsitoType categoria) {
		this.codice = codice;
		this.descrizione = descrizione;
		this.categoria = categoria;
	}

	/**
	 * Gets the codice value for this EsitoComunicazioneType.
	 * 
	 * @return codice
	 */
	public java.lang.String getCodice() {
		return codice;
	}

	/**
	 * Sets the codice value for this EsitoComunicazioneType.
	 * 
	 * @param codice
	 */
	public void setCodice(java.lang.String codice) {
		this.codice = codice;
	}

	/**
	 * Gets the descrizione value for this EsitoComunicazioneType.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this EsitoComunicazioneType.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the categoria value for this EsitoComunicazioneType.
	 * 
	 * @return categoria
	 */
	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.CategoriaEsitoType getCategoria() {
		return categoria;
	}

	/**
	 * Sets the categoria value for this EsitoComunicazioneType.
	 * 
	 * @param categoria
	 */
	public void setCategoria(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.CategoriaEsitoType categoria) {
		this.categoria = categoria;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof EsitoComunicazioneType))
			return false;
		EsitoComunicazioneType other = (EsitoComunicazioneType) obj;
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
				&& ((this.codice == null && other.getCodice() == null)
						|| (this.codice != null && this.codice.equals(other.getCodice())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.categoria == null && other.getCategoria() == null)
						|| (this.categoria != null && this.categoria.equals(other.getCategoria())));
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
		if (getCodice() != null) {
			_hashCode += getCodice().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getCategoria() != null) {
			_hashCode += getCategoria().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			EsitoComunicazioneType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"esitoComunicazioneType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codice");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "codice"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descrizione");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "descrizione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("categoria");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "categoria"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"categoriaEsitoType"));
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
