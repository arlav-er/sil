/**
 * CpiMasterLavoratoreBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.serviziLavoratore;

public class CpiMasterLavoratoreBean implements java.io.Serializable {
	private java.lang.String dataMaster;

	private java.lang.String codiceFiscale;

	private java.lang.String nome;

	private java.lang.String datNasc;

	private java.lang.String codTipoMaster;

	private java.lang.String codcomNas;

	private java.lang.String codCpiMaster;

	private java.lang.String codComNas;

	private java.lang.String cognome;

	public CpiMasterLavoratoreBean() {
	}

	public CpiMasterLavoratoreBean(java.lang.String dataMaster, java.lang.String codiceFiscale, java.lang.String nome,
			java.lang.String datNasc, java.lang.String codTipoMaster, java.lang.String codcomNas,
			java.lang.String codCpiMaster, java.lang.String codComNas, java.lang.String cognome) {
		this.dataMaster = dataMaster;
		this.codiceFiscale = codiceFiscale;
		this.nome = nome;
		this.datNasc = datNasc;
		this.codTipoMaster = codTipoMaster;
		this.codcomNas = codcomNas;
		this.codCpiMaster = codCpiMaster;
		this.codComNas = codComNas;
		this.cognome = cognome;
	}

	/**
	 * Gets the dataMaster value for this CpiMasterLavoratoreBean.
	 * 
	 * @return dataMaster
	 */
	public java.lang.String getDataMaster() {
		return dataMaster;
	}

	/**
	 * Sets the dataMaster value for this CpiMasterLavoratoreBean.
	 * 
	 * @param dataMaster
	 */
	public void setDataMaster(java.lang.String dataMaster) {
		this.dataMaster = dataMaster;
	}

	/**
	 * Gets the codiceFiscale value for this CpiMasterLavoratoreBean.
	 * 
	 * @return codiceFiscale
	 */
	public java.lang.String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this CpiMasterLavoratoreBean.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(java.lang.String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the nome value for this CpiMasterLavoratoreBean.
	 * 
	 * @return nome
	 */
	public java.lang.String getNome() {
		return nome;
	}

	/**
	 * Sets the nome value for this CpiMasterLavoratoreBean.
	 * 
	 * @param nome
	 */
	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	/**
	 * Gets the datNasc value for this CpiMasterLavoratoreBean.
	 * 
	 * @return datNasc
	 */
	public java.lang.String getDatNasc() {
		return datNasc;
	}

	/**
	 * Sets the datNasc value for this CpiMasterLavoratoreBean.
	 * 
	 * @param datNasc
	 */
	public void setDatNasc(java.lang.String datNasc) {
		this.datNasc = datNasc;
	}

	/**
	 * Gets the codTipoMaster value for this CpiMasterLavoratoreBean.
	 * 
	 * @return codTipoMaster
	 */
	public java.lang.String getCodTipoMaster() {
		return codTipoMaster;
	}

	/**
	 * Sets the codTipoMaster value for this CpiMasterLavoratoreBean.
	 * 
	 * @param codTipoMaster
	 */
	public void setCodTipoMaster(java.lang.String codTipoMaster) {
		this.codTipoMaster = codTipoMaster;
	}

	/**
	 * Gets the codcomNas value for this CpiMasterLavoratoreBean.
	 * 
	 * @return codcomNas
	 */
	public java.lang.String getCodcomNas() {
		return codcomNas;
	}

	/**
	 * Sets the codcomNas value for this CpiMasterLavoratoreBean.
	 * 
	 * @param codcomNas
	 */
	public void setCodcomNas(java.lang.String codcomNas) {
		this.codcomNas = codcomNas;
	}

	/**
	 * Gets the codCpiMaster value for this CpiMasterLavoratoreBean.
	 * 
	 * @return codCpiMaster
	 */
	public java.lang.String getCodCpiMaster() {
		return codCpiMaster;
	}

	/**
	 * Sets the codCpiMaster value for this CpiMasterLavoratoreBean.
	 * 
	 * @param codCpiMaster
	 */
	public void setCodCpiMaster(java.lang.String codCpiMaster) {
		this.codCpiMaster = codCpiMaster;
	}

	/**
	 * Gets the codComNas value for this CpiMasterLavoratoreBean.
	 * 
	 * @return codComNas
	 */
	public java.lang.String getCodComNas() {
		return codComNas;
	}

	/**
	 * Sets the codComNas value for this CpiMasterLavoratoreBean.
	 * 
	 * @param codComNas
	 */
	public void setCodComNas(java.lang.String codComNas) {
		this.codComNas = codComNas;
	}

	/**
	 * Gets the cognome value for this CpiMasterLavoratoreBean.
	 * 
	 * @return cognome
	 */
	public java.lang.String getCognome() {
		return cognome;
	}

	/**
	 * Sets the cognome value for this CpiMasterLavoratoreBean.
	 * 
	 * @param cognome
	 */
	public void setCognome(java.lang.String cognome) {
		this.cognome = cognome;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CpiMasterLavoratoreBean))
			return false;
		CpiMasterLavoratoreBean other = (CpiMasterLavoratoreBean) obj;
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
				&& ((this.dataMaster == null && other.getDataMaster() == null)
						|| (this.dataMaster != null && this.dataMaster.equals(other.getDataMaster())))
				&& ((this.codiceFiscale == null && other.getCodiceFiscale() == null)
						|| (this.codiceFiscale != null && this.codiceFiscale.equals(other.getCodiceFiscale())))
				&& ((this.nome == null && other.getNome() == null)
						|| (this.nome != null && this.nome.equals(other.getNome())))
				&& ((this.datNasc == null && other.getDatNasc() == null)
						|| (this.datNasc != null && this.datNasc.equals(other.getDatNasc())))
				&& ((this.codTipoMaster == null && other.getCodTipoMaster() == null)
						|| (this.codTipoMaster != null && this.codTipoMaster.equals(other.getCodTipoMaster())))
				&& ((this.codcomNas == null && other.getCodcomNas() == null)
						|| (this.codcomNas != null && this.codcomNas.equals(other.getCodcomNas())))
				&& ((this.codCpiMaster == null && other.getCodCpiMaster() == null)
						|| (this.codCpiMaster != null && this.codCpiMaster.equals(other.getCodCpiMaster())))
				&& ((this.codComNas == null && other.getCodComNas() == null)
						|| (this.codComNas != null && this.codComNas.equals(other.getCodComNas())))
				&& ((this.cognome == null && other.getCognome() == null)
						|| (this.cognome != null && this.cognome.equals(other.getCognome())));
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
		if (getDataMaster() != null) {
			_hashCode += getDataMaster().hashCode();
		}
		if (getCodiceFiscale() != null) {
			_hashCode += getCodiceFiscale().hashCode();
		}
		if (getNome() != null) {
			_hashCode += getNome().hashCode();
		}
		if (getDatNasc() != null) {
			_hashCode += getDatNasc().hashCode();
		}
		if (getCodTipoMaster() != null) {
			_hashCode += getCodTipoMaster().hashCode();
		}
		if (getCodcomNas() != null) {
			_hashCode += getCodcomNas().hashCode();
		}
		if (getCodCpiMaster() != null) {
			_hashCode += getCodCpiMaster().hashCode();
		}
		if (getCodComNas() != null) {
			_hashCode += getCodComNas().hashCode();
		}
		if (getCognome() != null) {
			_hashCode += getCognome().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			CpiMasterLavoratoreBean.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://serviziLavoratore.wsClient.coop.sil.eng.it",
				"CpiMasterLavoratoreBean"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataMaster");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataMaster"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "nome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("datNasc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "datNasc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codTipoMaster");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codTipoMaster"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codcomNas");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codcomNas"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codCpiMaster");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codCpiMaster"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codComNas");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codComNas"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
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
