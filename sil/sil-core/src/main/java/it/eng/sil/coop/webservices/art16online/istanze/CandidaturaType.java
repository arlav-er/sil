/**
 * CandidaturaType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class CandidaturaType implements java.io.Serializable {
	private it.eng.sil.coop.webservices.art16online.istanze.AnagraficaType anagrafica;

	private it.eng.sil.coop.webservices.art16online.istanze.ResidenzaType residenza;

	private it.eng.sil.coop.webservices.art16online.istanze.ContattiType contatti;

	private it.eng.sil.coop.webservices.art16online.istanze.ExtraUEType extraUE;

	private it.eng.sil.coop.webservices.art16online.istanze.ISEEType ISEE;

	private it.eng.sil.coop.webservices.art16online.istanze.IstanzaType istanza;

	private java.util.Date dataaggiornamento; // attribute

	public CandidaturaType() {
	}

	public CandidaturaType(it.eng.sil.coop.webservices.art16online.istanze.AnagraficaType anagrafica,
			it.eng.sil.coop.webservices.art16online.istanze.ResidenzaType residenza,
			it.eng.sil.coop.webservices.art16online.istanze.ContattiType contatti,
			it.eng.sil.coop.webservices.art16online.istanze.ExtraUEType extraUE,
			it.eng.sil.coop.webservices.art16online.istanze.ISEEType ISEE,
			it.eng.sil.coop.webservices.art16online.istanze.IstanzaType istanza, java.util.Date dataaggiornamento) {
		this.anagrafica = anagrafica;
		this.residenza = residenza;
		this.contatti = contatti;
		this.extraUE = extraUE;
		this.ISEE = ISEE;
		this.istanza = istanza;
		this.dataaggiornamento = dataaggiornamento;
	}

	/**
	 * Gets the anagrafica value for this CandidaturaType.
	 * 
	 * @return anagrafica
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.AnagraficaType getAnagrafica() {
		return anagrafica;
	}

	/**
	 * Sets the anagrafica value for this CandidaturaType.
	 * 
	 * @param anagrafica
	 */
	public void setAnagrafica(it.eng.sil.coop.webservices.art16online.istanze.AnagraficaType anagrafica) {
		this.anagrafica = anagrafica;
	}

	/**
	 * Gets the residenza value for this CandidaturaType.
	 * 
	 * @return residenza
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.ResidenzaType getResidenza() {
		return residenza;
	}

	/**
	 * Sets the residenza value for this CandidaturaType.
	 * 
	 * @param residenza
	 */
	public void setResidenza(it.eng.sil.coop.webservices.art16online.istanze.ResidenzaType residenza) {
		this.residenza = residenza;
	}

	/**
	 * Gets the contatti value for this CandidaturaType.
	 * 
	 * @return contatti
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.ContattiType getContatti() {
		return contatti;
	}

	/**
	 * Sets the contatti value for this CandidaturaType.
	 * 
	 * @param contatti
	 */
	public void setContatti(it.eng.sil.coop.webservices.art16online.istanze.ContattiType contatti) {
		this.contatti = contatti;
	}

	/**
	 * Gets the extraUE value for this CandidaturaType.
	 * 
	 * @return extraUE
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.ExtraUEType getExtraUE() {
		return extraUE;
	}

	/**
	 * Sets the extraUE value for this CandidaturaType.
	 * 
	 * @param extraUE
	 */
	public void setExtraUE(it.eng.sil.coop.webservices.art16online.istanze.ExtraUEType extraUE) {
		this.extraUE = extraUE;
	}

	/**
	 * Gets the ISEE value for this CandidaturaType.
	 * 
	 * @return ISEE
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.ISEEType getISEE() {
		return ISEE;
	}

	/**
	 * Sets the ISEE value for this CandidaturaType.
	 * 
	 * @param ISEE
	 */
	public void setISEE(it.eng.sil.coop.webservices.art16online.istanze.ISEEType ISEE) {
		this.ISEE = ISEE;
	}

	/**
	 * Gets the istanza value for this CandidaturaType.
	 * 
	 * @return istanza
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.IstanzaType getIstanza() {
		return istanza;
	}

	/**
	 * Sets the istanza value for this CandidaturaType.
	 * 
	 * @param istanza
	 */
	public void setIstanza(it.eng.sil.coop.webservices.art16online.istanze.IstanzaType istanza) {
		this.istanza = istanza;
	}

	/**
	 * Gets the dataaggiornamento value for this CandidaturaType.
	 * 
	 * @return dataaggiornamento
	 */
	public java.util.Date getDataaggiornamento() {
		return dataaggiornamento;
	}

	/**
	 * Sets the dataaggiornamento value for this CandidaturaType.
	 * 
	 * @param dataaggiornamento
	 */
	public void setDataaggiornamento(java.util.Date dataaggiornamento) {
		this.dataaggiornamento = dataaggiornamento;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CandidaturaType))
			return false;
		CandidaturaType other = (CandidaturaType) obj;
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
				&& ((this.anagrafica == null && other.getAnagrafica() == null)
						|| (this.anagrafica != null && this.anagrafica.equals(other.getAnagrafica())))
				&& ((this.residenza == null && other.getResidenza() == null)
						|| (this.residenza != null && this.residenza.equals(other.getResidenza())))
				&& ((this.contatti == null && other.getContatti() == null)
						|| (this.contatti != null && this.contatti.equals(other.getContatti())))
				&& ((this.extraUE == null && other.getExtraUE() == null)
						|| (this.extraUE != null && this.extraUE.equals(other.getExtraUE())))
				&& ((this.ISEE == null && other.getISEE() == null)
						|| (this.ISEE != null && this.ISEE.equals(other.getISEE())))
				&& ((this.istanza == null && other.getIstanza() == null)
						|| (this.istanza != null && this.istanza.equals(other.getIstanza())))
				&& ((this.dataaggiornamento == null && other.getDataaggiornamento() == null)
						|| (this.dataaggiornamento != null
								&& this.dataaggiornamento.equals(other.getDataaggiornamento())));
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
		if (getAnagrafica() != null) {
			_hashCode += getAnagrafica().hashCode();
		}
		if (getResidenza() != null) {
			_hashCode += getResidenza().hashCode();
		}
		if (getContatti() != null) {
			_hashCode += getContatti().hashCode();
		}
		if (getExtraUE() != null) {
			_hashCode += getExtraUE().hashCode();
		}
		if (getISEE() != null) {
			_hashCode += getISEE().hashCode();
		}
		if (getIstanza() != null) {
			_hashCode += getIstanza().hashCode();
		}
		if (getDataaggiornamento() != null) {
			_hashCode += getDataaggiornamento().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			CandidaturaType.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"CandidaturaType"));
		org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
		attrField.setFieldName("dataaggiornamento");
		attrField.setXmlName(new javax.xml.namespace.QName("", "dataaggiornamento"));
		attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
		typeDesc.addFieldDesc(attrField);
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("anagrafica");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "Anagrafica"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"AnagraficaType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("residenza");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "Residenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"ResidenzaType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("contatti");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "Contatti"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"ContattiType"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("extraUE");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "ExtraUE"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "ExtraUEType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("ISEE");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "ISEE"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "ISEEType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("istanza");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "Istanza"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "IstanzaType"));
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
